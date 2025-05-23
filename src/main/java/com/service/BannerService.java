package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.ObjectUtil;
import com.model.MenuRequest;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Service
public class BannerService {
	
	@Autowired 
	private CommonUtil comUtil; 
	@Autowired 
	private JwtTokenUtil jwtTokenUtil; 
	
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getBanner(){
		
    	String tableName = "t_main_banner";
    	
    	List<Map<String,Object>> selectResponse= comUtil.supaBaseSelect(tableName,null);
    	
		return selectResponse;
		
	}
	
	public List<Map<String,Object>> createMenu(MenuRequest  requestBody){
		
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


}
