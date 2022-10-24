package com.ht.constants;

public class Constants {
	
	public static final String[] NAME = {
		"蒋晓琴", "陆汉云", "谭长蓉", "秦丹"
	};
	public static final String[] ACCOUNT = {
		"DD20312064","EM0011173"
	};
	
	public static boolean  isNames (String Name){
		for(int i=0;i<NAME.length;i++){
			if(NAME[i].equals(Name)){
				return true;
			}
		}
		return false;
	}
	public static boolean  isAccount (String Account){
		for(int i=0;i<ACCOUNT.length;i++){
			if(ACCOUNT[i].equals(Account)){
				return true;
			}
		}
		return false;
	}
}
