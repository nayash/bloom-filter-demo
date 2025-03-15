package com.outliers.bloomfilter.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.outliers.bloomfilter.dto.request.UserRegistrationRequest;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private String username;
    private String email;
    // @Convert(converter = AttributeEncrypter.class) // you shouldn't save password in plain text
    private String password;

    public static User from(UserRegistrationRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
