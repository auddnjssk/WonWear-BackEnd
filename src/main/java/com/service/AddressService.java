package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MenuUtils;
import com.dto.AddressDTO;
import com.dto.CartDTO;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
	
	private final CommonUtil comUtil; 
	private final JwtTokenUtil jwtTokenUtil; 
	private final MenuUtils menuUtils; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getAddress(String userId){
		
		String tableName = "t_address";
    	String condition = "user_id=eq."+userId;
    	
    	List<Map<String,Object>> items= comUtil.supaBaseSelect(tableName,condition);
    	
    	return items;
	}
	public ResponseEntity<String> createAddress(AddressDTO requestBody , String userId){
		
		String tableName = "t_address";
        JsonObject supaBaseBody = new JsonObject();
        
    	supaBaseBody.addProperty("user_id"	 		, userId);
    	supaBaseBody.addProperty("address1"	 		, requestBody.getAddress1());
    	supaBaseBody.addProperty("address2"	 		, requestBody.getAddress2());
    	supaBaseBody.addProperty("receiver_phone"	, requestBody.getReceiverPhone());
    	supaBaseBody.addProperty("receiver_name"	, requestBody.getReceiverName());
    	supaBaseBody.addProperty("post_code"	 	, requestBody.getPostCode());
    	
    	ResponseEntity<String>  response = comUtil.supaBaseInsert(tableName,supaBaseBody);
    	
		
		return response;
	}
}
