package com.back.global.exception;

import com.back.global.rsData.RsData;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;

    // ErrorCode만 사용하는 생성자
    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    // 커스텀 메시지를 추가하는 생성자
    public ServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    // 메시지 포맷팅을 지원하는 생성자
    public ServiceException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageWithArgs(args));
        this.errorCode = errorCode;
        this.customMessage = errorCode.getMessageWithArgs(args);
    }

    // 하위 호환성을 위한 생성자
    @Deprecated
    public ServiceException(String resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.errorCode = null;
        this.customMessage = msg;
    }

    public RsData<Void> getRsData() {
        if (errorCode != null) {
            String message = customMessage != null ? customMessage : errorCode.getMessage();
            return new RsData<>(errorCode.getCode(), message, null);
        }
        // 하위 호환성을 위한 fallback
        return new RsData<>("500", customMessage, null);
    }

    public int getStatusCode() {
        return errorCode != null ? errorCode.getHttpStatus().value() : 500;
    }
}