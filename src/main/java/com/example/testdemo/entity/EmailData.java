package com.example.testdemo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String email;

    public EmailData(User user, String email) {
        this.user = user;
        this.email = email;
    }
}
