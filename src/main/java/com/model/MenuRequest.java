package com.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuRequest {

    private int cate_no;
    private String cate_name;
    private int parent_cate_no;
    private int sort_order;
}
