package vv.dev.notificator.cache;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisChannelHandler;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

@Component
public class RedisConnectionWatcher implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(RedisConnectionWatcher.class);

    private final CacheManager cacheManager;

    public RedisConnectionWatcher(
            CacheManager cacheManager
    ) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof LettuceConnectionFactory factory) {
            try {
                AbstractRedisClient client = factory.getNativeClient();
                if (client instanceof RedisClient redisClient) {
                    redisClient.addListener(
                            new RedisConnectionStateListener() {
                                @Override
                                public void onRedisConnected(RedisChannelHandler<?, ?> connection, SocketAddress socketAddress) {
                                    log.warn("Redis connection established. Clearing caches to avoid stale data.");
                                    clearAllCaches();
                                }

                                @Override
                                public void onRedisDisconnected(RedisChannelHandler<?, ?> connection) {
                                    log.warn("Redis disconnected");
                                }

                                @Override
                                public void onRedisExceptionCaught(RedisChannelHandler<?, ?> connection, Throwable cause) {
                                    log.debug("Redis exception: {}", cause.getMessage());
                                }
                            }
                    );
                    log.info("RedisConnectionWatcher: listener registered on RedisClient.");
                } else {
                    log.warn("Native client is not RedisClient (may be cluster), skip listener registration.");
                }
            } catch (Exception e) {
                log.error("Failed to register Redis connection listener", e);
            }
        }
        return bean;
    }


    private void clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
                log.info("Cache '{}' cleared after Redis recovery", name);
            }
        });
    }
}
