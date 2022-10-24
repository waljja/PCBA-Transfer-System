package com.ht.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "库存实体，库存相关字段")
public class PcbaInventoryEntity {
	private String WO;
	private double WOQuantity;
	private String PartNumber;
	private String Location;
	private String Factory;
	private String State;
	private double AvailableQuantity;
	private String AvailableBatch;
	private String CreateUser;
	private Data CreateTime;
	private String workcenter;
}
