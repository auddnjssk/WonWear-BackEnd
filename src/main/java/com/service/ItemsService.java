package com.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MenuUtils;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemsService {
	
	private final CommonUtil comUtil; 
	private final JwtTokenUtil jwtTokenUtil; 
	private final MenuUtils menuUtils; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getItems(String main,String sub){
		
        String condition = "mainmenu_name=eq."+main ;
    	String tableName = "t_mainmenu";
        List<Map<String,Object>> mainMenuList= comUtil.supaBaseSelect(tableName,condition);
        
        condition = "submenu_name=eq."+sub ;
        tableName = "t_submenu";
        List<Map<String,Object>> subMenuList= comUtil.supaBaseSelect(tableName,condition);
    	
        condition = "mainmenu_id=eq."+(int) mainMenuList.get(0).get("mainmenu_id") + "&submenu_id=eq." + (int) subMenuList.get(0).get("submenu_id");
        tableName = "t_items";
        
    	List<Map<String,Object>> itemsList= comUtil.supaBaseSelect(tableName,condition);
    	
		return itemsList;
		
	}
	
	public List<Map<String,Object>> getItemDetail(String itemsId){
		
		String condition = "items_id=eq."+itemsId ;
		String tableName = "t_items";
		List<Map<String,Object>> itemsList= comUtil.supaBaseSelect(tableName,condition);
		
		tableName = "t_item_detail";
		List<Map<String,Object>> itemDetailList= comUtil.supaBaseSelect(tableName,condition);
		
		List<Map<String, Object>> itemColorList = itemDetailList.stream()
			    .filter(map -> "COLOR".equals(map.get("item_cond")))
			    .collect(Collectors.toList());
		
		List<Map<String, Object>> itemSizeList = itemDetailList.stream()
				.filter(map -> "SIZE".equals(map.get("item_cond")))
				.collect(Collectors.toList());
		
		itemsList.get(0).put("items_color", itemColorList);
		itemsList.get(0).put("items_size", itemSizeList);
		
		return itemsList;
		
	}
	
	public ResponseEntity<String> createItemDetail(Map<String, Object> requestBody){
		
		String mainMenuName = (String) requestBody.get("mainMenu");
		String subMenuName = (String) requestBody.get("subMenu");
		
		int mainMenuId = menuUtils.getMainMenuId(mainMenuName);
		int subMenuId  = menuUtils.getSubMenuId(subMenuName);
		
		
		String tableName = "t_items";
        JsonObject supaBaseBody = new JsonObject();
        supaBaseBody.addProperty("item_name"	 , (String) requestBody.get("itemName"));
        supaBaseBody.addProperty("item_price"	 , (String) requestBody.get("price"));
        supaBaseBody.addProperty("image_number"  , (int) requestBody.get("chumbnailList"));
        supaBaseBody.addProperty("item_salePrice", (String) requestBody.get("salePrice"));
        supaBaseBody.addProperty("mainmenu_id"	 , mainMenuId);
        supaBaseBody.addProperty("submenu_id"	 , subMenuId);
        supaBaseBody.addProperty("submenu_id"	 , subMenuId);
        
        ResponseEntity<String> response = comUtil.supaBaseInsert(tableName,supaBaseBody);
        
        
        List<Map<String, Object>> responseList = comUtil.parseJsonString(response.getBody());
        //String itemId = (String) responseList.get(0).get("items_id");
        System.out.println(responseList.get(0).get("items_id"));
        int itemId = (int) responseList.get(0).get("items_id");
        
        List<Map<String,Object>> itemColorList = (List<Map<String,Object>>)  requestBody.get("itemColor") ;
        List<Map<String,Object>> itemSizeList  = (List<Map<String,Object>>)  requestBody.get("itemSize") ;
        
        
		tableName = "t_item_detail";
		for(Map<String,Object> itemColorMap : itemColorList) {
			supaBaseBody = new JsonObject();
			supaBaseBody.addProperty("items_id",itemId);
			supaBaseBody.addProperty("item_cond", "COLOR");
			supaBaseBody.addProperty("item_detail",(String) itemColorMap.get("item_detail"));
			comUtil.supaBaseInsert(tableName,supaBaseBody);
		}
		
		for(Map<String,Object> itemSizeMap : itemSizeList) {
			supaBaseBody = new JsonObject();
			supaBaseBody.addProperty("items_id",itemId);
			supaBaseBody.addProperty("item_cond", "SIZE");
			supaBaseBody.addProperty("item_detail",(String) itemSizeMap.get("item_detail"));
			comUtil.supaBaseInsert(tableName,supaBaseBody);
		}

		return response;
		
	}
}
