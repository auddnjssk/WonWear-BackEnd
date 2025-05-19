package com.common.utils;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MenuUtils {
	
    private final CommonUtil comUtil;
	
	public int getMainMenuId (String mainMenuName ){
		
		String condition = "mainmenu_name=eq."+ mainMenuName + "&subYn=eq.Y " ;
		String tableName = "t_mainmenu";
		List<Map<String,Object>> mainMenuList= comUtil.supaBaseSelect(tableName,condition);
		
		int mainMenuId = (int) mainMenuList.get(0).get("mainmenu_id");
		
		return mainMenuId;
	};
	
	public int getSubMenuId (String subMenuName ){
		
	    String condition = "submenu_name=eq."+subMenuName ;
	    String tableName = "t_submenu";
	    List<Map<String,Object>> subMenuList= comUtil.supaBaseSelect(tableName,condition);
	    
		int subMenuId = (int) subMenuList.get(0).get("submenu_id");
		
		return subMenuId;
	};

    

}
