package ru.qaplayground.dto;

public class UserCreatedOrGetResponse {
    public String email;
    public String name;
    public String nickname;
    public String avatar_url;
    public String uuid;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getUuid() {
        return uuid;
    }
}
