package com.project.requisition.service;

import com.project.requisition.constants.RequisitionStatus;
import com.project.requisition.entity.RequisitionEntity;
import com.project.requisition.exception.InvalidException;
import com.project.requisition.exception.NotFoundException;
import com.project.requisition.model.RequisitionRequest;
import com.project.requisition.repository.RequisitionRepository;
import com.project.requisition.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.project.requisition.constants.ErrorConstant.INVALID_STATUS;
import static com.project.requisition.constants.ErrorConstant.REQUISITION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final Clock systemClock;

    public List<RequisitionEntity> getAllRequisitions() {
        return requisitionRepository.findAll();
    }

    public RequisitionEntity getRequisitionById(String requisitionId) {
        return requisitionRepository.findById(UUID.fromString(requisitionId))
                .orElseThrow(() -> {
                    var err = String.format(REQUISITION_NOT_FOUND, requisitionId);
                    log.error("getRequisitionById, {}", err);
                    return new NotFoundException(err);
                });
    }

    public RequisitionEntity createNewRequisition(RequisitionRequest requisitionRequest) {
        var status = RequisitionStatus.valueOf(requisitionRequest.status().toUpperCase());
        if (!(RequisitionStatus.DRAFT.equals(status) || RequisitionStatus.PENDING_1.equals(status))) {
            log.error("createNewRequisition, Invalid status for new requisition: {} ", requisitionRequest.status());
            throw new InvalidException(String.format(INVALID_STATUS, requisitionRequest.status()));
        }

        var requisitionEntity = new RequisitionEntity();
        requisitionEntity.setRequisitionId(UUID.randomUUID());
        requisitionEntity.setSubject(requisitionRequest.subject());
        requisitionEntity.setDescription(requisitionRequest.description());
        requisitionEntity.setStatus(status);
        requisitionEntity.setRequesterId(UUID.fromString(requisitionRequest.requesterId()));
        requisitionEntity.setTeacherId(null);
        requisitionEntity.setAdminId(null);
        requisitionEntity.setLastOperatorId(UUID.fromString(requisitionRequest.lastOperatorId()));
        requisitionEntity.setCreatedTimestamp(ZonedDateTime.now(systemClock));
        requisitionEntity.setLastUpdatedTimestamp(ZonedDateTime.now(systemClock));

        return requisitionRepository.save(requisitionEntity);
    }

    public RequisitionEntity updateRequisition(String requisitionId, RequisitionRequest requisitionRequest) {
        var currentDateTime = ZonedDateTime.now(systemClock);
        var requisitionEntity = requisitionRepository.findById(UUID.fromString(requisitionId))
                .orElseThrow(() -> {
                    var err = String.format(REQUISITION_NOT_FOUND, requisitionId);
                    log.error("updateRequisition, {}", err);
                    return new NotFoundException(err);
                });

        var teacherId = CommonUtils.getUUIDFromString(requisitionRequest.teacherId());
        if (teacherId != null) {
            requisitionEntity.setTeacherApprovalTimestamp(currentDateTime);
        }

        var adminId = CommonUtils.getUUIDFromString(requisitionRequest.adminId());
        if (adminId != null) {
            requisitionEntity.setAdminApprovalTimestamp(currentDateTime);
        }

        requisitionEntity.setSubject(requisitionRequest.subject());
        requisitionEntity.setDescription(requisitionRequest.description());
        requisitionEntity.setStatus(RequisitionStatus.valueOf(requisitionRequest.status().toUpperCase()));
        requisitionEntity.setTeacherId(teacherId);
        requisitionEntity.setAdminId(adminId);
        requisitionEntity.setLastOperatorId(UUID.fromString(requisitionRequest.lastOperatorId()));
        requisitionEntity.setLastUpdatedTimestamp(currentDateTime);

        return requisitionRepository.save(requisitionEntity);
    }

    public void deleteRequisition(String requisitionId) {
        var requisitionEntity = requisitionRepository.findById(UUID.fromString(requisitionId))
                .orElseThrow(() -> {
                    var err = String.format(REQUISITION_NOT_FOUND, requisitionId);
                    log.error("deleteRequisition, {}", err);
                    return new NotFoundException(err);
                });
        requisitionRepository.delete(requisitionEntity);
    }

}
