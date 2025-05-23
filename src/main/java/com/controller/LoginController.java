package com.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.common.utils.CommonUtil;
import com.common.utils.Constants;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MessageHttpResponse;
import com.common.utils.ObjectUtil;
import com.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.service.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LoginController {

	@Autowired 
	private LoginService loginService; 
	@Autowired 
	private JwtTokenUtil jwtTokenUtil; 
	@Autowired 
	private CommonUtil comUtil; 
	@Autowired
	MessageHttpResponse messageHttpResponse;


    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestBody,HttpServletRequest request,HttpServletResponse response) throws IOException {

        String accessToken ;
        
        try {

        	List<Map<String,Object>> selectResponse = loginService.loginChk(requestBody);
        	
        	// 회원가입 안되어있으면 return
            if(ObjectUtil.isEmpty(selectResponse) || selectResponse.size() <= 0) {

            	accessToken = null;
            	
	            // 응답 DTO에 jwtToken 포함
	            ResponseDTO responseDTO = new ResponseDTO.Builder()
	                    .setMessage("loginFailed")
	                    .setStatusCode(200)
	                    .setResult(selectResponse)
	                    .build();
	            
	            return messageHttpResponse.success(responseDTO);
	            
            }else {

            	accessToken = jwtTokenUtil.generateAccessToken(selectResponse.get(0));

                // RefreshToken은 Cookie에 저장 (보안상 이유) 
                String refreshToken = jwtTokenUtil.generateRefreshToken(selectResponse.get(0));
                
                jakarta.servlet.http.Cookie refreshTokenCk = new jakarta.servlet.http.Cookie ("refreshToken" ,refreshToken );
                
                refreshTokenCk.setHttpOnly(true); 	// XSS 공격으로부터 보호하기 위해 설정
                refreshTokenCk.setSecure(true);  	// HTTPS 연결에서만 쿠키가 전송되도록 (HttpOnly)
                refreshTokenCk.setPath("/");
	            response.addCookie(refreshTokenCk);
	            
	            // 응답 DTO에 jwtToken 포함
	            ResponseDTO responseDTO = new ResponseDTO.Builder()
	                    .setMessage("loginSuccess")
	                    .setStatusCode(200)
	                    .setJwtToken(accessToken) // jwtToken을 응답에 포함시킴
	                    .setResult(selectResponse.get(0))
	                    .build();
	            return messageHttpResponse.success(responseDTO);
                
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Map<String, String> requestBody,HttpServletRequest request) throws IOException {
    	
    	String signUpId 	= requestBody.get("signUpId");
    	String signUpPw 	= requestBody.get("signUpPw");
    	String signUpNm 	= requestBody.get("signUpNm");
    	String signUpBusiNm = requestBody.get("signUpBusiNm");
    	
    	String condition = "user_id=eq."+signUpId;
    	String tableName = "t_user";
    	
    	String jwtToken ;
    	
    	try {
    		// DB
    		List<Map<String,Object>> selectResponse= comUtil.supaBaseSelect(tableName,condition);
    		
    		// 회원가입 되어있으면 return
    		if(selectResponse.size() > 0) {
    			
    			ResponseDTO responseDTO = new ResponseDTO.Builder()
                        .setMessage("이미 회원가입이 되어있습니다.")
                        .setStatusCode(200)
                        .build();
                return messageHttpResponse.success(responseDTO);
    			
    		}
    		// 회원가입 진행
	        JsonObject supaBaseBody = new JsonObject();
	        supaBaseBody.addProperty("user_id",signUpId);
	        supaBaseBody.addProperty("user_password",signUpPw);
	        supaBaseBody.addProperty("user_nm",signUpNm);
	        supaBaseBody.addProperty("user_busi_nm",signUpBusiNm);
	        
	        
	        System.out.println(supaBaseBody.toString());
	        
	        ResponseEntity<String> response = comUtil.supaBaseInsert(tableName,supaBaseBody);
			
	        HttpStatusCode code = response.getStatusCode();
	        
	        // Insert 성공시에 정상처리 메시지 Return
	        if(code.value() == 200) {
	            ResponseDTO responseDTO = new ResponseDTO.Builder()
	                    .setMessage(Constants.normalMessage)
	                    .setStatusCode(200)
	                    .build();
	            return messageHttpResponse.success(responseDTO);
	        }else {
	        	ResponseDTO responseDTO = new ResponseDTO.Builder()
	                    .setMessage(Constants.normalError)
	                    .setStatusCode(202)
	                    .build();
	            return messageHttpResponse.success(responseDTO);
	        }
            
    	} catch (AuthenticationException e) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    	}
    }
    
 	@GetMapping("/issueToken")
 	@Operation(summary = "RefreshToken을 이용해 AccessToken 재 발급", description = "RefreshToken의 유효성 확인(만료체크) 무결성 확인(조작여부) 권한확인(scopes) 후 AccessToken 재 발급")
 	public void loginChk(@CookieValue(name = "refreshToken", required = false) String refreshToken
 			, HttpServletRequest request,HttpSession session) throws Exception {
 			String accessToken;
 			
 			if(!ObjectUtil.isEmpty(refreshToken)) {
 				
 				String decodeToken = jwtTokenUtil.decodePayload(refreshToken);
 				
 				// 유효성 검증 (만료시간 체크)
 				if(jwtTokenUtil.isTokenExpired(refreshToken)) {
 					System.out.println("유효");
 					if(jwtTokenUtil.validateRefreshToken(refreshToken)){
 						
 						ObjectMapper objectMapper = new ObjectMapper();

 						Map<String,Object> userInfoMap = objectMapper.readValue(decodeToken, Map.class);
 						
 						// 정상적인 RefreshToken이라면 Access토큰 재발급
 						accessToken = jwtTokenUtil.generateAccessToken(userInfoMap);
 						
 					 }else System.out.println("무결성 확인 실패");
 					
 				}else System.out.println("유효하지 않은 토큰");
 				
 				
 				
 				return;
 			}else {
 				
 			}
 	}
}
