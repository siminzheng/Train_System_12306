package com.jiawa.train.member.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.member.config.MemberApplication;
import com.jiawa.train.member.domain.Ticket;
import com.jiawa.train.member.domain.TicketExample;
import com.jiawa.train.member.mapper.TicketMapper;
import com.jiawa.train.member.req.TicketQueryReq;
import com.jiawa.train.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Resource
    private TicketMapper ticketMapper;

    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class);


    public void save(MemberTicketReq req) {
//        LOG.info("seata全局事务ID save: {}", RootContext.getXID());
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        ticket.setId(SnowUtil.getSnowflakeNextId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticketMapper.insert(ticket);

    }


    public PageResp<TicketQueryResp> querList(TicketQueryReq req) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("id desc");
        TicketExample.Criteria criteria  = ticketExample.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Ticket> ticketsList = ticketMapper.selectByExample(ticketExample);


        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TicketQueryResp> list = BeanUtil.copyToList(ticketsList, TicketQueryResp.class);

        PageResp<TicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }


}
