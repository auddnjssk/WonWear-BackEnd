package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.CommonUtil;
import com.common.MessageHttpResponse;
import com.model.MenuRequest;
import com.model.ResponseDTO;
import com.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
// 	@PostMapping("/menu")
// 	public ResponseEntity<?> createMenu(HttpServletRequest request) throws IOException {
// 	    String rawJson = new BufferedReader(new InputStreamReader(request.getInputStream()))
// 	            .lines()
// 	            .collect(Collectors.joining("\n"));
// 	    
// 	    System.out.println("rawJson: " + rawJson);
// 	    return ResponseEntity.ok().build();
// 	}
 	
 	@PostMapping(value = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
 	@Operation(summary = "메뉴 조회", description = "메뉴 조회")
    public ResponseEntity<?> createMenu(@RequestBody MenuRequest rawJson) throws IOException {
 	
 		//List<Map<String, Object>> selectResponse = menuService.createMenu(requestBody);
 		
 		System.out.println("rawJson"+rawJson.toString());
 		// 응답 DTO에 jwtToken 포함
 		ResponseDTO responseDTO = new ResponseDTO.Builder()
 				.setMessage("loginSuccess")
 				.setStatusCode(200)
 				//.setResult(selectResponse)
 				.build();
 		
 		return messageHttpResponse.success(responseDTO);
 		
 	}
}
