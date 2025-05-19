package com.dto;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
    private String quantity;
    private String items_detail_id1;
    private String items_detail_id2;
}
