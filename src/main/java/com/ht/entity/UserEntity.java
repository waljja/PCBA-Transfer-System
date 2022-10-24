package com.ht.entity;

import lombok.Data;

@Data
public class UserEntity {
	private String account;
	private String password;
	private String name;
	private String factory;
	private String node;
	private String permissions;
}
