package com.jiawa.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.util.JWTUtilMy;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.config.MemberApplication;
import com.jiawa.train.member.domain.Member;
import com.jiawa.train.member.domain.MemberExample;
import com.jiawa.train.member.mapper.MemberMapper;
import com.jiawa.train.member.req.MemberLoginReq;
import com.jiawa.train.member.req.MemberRegisterReq;
import com.jiawa.train.member.req.MemberSendCodeReq;
import com.jiawa.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class);

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req) {

        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        Member memberDB = selectByMobile(mobile);

        // 如果手机号没有在用户表中，则插入一条放入到用户表中
        if (ObjectUtil.isNotNull(memberDB)) {
            //return list.get(0).getId();
            //throw new RuntimeException("手机号已注册");
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }


    public void sendCode(MemberSendCodeReq req) {
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        Member memberDB = selectByMobile(mobile);

        // 如果手机号没有在用户表中，则插入一条放入到用户表中
        if (ObjectUtil.isNull(memberDB)) {
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            LOG.info("手机号存在，不记录");
        }

        //生成验证码
//        String code = RandomUtil.randomString(4);
        String code = "8888";
        LOG.info("生成的验证码为：{}", code);
        // 保存短信记录表：手机号、短信验证码、有效期、是否已使用、业务类型、发送时间、使用时间
        LOG.info("保存短信记录表");

        // 对接短信通道，发送短信
        LOG.info("对接短信通道");

    }

    public MemberLoginResp login(MemberLoginReq req) {
        String code = req.getCode();
        String mobile = req.getMobile();
        Member memberDB = selectByMobile(mobile);


        if (ObjectUtil.isNull(memberDB)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
        String token = JWTUtilMy.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
//        Map<String,Object> map = BeanUtil.beanToMap(memberLoginResp);
//        String key = "Jiawa12306";
//        String token = JWTUtil.createToken(map, key.getBytes());
//        memberLoginResp.setToken(token);
        return memberLoginResp;


    }

    /**
     * 抽取出的函数，其主要用于判断数据库中是否存在传入的 mobile 电话号码
     * @param mobile 传入的电话号码，11位
     * @return
     */
    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(members)) {
            return null;
        } else {
            return members.get(0);
        }
    }

}
