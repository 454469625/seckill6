package com.lucq.seckill.result;

public class CodeMsg {
    private Integer code;
    private String msg;

    public static final CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务器异常");
    public static final CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static final CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求非法");
    public static final CodeMsg VERIFYCODE_ERROR = new CodeMsg(500103, "验证码错误");
    public static final CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500104, "不能频繁访问");

    //登录模块
    public static final CodeMsg SESSION_ERROR = new CodeMsg(500210, "session不存在或已经失效");
    public static final CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static final CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static final CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
    public static final CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
    public static final CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");

    public static final CodeMsg MOBILE_EXIST = new CodeMsg(500216, "手机号存在");
    public static final CodeMsg ADMIN_ERROR = new CodeMsg(500217, "管理员账号或密码错误");



    public static final CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");



    public static final CodeMsg SECKILL_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static final CodeMsg REPEAT_SECKILL = new CodeMsg(500501, "不能重复秒杀");
    public static final CodeMsg SECKILL_FAIL = new CodeMsg(500502, "秒杀失败");

    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    private CodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
