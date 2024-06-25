package ru.qaplayground.dto;

public class UserCreatedRequest {
    public String email;
    public String password;
    public String name;
    public String nickname;

    public UserCreatedRequest(String email, String password, String name, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }
}
