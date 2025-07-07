package com.jiawa.train.member.req;

import jakarta.validation.constraints.NotBlank;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.member.req
 * @BelongsClassName: MemberRegisterReq
 * @Author: yuanql
 * @CreateTime: 2023-07-09  21:02
 * @Description: member 的请求参数类
 * @Version: 1.0
 */


public class MemberRegisterReq {

    @NotBlank(message = "【手机号】不能为空！")  // 当手机号为空的时候，向前端返回此内容
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "MemberRegisterReq{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}

