package com.masil.backend.util.Formatter;

import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * ResponseEntity<> body 에 팀원 공통적인 포맷으로 기입하기 위한 클래스
 * init(ResponseCode responseCode, Object data) 사용 시
 * {
 *     "httpStatus" : int,
 *     "code" : "String",
 *     "message" : "String",
 *     "data" : {
 *         data
 *     }
 * }
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
public class DataResponseBodyFormatter extends ResponseBodyFormatter{
	private final Object data;

	/**
	 * @param responseCode ResponseCode 를 implements 한 enum
	 * @param data ResponseEntity 에 담아서 보낼 객체(Dto)
	 * @return Controller 에서 반환에 사용할 ResponseEntity
	 */
	public static ResponseEntity<ResponseBodyFormatter> init(ResponseCode responseCode, Object data){
		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(DataResponseBodyFormatter.builder()
				.httpStatus(responseCode.getHttpStatus().value())
				.code(responseCode.getCode())
				.message(responseCode.getMessage())
				.data(data)
				.build()
			);
	}

}
