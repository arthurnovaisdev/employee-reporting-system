package com.mbfreire.employee_reporting.repository;

import com.mbfreire.employee_reporting.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
