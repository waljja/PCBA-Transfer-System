package com.ht.api;

/**
 * 通用返回对象
 * @param <T>
 */
public class CommonResult<T> {
 
    private long code;
    private String message;
    private T data;
    private String total;
    private String smtnumber;
    private String cobnumber;
    private String minumber;
    private String node;
    private String name;
    private String[] permissions; 
 
    public CommonResult() {
    }
 
    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public CommonResult(long code, String message, T data,String node,String name,String[] permissions) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.node = node;
        this.name = name;
        this.permissions = permissions;
    }
    
    public CommonResult(long code, String message,String total,String smtnumber,String cobnumber,String minumber, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
        this.smtnumber = smtnumber;
        this.cobnumber = cobnumber;
        this.minumber = minumber;
    }
    
    
  

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getSmtnumber() {
		return smtnumber;
	}

	public void setSmtnumber(String smtnumber) {
		this.smtnumber = smtnumber;
	}

	public String getCobnumber() {
		return cobnumber;
	}

	public void setCobnumber(String cobnumber) {
		this.cobnumber = cobnumber;
	}

	public String getMinumber() {
		return minumber;
	}

	public void setMinumber(String minumber) {
		this.minumber = minumber;
	}

	public long getCode() {
        return code;
    }
 
    public void setCode(long code) {
        this.code = code;
    }
 
    public String getMessage() {
        return message;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }
 
    public T getData() {
        return data;
    }
 
    public void setData(T data) {
        this.data = data;
    }
 
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(String total,String smtnumber,String cobnumber,String minumber,T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),total,smtnumber,cobnumber,minumber, data);
    }
    
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data){
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    
    public static <T> CommonResult<T> success(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }
 
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }
    
//    /**
//     * 成功返回结果
//     *
//     * @param data 获取的数据
//     * @param  message 提示信息
//     */
//    public static <T> CommonResult<T> success(T data, String message,String node,String name) {
//        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data,node,name);
//    }
    
    /**
     * 登入成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> successLogin(T data, String message,String node,String name,String[] permissions) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data,node,name,permissions);
    }
    
   
    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }
 
    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }
 
    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }
 
    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }
 
    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }
 
    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }
 
    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

	public static CommonResult<String> success(String string, String factory,
			String node2, String name2, String name3, String permissions2) {
		// TODO Auto-generated method stub
		return null;
	}

	

	 
}

