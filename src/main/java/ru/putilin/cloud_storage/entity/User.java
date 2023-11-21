package ru.putilin.cloud_storage.entity;

import com.auth0.jwt.JWT;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 20, message = "Имя должно быть от 2 до 20 знаков")
    @Column(name = "username", unique = true)
    private String username;

    @NotEmpty
    @Size(min = 5, message = "Пароль должен содержать 5 и более символов")
    @Column(name = "password")
    private String password;

    @NotEmpty
    @Column(name = "role")
    private String role;

    @OneToOne(mappedBy = "user")
    private JWTToken tokens;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public JWTToken getTokens() {
        return tokens;
    }

    public void setTokens(JWTToken tokens) {
        this.tokens = tokens;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", tokens=" + tokens +
                '}';
    }
}
