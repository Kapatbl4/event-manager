package vv.dev.event_manager.user.model;

import vv.dev.event_manager.user.Role;

public class User {

    private long id;
    private String login;
    private String passwordHash;
    private int age;
    private Role role;

    public User(long id, String login, String passwordHash, int age, Role role) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.age = age;
        this.role = role;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
