package com.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;

@Service
public class LoginService {
	
	@Autowired 
	private CommonUtil comUtil; 
	@Autowired 
	private JwtTokenUtil jwtTokenUtil; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> loginChk(Map<String, String> requestBody){
		

        String userId 		= (String) requestBody.get("userId");
        String userPassword = (String) requestBody.get("passWord");
        String condition = "user_id=eq."+userId + "&user_password=eq." + userPassword;
        String tableName = "t_user";
		
    	// DB
    	List<Map<String,Object>> selectResponse= comUtil.supaBaseSelect(tableName,condition);
    	
    	
		return selectResponse;
		
	}
	
	public String createAccessToken(Map<String, Object> selectResponseMap){
		
		return jwtTokenUtil.generateAccessToken(selectResponseMap);
        
	}

}
