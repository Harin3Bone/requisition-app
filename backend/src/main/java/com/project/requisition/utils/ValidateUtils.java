package com.project.requisition.utils;

import com.project.requisition.enums.RequisitionStatus;
import com.project.requisition.exception.InvalidException;
import com.project.requisition.model.request.RequisitionRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.project.requisition.constants.ErrorMessage.INVALID_STATUS;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidateUtils {

    public static void validateNewRequisitionItem(RequisitionRequest requisitionRequest, RequisitionStatus status) {
        if (!(RequisitionStatus.DRAFT.equals(status) || RequisitionStatus.PENDING_1.equals(status))) {
            log.error("validateNewRequisitionItem, Invalid status for new requisition: {} ", requisitionRequest.status());
            throw new InvalidException(String.format(INVALID_STATUS, requisitionRequest.status()));
        }

        if (requisitionRequest.itemList().isEmpty()) {
            log.error("validateNewRequisitionItem, Empty item list for new requisition");
            throw new InvalidException("Empty item list for new requisition");
        }
    }
}
