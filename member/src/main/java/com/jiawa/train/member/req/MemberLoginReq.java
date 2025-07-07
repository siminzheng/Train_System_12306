package com.jiawa.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.member.req
 * @BelongsClassName: MemberRegisterReq
 * @Author: yuanql
 * @CreateTime: 2023-07-09  21:02
 * @Description: member 的请求参数类
 * @Version: 1.0
 */


public class MemberLoginReq {

    @NotBlank(message = "【手机号】不能为空！")  // 当手机号为空的时候，向前端返回此内容
    @Pattern(regexp = "1\\d{10}$", message = "手机号格式错误")
    private String mobile;

    @NotBlank(message = "【验证码】不能为空！")
    private String code;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberLoginReq{");
        sb.append("mobile='").append(mobile).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
