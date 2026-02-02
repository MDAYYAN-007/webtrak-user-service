package com.webtrak.user_service.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiStatus {
    private int code;
    private String label;
}

