package com.project.requisition.model.response;

import com.project.requisition.entity.RequisitionItemEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

public record RequisitionItemResponse(
        UUID requisitionItemId,
        UUID requisitionId,
        UUID itemId,
        int quantity,
        UUID lastOperatorId,
        ZonedDateTime createdTimestamp,
        ZonedDateTime lastUpdatedTimestamp
) {
    public RequisitionItemResponse(RequisitionItemEntity requisitionItemEntity) {
        this(
                requisitionItemEntity.getRequisitionItemId(),
                requisitionItemEntity.getRequisitionId(),
                requisitionItemEntity.getItemId(),
                requisitionItemEntity.getQuantity(),
                requisitionItemEntity.getLastOperatorId(),
                requisitionItemEntity.getCreatedTimestamp(),
                requisitionItemEntity.getLastUpdatedTimestamp()
        );
    }
}
