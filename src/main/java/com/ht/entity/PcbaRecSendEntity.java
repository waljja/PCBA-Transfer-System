package com.ht.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "PCBA板发送实体")
public class PcbaRecSendEntity {
	private String WO;
	private String WOQTY;
	private String PartNumber;
	private String ReceiveBatch;
	private String Location;
	private String ReceiveUser;
	private String ReceiveBatchQTY;
	private String ReceiveTime;
	private String Status;
	private String SendingBatch;
	private String SendingBatchQTY;
	private String SendingUser;
	private String SendingTime;
	private String WoCumulativeSentQTY;
	private String CreateUser;
	private String CreateTime;
}
