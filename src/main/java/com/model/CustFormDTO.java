package com.model;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustFormDTO {
    private List<Map<String, Object>> coordinateList;
    private List<String> imageList;
}
