package com.project.requisition.repository;

import com.project.requisition.entity.RequisitionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItemEntity, UUID> {

    List<RequisitionItemEntity> findByRequisitionId(UUID requisitionId);

    List<RequisitionItemEntity> findByRequisitionIdIn(List<UUID> requisitionId);

    void deleteByRequisitionId(UUID requisitionId);

    @Modifying
    @Query(value = "DELETE FROM requisition_item WHERE requisitionid = :requisitionId AND itemid IN (:itemId)", nativeQuery = true)
    void deleteByRequisitionId(@Param("requisitionId") UUID requisitionId, @Param("itemId") List<UUID> itemId);

}
