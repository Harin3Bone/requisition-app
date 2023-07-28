package com.project.requisition.constants;

import lombok.Getter;

@Getter
public enum RequisitionStatus {

    DRAFT,
    PENDING_1,
    PENDING_2,
    APPROVE,
    REJECT,
    CANCEL;
}
