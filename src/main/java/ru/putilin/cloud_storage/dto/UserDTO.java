package ru.putilin.cloud_storage.dto;

public class UserDTO {
    private String username;
    private String password;

    public UserDTO(String name, String password) {
        this.username = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
