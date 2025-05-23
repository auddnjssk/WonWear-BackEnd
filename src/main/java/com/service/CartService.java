package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MenuUtils;
import com.dto.CartDTO;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	
	private final CommonUtil comUtil; 
	private final JwtTokenUtil jwtTokenUtil; 
	private final MenuUtils menuUtils; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getCart(String userId){
		
    	String tableName = "t_cart";
    	String condition = "user_id=eq."+userId ;
    	List<Map<String,Object>> cartList = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> cartResponse = comUtil.supaBaseSelect(tableName,condition);
        
        for(Map<String,Object> cartMap : cartResponse) {
        	tableName = "t_item_detail";
        	condition = "items_detail_id=in.("+(int) cartMap.get("items_detail_id1") +","+(int) cartMap.get("items_detail_id2") + ")";
        	List<Map<String,Object>> itemDetail= comUtil.supaBaseSelect(tableName,condition);
        	
        	tableName = "t_items";
        	condition = "item_id=eq."+cartMap.get("item_id");
        	
        	List<Map<String,Object>> items= comUtil.supaBaseSelect(tableName,condition);
        	
        	Map<String,Object> itemMap = new HashMap<String,Object>();
        	itemMap.put("itemName"		 	, items.get(0).get("item_name"));
        	itemMap.put("itemPrice"			, items.get(0).get("item_price"));
        	itemMap.put("itemSalePrice"		, items.get(0).get("item_salePrice"));
        	itemMap.put("checked"			, false);
        	
        	itemMap.put("itemsId"		 	, cartMap.get("item_id"));
        	itemMap.put("quantity"		 	, cartMap.get("quantity"));
        	itemMap.put("itemsDetailId1"	, cartMap.get("items_detail_id1")); 
        	itemMap.put("itemsDetailId2"	, cartMap.get("items_detail_id2"));
        	
        	StringBuffer sb = new StringBuffer();
        	
        	for(Map<String,Object> itemDetailMap : itemDetail) {
        		String itemDetailType = ("COLOR").equals(itemDetailMap.get("item_cond")) ? "COLOR" : "SIZE";
        		sb.append(itemDetailType);
        		sb.append(" : ");
	        	itemMap.put(itemDetailType	 , itemDetailMap.get("item_detail"));
	        	sb.append(itemDetailMap.get("item_detail"));
	        	sb.append(", ");
	        	
        	}
        	int len = sb.length()-2;
        	sb.delete(len , sb.length());
        	
        	itemMap.put("optionName" , sb);
        	
        	cartList.add(itemMap);
        }
    	
		return cartList;
		
	}
	
	public ResponseEntity<String> createCart(List<CartDTO> requestBody,String userId){
		
		ResponseEntity<String> response = null;
		
		String tableName = "t_cart";
        JsonObject supaBaseBody = new JsonObject();
        for(CartDTO requestMap : requestBody) {
        	supaBaseBody.addProperty("user_id"	 		, userId);
        	supaBaseBody.addProperty("items_detail_id1" , requestMap.getItems_detail_id1());
        	supaBaseBody.addProperty("items_detail_id2" , requestMap.getItems_detail_id2());
        	supaBaseBody.addProperty("quantity"	 		, requestMap.getQuantity());
        	supaBaseBody.addProperty("item_id"	 		, requestMap.getItemsId());
        	response = comUtil.supaBaseInsert(tableName,supaBaseBody);
        }
        
		return response;
		
	}
	
	public ResponseEntity<String> deleteCart(String itemId,String itemsDetailId1,String itemsDetailId2,String userId){
		
		ResponseEntity<String> response = null;

		String tableName = "t_cart";
        String condition = "user_id=eq." + userId + "&item_id=eq." + itemId + "&items_detail_id1=eq." + itemsDetailId1 + "&items_detail_id2=eq." + itemsDetailId2;

        ResponseEntity<String> deleteSubmenuResponse = comUtil.supaBaseDelete(tableName, condition);
        if (!deleteSubmenuResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete");
        }
        
		return deleteSubmenuResponse;
		
	}
}
