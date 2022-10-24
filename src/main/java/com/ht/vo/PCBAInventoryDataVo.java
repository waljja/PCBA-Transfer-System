package com.ht.vo;

import lombok.Data;

@Data
public class PCBAInventoryDataVo {
	private String wo;
	private String woquantity;
	private String partnumber;
	private String location;
	private String availablequantity;
	private String uid;
	private String plant;
	private String workcenter;
	private String createuser;
	private String createtime;
}
