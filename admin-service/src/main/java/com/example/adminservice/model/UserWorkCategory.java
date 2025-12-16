package com.example.adminservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_work_categories", schema = "public")
@IdClass(UserWorkCategoryId.class)
@Getter
@Setter
public class UserWorkCategory {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "work_category")
    private WorkCategory workCategory;
}
