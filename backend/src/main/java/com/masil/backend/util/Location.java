package com.masil.backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Location {
	ALL("전체"),

	GANGNAM_SEOCHO("강남/서초"),

	JAMSIL_SONGPA_GANGDONG("잠실/송파/강동"),

	YEONGDEUNGPO_YEOUIDO_GANGSEO("영등포/여의도/강서"),

	GEONDAE_SEONGSU_WANGSHIMNI("건대/성수/왕십리"),

	JONGNO_JUNGU("종로/중구"),

	HONGDAE_HAPJEONG_MAPO("홍대/합정/마포"),

	YONGSAN_ITAEWON_HANNAM("용산/이태원/한남"),

	SEONGBUK_NOWON_JUNGNANG("성북/노원/중랑"),

	GURO_GWANAK_DONGJAK("구로/관악/동작");

	private final String localName;

}
