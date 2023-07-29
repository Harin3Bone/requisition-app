package com.project.requisition.model.request;

public record RequisitionItemRequest(
        String itemId,
        int quantity
) {}
