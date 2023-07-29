package com.project.requisition.controller;

import com.project.requisition.model.request.RequisitionRequest;
import com.project.requisition.model.response.RequisitionResponse;
import com.project.requisition.service.RequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requisition")
public class RequisitionController {

    private final RequisitionService requisitionService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<RequisitionResponse> getRequisition() {
        return requisitionService.getAllRequisitions();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RequisitionResponse getRequisitionByIdResponse(@PathVariable String id) {
        return requisitionService.getRequisitionById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public RequisitionResponse createRequisition(@RequestBody RequisitionRequest requisitionRequest) {
        return requisitionService.createNewRequisition(requisitionRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RequisitionResponse updateRequisition(@PathVariable String id, @RequestBody RequisitionRequest requisitionRequest) {
        return requisitionService.updateRequisition(id, requisitionRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequisition(@PathVariable String id) {
        requisitionService.deleteRequisition(id);
    }

}
