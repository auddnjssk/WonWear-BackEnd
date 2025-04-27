package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.CommonUtil;
import com.common.ObjectUtil;
import com.common.utils.JwtTokenUtil;

@Service
public class ItemsService {
	
	@Autowired 
	private CommonUtil comUtil; 
	@Autowired 
	private JwtTokenUtil jwtTokenUtil; 
	
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
}
