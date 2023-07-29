package com.project.requisition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "requisition_item")
public class RequisitionItemEntity {

    @Id
    @Column(name = "requisition_itemid", updatable = false, nullable = false)
    private UUID requisitionItemId;

    @Column(name = "requisitionid", updatable = false, nullable = false)
    private UUID requisitionId;

    @Column(name = "itemid", updatable = false, nullable = false)
    private UUID itemId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "lastoperatorid")
    private UUID lastOperatorId;

    @Column(name = "created_timestamp", nullable = false)
    private ZonedDateTime createdTimestamp;

    @Column(name = "lastupdated_timestamp", nullable = false)
    private ZonedDateTime lastUpdatedTimestamp;

}
