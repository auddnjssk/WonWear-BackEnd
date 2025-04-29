package com.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuRequest {

    private int mainmenu_id;
    private String mainmenu_name;
    private String subYn;
    private boolean addYn;
}
