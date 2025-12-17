package com.example.adminservice.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_work_categories")
@Getter
@Setter
public class UserWorkCategory {

    @EmbeddedId
    private UserWorkCategoryId id;
}
