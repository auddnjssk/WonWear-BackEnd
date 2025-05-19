package com.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.CommonUtil;
import com.common.utils.Constants;
import com.common.utils.MessageHttpResponse;
import com.dto.ResponseDTO;
import com.model.MenuRequest;
import com.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MenuController {

	private final CommonUtil comUtil; 
	private final MenuService menuService; 
	private final MessageHttpResponse messageHttpResponse;
	private Constants constants;
	

 	@GetMapping("/menu")
 	@Operation(summary = "메뉴 조회", description = "메뉴 조회")
 	public ResponseEntity<?> getMenu(HttpServletRequest request,HttpSession session) throws Exception {
 		
    	List<Map<String, Object>> selectResponse = menuService.getMenu();
    	
    	
        // 응답 DTO에 jwtToken 포함
        ResponseDTO responseDTO = new ResponseDTO.Builder()
                .setMessage("loginSuccess")
                .setStatusCode(200)
                .setResult(selectResponse)
                .build();
        
        return messageHttpResponse.success(responseDTO);
 	
 	}
 	
 	@PostMapping(value = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "메뉴 추가", description = "메뉴 추가")
    public ResponseEntity<?> createMenu(@RequestBody MenuRequest requestBody) throws IOException {
 	
 		ResponseEntity<String> selectResponse = menuService.createMenu(requestBody);
 		
 		// 응답 DTO에 jwtToken 포함
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage(constants.normalMessage)
 				.setStatusCode(200)
 				.setResult(comUtil.parseJsonString(selectResponse.getBody()))
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
 	
 	@DeleteMapping("/menu/{mainmenu_id}")
 	@Operation(summary = "메뉴 삭제", description = "메뉴 삭제")
 	public ResponseEntity<?> deleteMenu(@PathVariable Long mainmenu_id) throws IOException {
 		
 		ResponseEntity<String> selectResponse = menuService.deleteMenu(mainmenu_id);
 		
 		// 응답 DTO에 jwtToken 포함
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage(constants.normalMessage)
 				.setStatusCode(200)
 				.setResult(comUtil.parseJsonString(selectResponse.getBody()))
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
 	
 	
 	@PostMapping(value = "/subMenu", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "서브메뉴 추가", description = "서브메뉴 추가")
    public ResponseEntity<?> createSubMenu(@RequestBody MenuRequest requestBody) throws IOException {
 	
 		ResponseEntity<String> response = menuService.createSubMenu(requestBody);
 		
 		// 응답 DTO에 jwtToken 포함
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
 	@DeleteMapping(value = "/subMenu/{mainmenu_id}/{submenu_id}", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "서브메뉴 삭제", description = "서브메뉴 삭제")
 	public ResponseEntity<?> deleteSubMenu(@PathVariable("mainmenu_id") Long mainmenuId,@PathVariable("submenu_id") Long submenuId) throws IOException {
 		
 		ResponseEntity<String> response = menuService.deleteSubMenu(mainmenuId,submenuId);
 		
 		// 응답 DTO에 jwtToken 포함
 		if(response.getStatusCode() == HttpStatus.OK) {
 			ResponseDTO responseDTO = new ResponseDTO.Builder()
 					.setMessage("Success")
 					.setStatusCode(200)	
 					.setResult(response.getBody())
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
