package com.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {

    private List<Map<String, Object>> chumbnailList;
    private List<Map<String, Object>> itemColor;
    private List<Map<String, Object>> itemSize;

    private int price;
    private int salePrice;

    private String mainMenu;
    private String subMenu;
}
