package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class MenuController {

	@Autowired 
	private CommonUtil comUtil; 
	

	@Autowired 
	private MenuService menuService; 
	
	@Autowired
	MessageHttpResponse messageHttpResponse;

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
}
