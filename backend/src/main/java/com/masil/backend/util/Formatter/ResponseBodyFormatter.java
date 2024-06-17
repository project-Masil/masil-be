package com.masil.backend.util.Formatter;

import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * ResponseEntity<> body 에 팀원 공통적인 포맷으로 기입하기 위한 클래스
 * init(ResponseCode responseCode) 사용 시
 * {
 *     "httpStatus" : int,
 *     "code" : "String",
 *     "message" : "String"
 * }
 */
@Getter
@ToString
@SuperBuilder
public class ResponseBodyFormatter {

	private final int httpStatus;
	private final String code;
	private final String message;

	/**
	 * @param responseCode ResponseCode 를 implements 한 enum
	 * @return Controller 에서 반환에 사용할 ResponseEntity
	 */
	public static ResponseEntity<ResponseBodyFormatter> init(ResponseCode responseCode){
		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(ResponseBodyFormatter.builder()
				.httpStatus(responseCode.getHttpStatus().value())
				.code(responseCode.getCode())
				.message(responseCode.getMessage())
				.build()
			);
	}

}
