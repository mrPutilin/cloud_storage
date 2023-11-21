package ru.putilin.cloud_storage.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class JWTToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tokens")
    private String authToken;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User user;

    public JWTToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setToken(String token) {
        this.authToken = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
