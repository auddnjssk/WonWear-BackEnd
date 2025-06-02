package com.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.common.CommonDao;
import com.common.utils.CommonUtil;
import com.common.utils.JwtTokenUtil;
import com.common.utils.MenuUtils;
import com.common.utils.ObjectUtil;
import com.dto.ItemDTO;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemsService {
	
	private final CommonUtil comUtil; 
	private final JwtTokenUtil jwtTokenUtil; 
	private final MenuUtils menuUtils; 
	private final CommonDao commonDao; 
	
    @Value("${imageDetailFileDir}")
    private String imageDetailFileDir;
    
    private static final long EXPIRATION_TIME = 864_000_00;
    
	public List<Map<String,Object>> getItems(String cateNo){
		
    	String tableName = "t_category";
    	String condition = "cate_no=eq."+cateNo ;
    	
    	Long cateNoLong = Long.valueOf(cateNo); 
    	
    	List<Map<String,Object>> itemsList=commonDao.selectList("ItemMapper.selectItem", cateNoLong );
//    	
//        List<Map<String,Object>> mainMenuList= comUtil.supaBaseSelect(tableName,condition);
//    	
//        tableName = "t_items";
//    	List<Map<String,Object>> itemsList= comUtil.supaBaseSelect(tableName,condition);
//    	
		return itemsList;
		
	}
	
	public List<Map<String,Object>> getItemDetail(String itemsId){
		
		String condition = "item_id=eq."+itemsId ;
		String tableName = "t_items";
		List<Map<String,Object>> itemsList= comUtil.supaBaseSelect(tableName,condition);
		
		tableName = "t_item_detail";
		List<Map<String,Object>> itemDetailList= comUtil.supaBaseSelect(tableName,condition);
		
		if(ObjectUtil.isNotEmpty(itemDetailList)){
			List<Map<String, Object>> itemColorList = itemDetailList.stream()
				    .filter(map -> "COLOR".equals(map.get("item_cond")))
				    .collect(Collectors.toList());
			
			List<Map<String, Object>> itemSizeList = itemDetailList.stream()
					.filter(map -> "SIZE".equals(map.get("item_cond")))
					.collect(Collectors.toList());
			
			itemsList.get(0).put("items_color", itemColorList);
			itemsList.get(0).put("items_size", itemSizeList);
		}
		
		String basePath = imageDetailFileDir;
		String itemNo = "103";
		int index = 1;

		List<String> imageList = new ArrayList<>();

		while (true) {
		    String filename = "/"+itemNo + "_" + index + ".jpg"; // 확장자에 따라 변경
		    File file = new File(basePath + filename);

		    if (file.exists()) {
		        imageList.add(filename);
		        index++;
		    } else {
		        break;
		    }
		}
		
		itemsList.get(0).put("detailImageSize", index);

		
		return itemsList;
		
	}
	
	public ResponseEntity<String> createItemDetail(Map<String, Object> requestBody){
		
		String cateNo = (String) requestBody.get("cateNo");
		
		
		String tableName = "t_items";
        JsonObject supaBaseBody = new JsonObject();
        supaBaseBody.addProperty("item_name"	 , (String) requestBody.get("itemName"));
        supaBaseBody.addProperty("item_price"	 , (String) requestBody.get("price"));
        supaBaseBody.addProperty("image_number"  , (int) requestBody.get("chumbnailList"));
        supaBaseBody.addProperty("item_salePrice", (String) requestBody.get("salePrice"));
        supaBaseBody.addProperty("cate_no"	 , cateNo);
        
        ResponseEntity<String> response = comUtil.supaBaseInsert(tableName,supaBaseBody);
        
        
        List<Map<String, Object>> responseList = comUtil.parseJsonString(response.getBody());
        
        int itemId = (int) responseList.get(0).get("item_id");
        
        List<Map<String,Object>> itemColorList = (List<Map<String,Object>>)  requestBody.get("itemColor") ;
        List<Map<String,Object>> itemSizeList  = (List<Map<String,Object>>)  requestBody.get("itemSize") ;
        
        
		tableName = "t_item_detail";
		for(Map<String,Object> itemColorMap : itemColorList) {
			supaBaseBody = new JsonObject();
			supaBaseBody.addProperty("item_id",itemId);
			supaBaseBody.addProperty("item_cond", "COLOR");
			supaBaseBody.addProperty("item_detail",(String) itemColorMap.get("item_detail"));
			comUtil.supaBaseInsert(tableName,supaBaseBody);
		}
		
		for(Map<String,Object> itemSizeMap : itemSizeList) {
			supaBaseBody = new JsonObject();
			supaBaseBody.addProperty("item_id",itemId);
			supaBaseBody.addProperty("item_cond", "SIZE");
			supaBaseBody.addProperty("item_detail",(String) itemSizeMap.get("item_detail"));
			comUtil.supaBaseInsert(tableName,supaBaseBody);
		}

		return response;
	}
	public ResponseEntity<String> deleteItem(List<ItemDTO> requestBody){
		
		List<Long> itemIds = new ArrayList<>();
		
		for(ItemDTO itemColorMap : requestBody) {
			itemIds.add(itemColorMap.getItem_id());
		}
		
		int deletedCount = commonDao.delete("ItemMapper.deleteItem", itemIds);
		
	    if (deletedCount == itemIds.size()) {
	        return ResponseEntity.ok("삭제 성공");
	    } else {
	        throw new IllegalStateException("일부 항목이 삭제되지 않았습니다. ");
	    }	
		
	}
}
