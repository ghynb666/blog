package com.blog.common;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private String requestId;
    private LocalDateTime timestamp;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ErrorCode.SUCCESS.getCode());
        result.setMessage(ErrorCode.SUCCESS.getMessage());
        result.setData(data);
        fillMeta(result);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(ErrorCode.INTERNAL_ERROR.getCode(), message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        fillMeta(result);
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    private static void fillMeta(Result<?> result) {
        String requestId = RequestContext.getRequestId();
        result.setRequestId(requestId == null ? UUID.randomUUID().toString() : requestId);
        result.setTimestamp(LocalDateTime.now());
    }
}
