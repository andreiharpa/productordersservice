package com.andreiharpa.assignment.repositories;

import com.andreiharpa.assignment.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Order} entities
 *
 * @author Andrei Harpa
 *
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByTimestampBetween(Date startTime, Date endTime);
}
