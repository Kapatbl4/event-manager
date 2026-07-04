package vv.dev.event_manager.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @NotBlank(message = "Login cannot be empty")
        @Size(min = 5)
        String login,
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 5)
        String password,
        @NotNull(message = "Age cannot be empty")
        @Min(18)
        Integer age
) {
}
