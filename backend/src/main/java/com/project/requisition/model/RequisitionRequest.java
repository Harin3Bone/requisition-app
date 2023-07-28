package com.project.requisition.model;

public record RequisitionRequest(
        String subject,
        String description,
        String status,
        String requesterId,
        String teacherId,
        String adminId,
        String lastOperatorId
) {}
