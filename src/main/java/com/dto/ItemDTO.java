package com.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
    private Long cate_no;
    private Long item_salePrice;
    private Long image_number;
    private Long item_id;
    private Long item_price;
    private String created_at;
    private String item_name;
    private Boolean removeYn;

    // getter, setter
}
