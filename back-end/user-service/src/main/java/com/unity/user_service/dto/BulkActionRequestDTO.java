package com.unity.user_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkActionRequestDTO {
    private List<Long> userIds;
    private Long adminId;
    private String action;
    private String comments;
}
