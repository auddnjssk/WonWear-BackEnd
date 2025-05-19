package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.CommonUtil;
import com.common.utils.MessageHttpResponse;
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

 	@GetMapping("/cart")
 	@Operation(summary = "장바구니 조회", description = "장바구니 조회")
 	public ResponseEntity<?> getItems(@RequestParam("main") String main, @RequestParam("sub") String sub,
 			HttpServletRequest request,HttpSession session) throws Exception {
 		
    	List<Map<String, Object>> selectResponse = cartService.getItems(main,sub);
    	
    	
        // 응답 DTO에 jwtToken 포함
        ResponseDTO responseDTO = new ResponseDTO.Builder()
                .setMessage("Success")
                .setStatusCode(200)
                .setResult(selectResponse)
                .build();
        
        return messageHttpResponse.success(responseDTO);
 			
 	}
 	@PostMapping(value = "/cart", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "장바구니 추가", description = "장바구니 추가")
 	public ResponseEntity<?> createCart(@RequestBody List<CartDTO> requestBody) throws Exception {

 		ResponseEntity<String> response = cartService.createCart(requestBody);
 		
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
