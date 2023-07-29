package com.project.requisition.entity;

import com.project.requisition.constants.RequisitionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "requisition")
public class RequisitionEntity {

    @Id
    @Column(name = "requisitionid", updatable = false, nullable = false)
    private UUID requisitionId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;

    @Column(name = "requesterid")
    private UUID requesterId;

    @Column(name = "teacherid")
    private UUID teacherId;

    @Column(name = "adminid")
    private UUID adminId;

    @Column(name = "teacher_approval_timestamp")
    private ZonedDateTime teacherApprovalTimestamp;

    @Column(name = "admin_approval_timestamp")
    private ZonedDateTime adminApprovalTimestamp;

    @Column(name = "lastoperatorid")
    private UUID lastOperatorId;

    @Column(name = "created_timestamp", nullable = false)
    private ZonedDateTime createdTimestamp;

    @Column(name = "lastupdated_timestamp", nullable = false)
    private ZonedDateTime lastUpdatedTimestamp;

}
