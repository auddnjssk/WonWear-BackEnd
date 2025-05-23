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
import com.dto.CartDTO;
import com.dto.OrderDTO;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final CommonUtil comUtil; 
	private final JwtTokenUtil jwtTokenUtil; 
	private final MenuUtils menuUtils; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getOrder(){
		
    	String tableName = "t_orders";
//    	String condition = "user_id=eq."+userId ;
        List<Map<String,Object>> orderResponse = comUtil.supaBaseSelect(tableName,"");
        
		return orderResponse;
		
	}
	
	public ResponseEntity<String> createOrder(OrderDTO requestBody,String userId){
		
//		String mainMenuName = (String) requestBody.get("mainMenu");
//		String subMenuName = (String) requestBody.get("subMenu");
//		
//		int mainMenuId = menuUtils.getMainMenuId(mainMenuName);
//		int subMenuId  = menuUtils.getSubMenuId(subMenuName);
//		
		ResponseEntity<String> response = null;
		
		String tableName = "t_orders";
        JsonObject supaBaseBody = new JsonObject();
    	supaBaseBody.addProperty("user_id"    		, userId);
    	supaBaseBody.addProperty("receiver_phone"	, requestBody.getPhoneNb());
    	supaBaseBody.addProperty("receiver_name"	, requestBody.getReceiverName());
    	supaBaseBody.addProperty("post_code"		, requestBody.getPostCodeVal());
    	supaBaseBody.addProperty("address1"			, requestBody.getAddress1());
    	supaBaseBody.addProperty("address2"			, requestBody.getAddress2());
    	supaBaseBody.addProperty("order_message"	, requestBody.getAddress2());
    	supaBaseBody.addProperty("status"			, "01");
    	response = comUtil.supaBaseInsert(tableName,supaBaseBody);
    	
    	List<Map<String, Object>> jsonResponse = comUtil.parseJsonString(response.getBody());
    	
		tableName = "t_order_items";
        supaBaseBody = new JsonObject();
        
        for(CartDTO cartMap : requestBody.getCartList()) {
        	supaBaseBody.addProperty("order_id"	 		, (int) jsonResponse.get(0).get("order_id"));
        	supaBaseBody.addProperty("quantity"	 		, cartMap.getQuantity());
        	supaBaseBody.addProperty("item_id"	 		, cartMap.getItemsId());
        	supaBaseBody.addProperty("item_name"	 	, cartMap.getItemName());
        	supaBaseBody.addProperty("price"	 		, cartMap.getItemSalePrice());
        	supaBaseBody.addProperty("option_name"	 	, cartMap.getOptionName());
        	response = comUtil.supaBaseInsert(tableName,supaBaseBody);
        	
        }
        

		return response;
		
	}
}
