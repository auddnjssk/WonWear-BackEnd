package com.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
	private int itemsId;
    private int quantity;
    private String items_detail_id1;
    private String items_detail_id2;
    
    
    private String COLOR;
    private String SIZE;
    private String itemName;
    private int itemPrice;
    private int itemSalePrice;
    
    private String checked;
    private String itemsDetailId1;
    private String itemsDetailId2;
    
    private String optionName;
}
