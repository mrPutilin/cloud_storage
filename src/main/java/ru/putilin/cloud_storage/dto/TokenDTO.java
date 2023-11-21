package ru.putilin.cloud_storage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDTO {
    @JsonProperty("auth-token")
    private String authToken;

    public TokenDTO() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
