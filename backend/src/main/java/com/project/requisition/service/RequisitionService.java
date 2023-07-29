package com.project.requisition.service;

import com.project.requisition.constants.RequisitionStatus;
import com.project.requisition.entity.RequisitionEntity;
import com.project.requisition.entity.RequisitionItemEntity;
import com.project.requisition.exception.NotFoundException;
import com.project.requisition.model.request.RequisitionItemRequest;
import com.project.requisition.model.request.RequisitionRequest;
import com.project.requisition.repository.RequisitionItemRepository;
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

import static com.project.requisition.constants.ErrorConstant.REQUISITION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final RequisitionItemRepository requisitionItemRepository;
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

    @Transactional
    public RequisitionEntity createNewRequisition(RequisitionRequest requisitionRequest) {
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

        requisitionRepository.save(requisitionEntity);
        createRequisitionItem(requisitionId, operatorId, requisitionRequest.itemList(), currentDateTime);

        return requisitionEntity;
    }

    @Transactional
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

        requisitionRepository.save(requisitionEntity);
        if (!CommonUtils.isListNullOrEmpty(requisitionRequest.itemList())) {
            patchRequisitionItem(requisitionEntity.getRequisitionId(), requisitionEntity.getLastOperatorId(), requisitionRequest.itemList(), currentDateTime);
        }

        return requisitionEntity;
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
        requisitionItemRepository.deleteByRequisitionId(requisitionId);
    }

    private void createRequisitionItem(UUID requisitionId, UUID operatorId, List<RequisitionItemRequest> itemList, ZonedDateTime currentDateTime) {
        var requisitionItemEntityList = new ArrayList<RequisitionItemEntity>();
        itemList.forEach(item -> {
            var requisitionItemEntity = populateRequisitionItemEntity(requisitionId, item, operatorId, currentDateTime);
            requisitionItemEntityList.add(requisitionItemEntity);
        });
        requisitionItemRepository.saveAll(requisitionItemEntityList);
    }

    @Transactional
    public void patchRequisitionItem(UUID requisitionId, UUID operatorId, List<RequisitionItemRequest> updatedList, ZonedDateTime currentDateTime) {
        var existingList = requisitionItemRepository.findByRequisitionId(requisitionId);
        var deleteList = new ArrayList<UUID>();

        // Process update existing item when edit requisition
        for (var updated : updatedList) {
            for (var existing : existingList) {
                if (existing.getItemId().equals(UUID.fromString(updated.itemId()))) {
                    if (updated.quantity() == 0) {
                        // Process delete item when edit requisition
                        deleteList.add(existing.getItemId());
                    }
                    existing.setQuantity(updated.quantity());
                    existing.setLastOperatorId(operatorId);
                    existing.setLastUpdatedTimestamp(currentDateTime);
                }
            }
        }

        // Process add new item when edit requisition
        if (existingList.size() != updatedList.size()) {
            updatedList.stream()
                    .filter(updated -> existingList.stream().noneMatch(existing -> existing.getItemId().equals(UUID.fromString(updated.itemId()))))
                    .forEach(item -> {
                        var requisitionItemEntity = populateRequisitionItemEntity(requisitionId, item, operatorId, currentDateTime);
                        existingList.add(requisitionItemEntity);
                    });
        }

        requisitionItemRepository.saveAll(existingList);
        requisitionItemRepository.deleteByRequisitionId(requisitionId, deleteList);
    }

    private RequisitionItemEntity populateRequisitionItemEntity(
            UUID requisitionId, RequisitionItemRequest requisitionItemRequest, UUID operatorId, ZonedDateTime currentDateTime
    ) {
        var requisitionItemEntity = new RequisitionItemEntity();
        requisitionItemEntity.setRequisitionItemId(UUID.randomUUID());
        requisitionItemEntity.setRequisitionId(requisitionId);
        requisitionItemEntity.setItemId(UUID.fromString(requisitionItemRequest.itemId()));
        requisitionItemEntity.setQuantity(requisitionItemRequest.quantity());
        requisitionItemEntity.setLastOperatorId(operatorId);
        requisitionItemEntity.setCreatedTimestamp(currentDateTime);
        requisitionItemEntity.setLastUpdatedTimestamp(currentDateTime);
        return requisitionItemEntity;
    }

}
