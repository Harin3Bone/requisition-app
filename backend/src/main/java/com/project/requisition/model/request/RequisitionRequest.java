package com.project.requisition.model.request;

import java.util.List;

public record RequisitionRequest(
        String subject,
        String description,
        String status,
        String requesterId,
        String teacherId,
        String adminId,
        List<RequisitionItemRequest> itemList,
        String lastOperatorId
) {}
