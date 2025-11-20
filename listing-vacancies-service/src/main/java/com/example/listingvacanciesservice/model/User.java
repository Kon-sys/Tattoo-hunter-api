package com.example.listingvacanciesservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Company company;
}
