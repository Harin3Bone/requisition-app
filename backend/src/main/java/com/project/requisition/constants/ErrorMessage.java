package com.project.requisition.constants;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
    public static final String INVALIDATE_ERROR = "Invalidate input data occurred";
    public static final String INVALID_STATUS = "Invalid status: %s";
    public static final String REQUISITION_NOT_FOUND = "Requisition not found for id: %s";

}
