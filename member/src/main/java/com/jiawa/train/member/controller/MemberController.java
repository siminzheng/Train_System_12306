package com.jiawa.train.member.controller;

import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.member.req.MemberLoginReq;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.resp.MemberLoginResp;
import com.jiawa.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Integer> count() {
        int count = memberService.count();
//        CommonResp<Integer> integerCommonResp = new CommonResp<>();
//        integerCommonResp.setContent(count);
//        return integerCommonResp;
        return new CommonResp<>(count);
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req){
        long register = memberService.register(req);
//        CommonResp<Long> longCommonResp = new CommonResp<>();
//        longCommonResp.setContent(register);
//        return longCommonResp;
        return new CommonResp<>(register);
    }


    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq req){
        memberService.sendCode(req);

        return new CommonResp<>();
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> sendCode(@Valid @RequestBody MemberLoginReq req){
        MemberLoginResp loginResp = memberService.login(req);

        return new CommonResp<>(loginResp);
    }





}
