package com.jiawa.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.config.MemberApplication;
import com.jiawa.train.member.domain.Passenger;
import com.jiawa.train.member.domain.PassengerExample;
import com.jiawa.train.member.mapper.PassengerMapper;
import com.jiawa.train.member.req.PassengerQueryReq;
import com.jiawa.train.member.req.PassengerSaveReq;
import com.jiawa.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class);


    public void save(PassengerSaveReq req) {

        DateTime now = DateTime.now();


        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);

        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);

        }

        //passengerMapper.insert(passenger);

    }


    public PageResp<PassengerQueryResp> querList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("id desc");
        PassengerExample.Criteria critelia  = passengerExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getMemberId())) {
            critelia.andMemberIdEqualTo(req.getMemberId());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengersList = passengerMapper.selectByExample(passengerExample);


        PageInfo<Passenger> pageInfo = new PageInfo<>(passengersList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<PassengerQueryResp> list = BeanUtil.copyToList(passengersList, PassengerQueryResp.class);

        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }


    public List<PassengerQueryResp> queryMine() {
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("name asc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        criteria.andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> list = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }


}
