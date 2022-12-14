package com.ht.controller;

import com.ht.api.CommonResult;
import com.ht.api.ResultCode;
import com.ht.entity.UserEntity;
import com.ht.service.UserService;
import io.swagger.annotations.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;

@Api("JavaAPI接口")
@CrossOrigin //跨域调用
@RestController
@RequestMapping(value="/api") 
public class UserConntroller {
	
	 private static Logger log = LoggerFactory.getLogger(UserConntroller.class);
	  
	@Autowired
    public UserService userService;
	
	/**
     * 用户登录
     *
     * @param name 用户账号
     * @param paw 用户密码
     * @return 
     */
	//,method=RequestMethod.GET
	@ApiResponses({@ApiResponse(code = 400, message = "请求参数没有填好"),
				   @ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确")})
	@ApiOperation(value="登入账号", notes="根据账号密码进行登入")
	@RequestMapping(value="/login",method=RequestMethod.POST)
    public CommonResult<String> SelAccount(@RequestBody UserEntity user) {
		ResultCode result = null;
		String[] strArray = null; 
		UserEntity state = userService.SelAccount(user.getAccount(),user.getPassword());
		if (checkObjFieldIsNotNull(state)) {
		   strArray = state.getPermissions().split(",");
		}else {
			result = ResultCode.LOGINGFAILED;
			return CommonResult.failed(result);
		}
		return CommonResult.successLogin("登入成功",state.getFactory(),state.getNode(),state.getName(),strArray);
	} 
	
	 /**
     * 用户注册
     *
     * @param name     用户账号
     * @param password 用户密码
     * @return 
     */
	//,method=RequestMethod.GET
	@ApiResponses({@ApiResponse(code = 400, message = "请求参数没有填好"),
				   @ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确")})
	@ApiOperation(value="注册账号", notes="根据姓名年龄进行注册")
	@RequestMapping(value="/insert",method=RequestMethod.POST)
    public String postLogin(@RequestBody UserEntity user) {
		JSONObject jsonObject = new JSONObject();
		String code = "";
        String msg = "";
		int state = userService.insert(user);
		if (state>0) {
			code = "100";
	        msg = "注册成功";
			jsonObject.put("code", code);
	        jsonObject.put("msg", msg);
		}else {
			code = "101";
	        msg = "注册失败";
	        jsonObject.put("code", code);
	        jsonObject.put("msg", msg);
		}
		
        return jsonObject.toString();
	}
	
	/**
     * 删除用户
     *
     * @param ID     用户ID
     * @return 
     */
	//,method=RequestMethod.GET
	@ApiResponses({@ApiResponse(code = 400, message = "请求参数没有填好"),
				   @ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确")})
	@ApiOperation(value="删除账号", notes="根据姓Id进行删除")
	@RequestMapping(value="/delete",method=RequestMethod.GET)
    public String postLogin(@RequestParam(name = "id",required = true,defaultValue = "")@ApiParam("id") String id) {
		JSONObject jsonObject = new JSONObject();
		String code = "";
        String msg = "";
		int state = userService.delete(id);
		if (state>0) {
			code = "100";
	        msg = "删除成功";
			jsonObject.put("code", code);
	        jsonObject.put("msg", msg);
		}else {
			code = "101";
	        msg = "删除失败";
	        jsonObject.put("code", code);
	        jsonObject.put("msg", msg);
		}
		
        return jsonObject.toString();
	}
	
	public static boolean checkObjFieldIsNotNull(Object obj) {   // true 不为空  false 为空
	    boolean flag = false;
	    try {
	        for (Field f : obj.getClass().getDeclaredFields()) {
	            f.setAccessible(true);
	            if (f.get(obj) == null || f.get(obj) == "") {
	            } else {
	                flag = true;
	            }
	        }
	    } catch (Exception e) {
	        return false;
	    }
	    return flag;
	}
	

}
