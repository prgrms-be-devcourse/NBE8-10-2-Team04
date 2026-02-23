package com.back.global.exception;

import com.back.global.rsData.RsData;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;
    private final String resultCode;

    // ErrorCode만 사용하는 생성자
    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.resultCode=errorCode.getCode();
    }

    // 커스텀 메시지를 추가하는 생성자
    public ServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.resultCode=errorCode.getCode();
    }

    // 메시지 포맷팅을 지원하는 생성자
    public ServiceException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageWithArgs(args));
        this.errorCode = errorCode;
        this.customMessage = errorCode.getMessageWithArgs(args);
        this.resultCode=errorCode.getCode();
    }

    // 하위 호환성을 위한 생성자
    @Deprecated
    public ServiceException(String resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.errorCode = null;
        this.customMessage = msg;
        this.resultCode=resultCode;
    }

    public RsData<Void> getRsData() {
        if (errorCode != null) {
            String message = customMessage != null ? customMessage : errorCode.getMessage();
            return new RsData<>(errorCode.getCode(), message, null);
        }
        // 하드코딩된 500 대신 저장된 resultCode 반환
        return new RsData<>(resultCode != null ? resultCode : "500", customMessage, null);
    }

    public int getStatusCode() {
        if (errorCode != null) {
            return errorCode.getHttpStatus().value();
        }
        // 문자열 resultCode("401-1" 등)에서 앞 3자리만 파싱해 상태 코드로 반환
        if (resultCode != null && resultCode.length() >= 3) {
            try {
                return Integer.parseInt(resultCode.substring(0, 3));
            } catch (NumberFormatException e) {
                return 500;
            }
        }
        return 500;
    }
}