package com.mes.controller;

import com.mes.common.Result;
import com.mes.dto.MaterialIssueDTO;
import com.mes.service.MaterialIssueService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MaterialIssueController {
    private final MaterialIssueService materialIssueService;

    public MaterialIssueController(MaterialIssueService materialIssueService) {
        this.materialIssueService = materialIssueService;
    }
    @PostMapping("/issue")
    public Result<Void> issue(@RequestBody MaterialIssueDTO materialIssueDTO) {
        materialIssueService.issue(materialIssueDTO);
        return Result.ok();
    }
}
