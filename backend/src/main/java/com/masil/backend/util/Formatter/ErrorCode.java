package com.masil.backend.util.Formatter;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode implements ResponseCode {

    WRONG_JSON_FORMAT(HttpStatus.BAD_REQUEST, "JSON 에 지원하지 않는 키워드가 있습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 내역입니다."),
    NOT_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }


	@Override
	public String getCode() {
        return name();
    }

}
