package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.common.CommonUtil;
import com.common.MessageHttpResponse;
import com.model.ResponseDTO;
import com.service.ItemsService;
import com.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ItemsController {

	@Autowired 
	private ItemsService itemsService; 
	
	@Autowired
	MessageHttpResponse messageHttpResponse;

 	@GetMapping("/items")
 	@Operation(summary = "메뉴 조회", description = "메뉴 조회")
 	public ResponseEntity<?> getItems(@RequestParam("main") String main, @RequestParam("sub") String sub,
 			HttpServletRequest request,HttpSession session) throws Exception {
 		
    	List<Map<String, Object>> selectResponse = itemsService.getItems(main,sub);
    	
    	
        // 응답 DTO에 jwtToken 포함
        ResponseDTO responseDTO = new ResponseDTO.Builder()
                .setMessage("Success")
                .setStatusCode(200)
                .setResult(selectResponse)
                .build();
        
        return messageHttpResponse.success(responseDTO);
 			
 	}
 	
 	@GetMapping("/itemDetail")
 	@Operation(summary = "메뉴 조회", description = "메뉴 조회")
 	public ResponseEntity<?> getItemDetail(@RequestParam("itemsId") String itemsId,
 			HttpServletRequest request,HttpSession session) throws Exception {
 		
 		List<Map<String, Object>> selectResponse = itemsService.getItemDetail(itemsId);
 		
 		
 		// 응답 DTO에 jwtToken 포함
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage("Success")
 				.setStatusCode(200)
 				.setResult(selectResponse)
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
}
