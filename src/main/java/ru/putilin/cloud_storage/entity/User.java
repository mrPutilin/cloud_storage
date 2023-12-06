package ru.putilin.cloud_storage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Size(min = 5, max = 20, message = "Имя должно быть от 2 до 20 знаков")
    @Column(name = "email", unique = true)
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
                ", username='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", tokens=" + tokens +
                '}';
    }
}
