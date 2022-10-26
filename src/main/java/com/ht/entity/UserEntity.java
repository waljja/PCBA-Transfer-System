package com.ht.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "用户实体")
public class UserEntity {
	private String account;
	private String password;
	private String name;
	private String factory;
	private String node;
	private String permissions;
}
