package com.project.requisition.repository;

import com.project.requisition.entity.RequisitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RequisitionRepository extends JpaRepository<RequisitionEntity, UUID> {
}
