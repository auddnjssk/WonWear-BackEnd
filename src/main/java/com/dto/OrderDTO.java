package com.dto;

import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
	private List<CartDTO> cartList;
	private String postCodeVal;
	private String receiverName;
	private String address1;
	private String address2;
	private String phoneNb;
	private String orderMessage;
	
}
