package com.webtrak.user_service.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private ApiStatus status;
    private String message;
    private T data;

}
