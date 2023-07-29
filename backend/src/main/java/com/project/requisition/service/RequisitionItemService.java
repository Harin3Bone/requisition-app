package com.project.requisition.service;

import com.project.requisition.entity.RequisitionItemEntity;
import com.project.requisition.model.request.RequisitionItemRequest;
import com.project.requisition.repository.RequisitionItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequisitionItemService {

    private final RequisitionItemRepository requisitionItemRepository;

    @Transactional
    public void createRequisitionItem(UUID requisitionId, UUID operatorId, List<RequisitionItemRequest> itemList, ZonedDateTime currentDateTime) {
        var requisitionItemEntityList = new ArrayList<RequisitionItemEntity>();
        itemList.forEach(item -> {
            var requisitionItemEntity = populateRequisitionItemEntity(requisitionId, item, operatorId, currentDateTime);
            requisitionItemEntityList.add(requisitionItemEntity);
        });
        requisitionItemRepository.saveAll(requisitionItemEntityList);
    }

    @Transactional
    public void deleteRequisitionItemByRequisitionId(UUID requisitionId) {
        requisitionItemRepository.deleteByRequisitionId(requisitionId);
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
        updatedList.stream()
            .filter(updated -> existingList.stream().noneMatch(existing -> existing.getItemId().equals(UUID.fromString(updated.itemId()))))
            .forEach(item -> {
                if (item.quantity() == 0) {
                    return;
                }
                var requisitionItemEntity = populateRequisitionItemEntity(requisitionId, item, operatorId, currentDateTime);
                existingList.add(requisitionItemEntity);
            });

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
