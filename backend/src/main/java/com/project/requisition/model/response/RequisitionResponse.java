package com.project.requisition.model.response;

import com.project.requisition.entity.RequisitionEntity;
import com.project.requisition.entity.RequisitionItemEntity;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record RequisitionResponse(
        UUID requisitionId,
        String subject,
        String description,
        String status,
        UUID requesterId,
        UUID teacherId,
        UUID adminId,
        ZonedDateTime teacherApprovalTimestamp,
        ZonedDateTime adminApprovalTimestamp,
        List<RequisitionItemResponse> itemList,
        UUID lastOperatorId,
        ZonedDateTime createdTimestamp,
        ZonedDateTime lastUpdatedTimestamp
) {
    public RequisitionResponse(RequisitionEntity requisitionEntity, List<RequisitionItemEntity> itemEntityList) {
        this(
                requisitionEntity.getRequisitionId(),
                requisitionEntity.getSubject(),
                requisitionEntity.getDescription(),
                requisitionEntity.getStatus().name(),
                requisitionEntity.getRequesterId(),
                requisitionEntity.getTeacherId(),
                requisitionEntity.getAdminId(),
                requisitionEntity.getTeacherApprovalTimestamp(),
                requisitionEntity.getAdminApprovalTimestamp(),
                itemEntityList.stream().map(RequisitionItemResponse::new).toList(),
                requisitionEntity.getLastOperatorId(),
                requisitionEntity.getCreatedTimestamp(),
                requisitionEntity.getLastUpdatedTimestamp()
        );
    }
}
