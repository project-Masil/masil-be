package com.masil.backend.controller;

import com.masil.backend.dto.request.CafeDto;
import com.masil.backend.entity.CafeInfo;
import com.masil.backend.service.CafeService;
import com.masil.backend.util.Formatter.SuccessCode;
import com.masil.backend.util.Formatter.ErrorCode;
import com.masil.backend.util.Formatter.ResponseBodyFormatter;
import com.masil.backend.util.Formatter.DataResponseBodyFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
public class CafeController {

	@Autowired
	private CafeService cafeService;

	@GetMapping
	public ResponseEntity<ResponseBodyFormatter> getAllCafes() {
		List<CafeInfo> cafes = cafeService.getAllCafes();
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, cafes);
	}

	@GetMapping("/info/{cafeId}")
	public ResponseEntity<ResponseBodyFormatter> getCafeById(@PathVariable Long cafeId) {
		CafeInfo cafe = cafeService.getCafeById(cafeId).orElse(null);
		if (cafe == null) {
			return ResponseBodyFormatter.init(ErrorCode.NOT_EXIST);
		}
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, cafe);
	}

	@GetMapping("/like/{userId}")
	public ResponseEntity<ResponseBodyFormatter> getLikedCafesByUserId(@PathVariable Long userId) {
		List<CafeInfo> likedCafes = cafeService.getLikedCafesByUserId(userId);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, likedCafes);
	}

	@GetMapping("/search")
	public ResponseEntity<ResponseBodyFormatter> searchCafes(@RequestParam String query) {
		List<CafeInfo> cafes = cafeService.searchCafes(query);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, cafes);
	}

	@PostMapping("/newplace")
	public ResponseEntity<ResponseBodyFormatter> createCafe(@RequestBody CafeDto cafeDTO) {
		CafeInfo cafe = cafeService.createCafe(cafeDTO);
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, cafe);
	}

	@GetMapping("/rec")
	public ResponseEntity<ResponseBodyFormatter> getRecommendedCafes() {
		List<CafeInfo> recommendedCafes = cafeService.getRecommendedCafes();
		return DataResponseBodyFormatter.init(SuccessCode.SUCCESS, recommendedCafes);
	}
}