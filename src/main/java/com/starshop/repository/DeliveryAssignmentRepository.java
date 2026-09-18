package com.starshop.repository;

import com.starshop.domain.DeliveryAssignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** @owner: TV2 (shipper) dùng. */
public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {
    @EntityGraph(attributePaths = {"order", "order.store"})
    List<DeliveryAssignment> findByShipperId(Long shipperId);
    Optional<DeliveryAssignment> findByIdAndShipperId(Long id, Long shipperId);
}
