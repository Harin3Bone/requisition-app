package com.project.requisition.service;

import com.project.requisition.enums.RequisitionStatus;
import com.project.requisition.entity.RequisitionEntity;
import com.project.requisition.entity.RequisitionItemEntity;
import com.project.requisition.exception.NotFoundException;
import com.project.requisition.model.request.RequisitionRequest;
import com.project.requisition.model.response.RequisitionResponse;
import com.project.requisition.repository.RequisitionRepository;
import com.project.requisition.utils.CommonUtils;
import com.project.requisition.utils.ValidateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.project.requisition.constants.ErrorMessage.REQUISITION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequisitionService {

    private final RequisitionItemService requisitionItemService;

    private final RequisitionRepository requisitionRepository;
    private final Clock systemClock;

    public List<RequisitionResponse> getAllRequisitions() {
        var requisitionList = new ArrayList<RequisitionResponse>();
        var requisitionEntityList = requisitionRepository.findAll();

        var requisitionIdList = requisitionEntityList.stream()
                .map(RequisitionEntity::getRequisitionId)
                .toList();
        var itemMap = requisitionItemService.getRequisitionItemByRequisitionId(requisitionIdList);

        for (var requisitionEntity : requisitionEntityList) {
            var itemList = itemMap.get(requisitionEntity.getRequisitionId()) == null
                    ? new ArrayList<RequisitionItemEntity>()
                    : itemMap.get(requisitionEntity.getRequisitionId());
            var requisitionResponse = new RequisitionResponse(requisitionEntity, itemList);
            requisitionList.add(requisitionResponse);
        }

        return requisitionList;
    }

    public RequisitionResponse getRequisitionById(String uuid) {
        var requisitionId = UUID.fromString(uuid);
        var requisitionEntity = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> {
                    var err = String.format(REQUISITION_NOT_FOUND, requisitionId);
                    log.error("getRequisitionById, {}", err);
                    return new NotFoundException(err);
                });
        var requisitionItem = requisitionItemService.getRequisitionItemByRequisitionId(requisitionId);

        return new RequisitionResponse(requisitionEntity, requisitionItem);
    }

    @Transactional
    public RequisitionResponse createNewRequisition(RequisitionRequest requisitionRequest) {
        var requisitionId = UUID.randomUUID();
        var operatorId = UUID.fromString(requisitionRequest.lastOperatorId());
        var currentDateTime = ZonedDateTime.now(systemClock);
        var status = RequisitionStatus.valueOf(requisitionRequest.status().toUpperCase());
        ValidateUtils.validateNewRequisitionItem(requisitionRequest, status);

        var requisitionEntity = new RequisitionEntity();
        requisitionEntity.setRequisitionId(requisitionId);
        requisitionEntity.setSubject(requisitionRequest.subject());
        requisitionEntity.setDescription(requisitionRequest.description());
        requisitionEntity.setStatus(status);
        requisitionEntity.setRequesterId(UUID.fromString(requisitionRequest.requesterId()));
        requisitionEntity.setTeacherId(null);
        requisitionEntity.setAdminId(null);
        requisitionEntity.setLastOperatorId(operatorId);
        requisitionEntity.setCreatedTimestamp(currentDateTime);
        requisitionEntity.setLastUpdatedTimestamp(currentDateTime);

        var saveEntity = requisitionRepository.save(requisitionEntity);
        var saveItemEntity = requisitionItemService.createRequisitionItem(requisitionId, operatorId, requisitionRequest.itemList(), currentDateTime);

        return new RequisitionResponse(saveEntity, saveItemEntity);
    }

    @Transactional
    public RequisitionResponse updateRequisition(String requisitionId, RequisitionRequest requisitionRequest) {
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

        requisitionRepository.save(requisitionEntity);
        if (!CommonUtils.isListNullOrEmpty(requisitionRequest.itemList())) {
            requisitionItemService.patchRequisitionItem(requisitionEntity.getRequisitionId(), requisitionEntity.getLastOperatorId(), requisitionRequest.itemList(), currentDateTime);
        }

        return getRequisitionById(requisitionId);
    }

    @Transactional
    public void deleteRequisition(String id) {
        var requisitionId = UUID.fromString(id);
        var requisitionEntity = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> {
                    var err = String.format(REQUISITION_NOT_FOUND, requisitionId);
                    log.error("deleteRequisition, {}", err);
                    return new NotFoundException(err);
                });
        requisitionRepository.delete(requisitionEntity);
        requisitionItemService.deleteRequisitionItemByRequisitionId(requisitionId);
    }

}
