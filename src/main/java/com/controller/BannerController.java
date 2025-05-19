package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.MessageHttpResponse;
import com.dto.ResponseDTO;
import com.service.BannerService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class BannerController {
	
	@Autowired
	MessageHttpResponse messageHttpResponse;
	
	@Autowired
	BannerService bannerService;
	
 	@GetMapping("/mainBanner")
 	@Operation(summary = "메뉴 조회", description = "메뉴 조회")
 	public ResponseEntity<?> getMainBanner(HttpServletRequest request,HttpSession session){
 		
 		List<Map<String,Object>> selectResponse = bannerService.getBanner();
 		
 		
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage("Success")
 				.setStatusCode(200)
 				.setResult(selectResponse)
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
	
	
	
}
