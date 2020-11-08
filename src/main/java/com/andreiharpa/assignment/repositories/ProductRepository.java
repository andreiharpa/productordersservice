package com.andreiharpa.assignment.repositories;

import com.andreiharpa.assignment.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Product} entities
 *
 * @author Andrei Harpa
 *
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
}
