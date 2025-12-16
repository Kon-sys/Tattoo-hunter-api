package com.example.adminservice.model;

import java.io.Serializable;
import java.util.Objects;

public class UserWorkCategoryId implements Serializable {

    private Long userId;
    private WorkCategory workCategory;

    public UserWorkCategoryId() {}

    public UserWorkCategoryId(Long userId, WorkCategory workCategory) {
        this.userId = userId;
        this.workCategory = workCategory;
    }

    // equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserWorkCategoryId that)) return false;
        return Objects.equals(userId, that.userId)
                && workCategory == that.workCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, workCategory);
    }
}
