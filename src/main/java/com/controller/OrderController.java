package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.dto.OrderDTO;
import com.dto.ResponseDTO;
import com.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService; 
	private final CommonUtil comUtil; 
	private final MessageHttpResponse messageHttpResponse;
	private final JwtTokenUtil jwtTokenUtil;

 	@GetMapping("/order")
 	@Operation(summary = "장바구니 조회", description = "장바구니 조회")
 	public ResponseEntity<?> getItems(@RequestHeader("Authorization") String authorization ) throws Exception {
 		
 		List<Map<String,Object>> response = orderService.getOrder();
 		
        // 응답 DTO에 jwtToken 포함
        ResponseDTO responseDTO = new ResponseDTO.Builder()
                .setMessage("Success")
                .setStatusCode(200)
                .setResult(response)
                .build();
        
        return messageHttpResponse.success(responseDTO);
 			
 	}
 	@PostMapping(value = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "주문 추가", description = "주문 추가")
 	public ResponseEntity<?> createCart(@RequestHeader("Authorization") String authorization,
 			@RequestBody OrderDTO requestBody) throws Exception {
 		
 		String accessToken = null;
 		String userId      = null;
 		
 		ResponseEntity<String> response = null;
 		
 	    if (authorization != null && authorization.startsWith("Bearer ")) {
 	        accessToken = jwtTokenUtil.StringTk2Json(authorization.substring(7));
 	        userId = jwtTokenUtil.getUserIdFromToken(accessToken);
 	    }
 	    
 	    if(ObjectUtil.isNotEmpty(userId)) {
 	    	response = orderService.createOrder(requestBody,userId);
 	    }else {
 	    	ResponseDTO responseDTO = new ResponseDTO.Builder()
	 				.setMessage("Token expired")
	 				.setStatusCode(401)
	 				.setResult(response)
	 				.build();
 			return messageHttpResponse.success(responseDTO);
 	    }
 	    
 		if(response.getStatusCode() == HttpStatus.OK) {
	 		ResponseDTO responseDTO = new ResponseDTO.Builder()
	 				.setMessage("Success")
	 				.setStatusCode(200)	
	 				.setResult(comUtil.parseJsonString(response.getBody()))
	 				.build();
	 		return messageHttpResponse.success(responseDTO);
 		}else {
 			ResponseDTO responseDTO = new ResponseDTO.Builder()
	 				.setMessage("failed")
	 				.setStatusCode(404)
	 				.setResult(response)
	 				.build();
 			return messageHttpResponse.success(responseDTO);
 		}
 	}
}
