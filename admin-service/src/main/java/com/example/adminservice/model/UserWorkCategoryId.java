package com.example.adminservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserWorkCategoryId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "work_category", nullable = false)
    private String workCategory;
}
