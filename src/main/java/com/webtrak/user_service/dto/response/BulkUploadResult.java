package com.webtrak.user_service.dto.response;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
public class BulkUploadResult {
    private int totalRows;
    private int successCount;
    private int failureCount;
    private List<String> errors;
}
