package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.ObjectUtil;
import com.google.gson.JsonObject;
import com.model.MenuRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Service
public class MenuService {
	
	@Autowired 
	private CommonUtil comUtil; 
	@Autowired 
	private JwtTokenUtil jwtTokenUtil; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getMenu(){
		
    	String tableName = "t_mainmenu";
    	
    	List<Map<String,Object>> selectResponse= comUtil.supaBaseSelect(tableName,null);
    	
    	tableName = "t_submenu";
    	
    	List<Map<String,Object>> selectSubMenu = comUtil.supaBaseSelect(tableName,null);
    	
    	List<Map<String,Object>> mainMenuList = new ArrayList<>();
    	
    	for(Map<String,Object> mainMenuMap : selectResponse) {
    		
    		List<Map<String,Object>> subMenuList = new ArrayList<>();
    		
        	for(Map<String,Object> subMenuMap : selectSubMenu) {
        		if(mainMenuMap.get("mainmenu_id").equals(subMenuMap.get("mainmenu_id"))) {
        			subMenuList.add(subMenuMap);
        		}
        	}
        	
        	
        	if(ObjectUtil.isNotEmpty(subMenuList)) {
        		mainMenuMap.put("subMenuList", subMenuList);
        	}
        	mainMenuList.add(mainMenuMap);
    	}
    	
		return mainMenuList;
		
	}
	
	public ResponseEntity<String> createMenu(MenuRequest requestBody){
		
		String tableName = "t_mainmenu";
        JsonObject supaBaseBody = new JsonObject();
        supaBaseBody.addProperty("mainmenu_name" , requestBody.getMainmenu_name());
        supaBaseBody.addProperty("subYn"	     , requestBody.getSubYn());
        
        ResponseEntity<String> response = comUtil.supaBaseInsert(tableName,supaBaseBody);

		return response;
	}
	
	public ResponseEntity<String> deleteMenu(Long mainmenu_id){
		
		String tableName = "t_submenu";
		String condition = "mainmenu_id=eq."+mainmenu_id ;
		comUtil.supaBaseDelete(tableName,condition);
		
		tableName = "t_mainmenu";
		comUtil.supaBaseDelete(tableName,condition);
		
		tableName = "t_cart";
		comUtil.supaBaseDelete(tableName,condition);
		
		tableName = "t_items";
		ResponseEntity<String> response = comUtil.supaBaseDelete(tableName,condition);
		
		return response;
	}
	
	public ResponseEntity<String> createSubMenu(MenuRequest requestBody){
		
		
		ResponseEntity<String> response = null;
		String tableName = "t_mainmenu";
		String condition = "mainmenu_id=eq."+requestBody.getMainmenu_id() ;
		
        if(requestBody.getSubmenu_id() == 1) {
        	
        	JsonObject supaBaseBody = new JsonObject();
        	supaBaseBody.addProperty("subYn" , "Y");
        	comUtil.supaBaseUpdate(tableName,condition,supaBaseBody);
        	
        }
        
        tableName = "t_submenu";
        
    	JsonObject supaBaseBody = new JsonObject();
    	supaBaseBody.addProperty("mainmenu_id" , requestBody.getMainmenu_id());
    	supaBaseBody.addProperty("submenu_id" 	 , requestBody.getSubmenu_id());
    	supaBaseBody.addProperty("submenu_name"  , requestBody.getSubmenu_name());
    	
    	response = comUtil.supaBaseInsert(tableName,supaBaseBody);

		return response;
	}
	
	public ResponseEntity<String> deleteSubMenu(Long mainmenuId, Long submenuId) {
	    String tableName;
	    String condition;
	    ResponseEntity<String> response = null;

	    try {
	        // 1. subYn 업데이트 (submenuId == 1일 때만)
	        if (submenuId == 1) {
	            tableName = "t_mainmenu";
	            condition = "mainmenu_id=eq." + mainmenuId;

	            JsonObject supaBaseBody = new JsonObject();
	            supaBaseBody.addProperty("subYn", "N");

	            ResponseEntity<String> updateResponse = comUtil.supaBaseUpdate(tableName, condition, supaBaseBody);
	            if (!updateResponse.getStatusCode().is2xxSuccessful()) {
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("Failed to update subYn for mainmenu_id: " + mainmenuId);
	            }
	        }

	        // 2. t_submenu 삭제
	        tableName = "t_submenu";
	        condition = "submenu_id=eq." + submenuId + "&mainmenu_id=eq." + mainmenuId;

	        ResponseEntity<String> deleteSubmenuResponse = comUtil.supaBaseDelete(tableName, condition);
	        if (!deleteSubmenuResponse.getStatusCode().is2xxSuccessful()) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to delete from t_submenu: submenu_id=" + submenuId);
	        }

	        // 3. t_items 삭제
	        tableName = "t_items";

	        ResponseEntity<String> deleteItemsResponse = comUtil.supaBaseDelete(tableName, condition);
	        if (!deleteItemsResponse.getStatusCode().is2xxSuccessful()) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to delete from t_items: submenu_id=" + submenuId);
	        }

	        response = ResponseEntity.ok("Delete successful: submenu_id=" + submenuId);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Exception occurred: " + e.getMessage());
	    }

	    return response;
	}

}
