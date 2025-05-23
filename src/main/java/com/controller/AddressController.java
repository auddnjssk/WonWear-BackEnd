package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MessageHttpResponse;
import com.common.utils.ObjectUtil;
import com.dto.AddressDTO;
import com.dto.ResponseDTO;
import com.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService; 
	private final CommonUtil comUtil; 
	private final MessageHttpResponse messageHttpResponse;
	private final JwtTokenUtil jwtTokenUtil;

 	@GetMapping("/address")
 	@Operation(summary = "주소록 조회", description = "주소록 조회")
 	public ResponseEntity<?> getAddress(@RequestHeader("Authorization") String authorization ) throws Exception {
 		
 		String accessToken = null;
 		String userId      = null;
 		
 		List<Map<String,Object>> response = null;
 		
 	    if (authorization != null && authorization.startsWith("Bearer ")) {
 	        accessToken = jwtTokenUtil.StringTk2Json(authorization.substring(7));
 	        userId = jwtTokenUtil.getUserIdFromToken(accessToken);
 	    }
 	    
 	    if(ObjectUtil.isNotEmpty(userId)) {
 	    	response = addressService.getAddress(userId);
 	    }else {
 	    	ResponseDTO responseDTO = new ResponseDTO.Builder()
	 				.setMessage("Token expired")
	 				.setStatusCode(401)
	 				.setResult(response)
	 				.build();
 			return messageHttpResponse.success(responseDTO);
 	    }
        // 응답 DTO에 jwtToken 포함
        ResponseDTO responseDTO = new ResponseDTO.Builder()
                .setMessage("Success")
                .setStatusCode(200)
                .setResult(response)
                .build();
        
        return messageHttpResponse.success(responseDTO);
 			
 	}
 	
 	@PostMapping("/address")
 	@Operation(summary = "주소록 추가", description = "주소록 추가")
 	public ResponseEntity<?> createAddress(@RequestBody AddressDTO requestBody,    			
 			@RequestHeader("Authorization") String authorization ) throws Exception {
 		
 		String userId      = null;
 		
 		ResponseEntity<String>  response = null;
 		
 		if (authorization != null && authorization.startsWith("Bearer ")) {
 			userId = jwtTokenUtil.getUserIdFromStringToken(authorization);
 		}
 		
 		if(ObjectUtil.isNotEmpty(userId)) {
 			response = addressService.createAddress(requestBody,userId);
 		}else {
 			ResponseDTO responseDTO = new ResponseDTO.Builder()
 					.setMessage("Token expired")
 					.setStatusCode(401)
 					.setResult(response)
 					.build();
 			return messageHttpResponse.success(responseDTO);
 		}
 		// 응답 DTO에 jwtToken 포함
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage("Success")
 				.setStatusCode(200)
 				.setResult(response)
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
}
