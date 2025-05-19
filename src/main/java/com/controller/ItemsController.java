package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.dto.ItemRequestDTO;
import com.dto.ResponseDTO;
import com.service.ItemsService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemsController {

	private final ItemsService itemsService; 
	private final CommonUtil comUtil; 
	private final MessageHttpResponse messageHttpResponse;

 	@GetMapping("/items")
 	@Operation(summary = "아이템 리스트 조회", description = "아이템 리스트 조회")
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
 	@Operation(summary = "아이템 디테일 조회", description = "아이템 디테일 조회")
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
 	
 	@PostMapping(value = "/itemDetail", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "아이템 디테일 추가", description = "아이템 디테일 추가")
 	public ResponseEntity<?> createItemDetail(@RequestBody Map<String, Object> requestBody) throws Exception {

 		ResponseEntity<String> response = itemsService.createItemDetail(requestBody);
 		
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
