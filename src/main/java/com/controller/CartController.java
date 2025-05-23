package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MessageHttpResponse;
import com.common.utils.ObjectUtil;
import com.dto.CartDTO;
import com.dto.ResponseDTO;
import com.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService; 
	private final CommonUtil comUtil; 
	private final MessageHttpResponse messageHttpResponse;
	private final JwtTokenUtil jwtTokenUtil;

 	@GetMapping("/cart")
 	@Operation(summary = "장바구니 조회", description = "장바구니 조회")
 	public ResponseEntity<?> getItems(@RequestHeader("Authorization") String authorization ) throws Exception {
 		
 		String userId      = null;
 		
 		List<Map<String,Object>>  response = null;
 		
 		if (authorization != null && authorization.startsWith("Bearer ")) {
 			userId = jwtTokenUtil.getUserIdFromStringToken(authorization);
 		}
 		
 	    if(ObjectUtil.isNotEmpty(userId)) {
 	    	response = cartService.getCart(userId);
 	    }else {
 	    	ResponseDTO responseDTO = new ResponseDTO.Builder()
	 				.setMessage("")
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
 	@PostMapping(value = "/cart", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "장바구니 추가", description = "장바구니 추가")
 	public ResponseEntity<?> createCart(@RequestHeader("Authorization") String authorization,
 			@RequestBody List<CartDTO> requestBody) throws Exception {
 		
 		String accessToken = null;
 		String userId      = null;
 		
 		ResponseEntity<String> response = null;
 		
 	    if (authorization != null && authorization.startsWith("Bearer ")) {
 	        accessToken = jwtTokenUtil.StringTk2Json(authorization.substring(7));
 	        userId = jwtTokenUtil.getUserIdFromToken(accessToken);
 	    }
 	    
 	    if(ObjectUtil.isNotEmpty(userId)) {
 	    	response = cartService.createCart(requestBody,userId);
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
 	@DeleteMapping("/cart/{itemId}/{itemsDetailId1}/{itemsDetailId2}")
 	@Operation(summary = "장바구니 삭제", description = "장바구니 삭제")
 	public ResponseEntity<?> deleteCart(@RequestHeader("Authorization") String authorization,
 			@PathVariable String itemId,@PathVariable String itemsDetailId1,@PathVariable String itemsDetailId2) throws Exception {
 		
 		String userId      = null;
 		
 		ResponseEntity<String>  response = null;
 		
 		if (authorization != null && authorization.startsWith("Bearer ")) {
 			userId = jwtTokenUtil.getUserIdFromStringToken(authorization);
 		}
 		
 		
 		if(ObjectUtil.isNotEmpty(userId)) {
 			response = cartService.deleteCart(itemId,itemsDetailId1,itemsDetailId2, userId);
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
