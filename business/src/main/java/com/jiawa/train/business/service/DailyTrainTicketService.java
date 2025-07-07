package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.DailyTrain;
import com.jiawa.train.business.domain.DailyTrainTicket;
import com.jiawa.train.business.domain.DailyTrainTicketExample;
import com.jiawa.train.business.domain.TrainStation;
import com.jiawa.train.business.enums.SeatTypeEnum;
import com.jiawa.train.business.enums.TrainTypeEnum;
import com.jiawa.train.business.mapper.DailyTrainTicketMapper;
import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);

    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    private TrainStationService trainStationService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;


    public void save(DailyTrainTicketSaveReq req) {

        DateTime now = DateTime.now();


        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);

        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);

        }

        //dailyTrainTicketMapper.insert(dailyTrainTicket);

    }


    @Cacheable(value = "DailyTrainTicketService.queryList3")
    public PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req) {
        LOG.info("测试缓存击穿");
        return null;
    }


    @CachePut(value = "DailyTrainTicketService.queryList2")
    public PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req) {
        return querList(req);
    }


    //@CachePut("DailyTrainTicketService.querList")
    @Cacheable("DailyTrainTicketService.querList")
    public PageResp<DailyTrainTicketQueryResp> querList(DailyTrainTicketQueryReq req) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.setOrderByClause("id desc");
        DailyTrainTicketExample.Criteria criteria  = dailyTrainTicketExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }
        if (ObjectUtil.isNotNull(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if (ObjectUtil.isNotNull(req.getStart())) {
            criteria.andStartEqualTo(req.getStart());
        }
        if (ObjectUtil.isNotNull(req.getEnd())) {
            criteria.andEndEqualTo(req.getEnd());
        }


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainTicket> dailyTrainTicketsList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);


        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketsList, DailyTrainTicketQueryResp.class);

        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }


    @Transactional
    public void genDaily(DailyTrain dailyTrain, Date date, String trainCode) {
        LOG.info("开始生成日期【{}】车次【{}】的余票信息", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的余票信息
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);

        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

        // 查询途径的车站信息

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainStations)) {
            LOG.info("该车次没有余票基础数据，生成该车次余票信息结束");
            return;
        }

        DateTime now = DateTime.now();
        //LOG.info("啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊 现在开始插入余票数据,trainStations为：{}", trainStations);

        for (int i = 0; i < trainStations.size(); i++) {
            TrainStation trainStationStart = trainStations.get(i); // 得到出发站
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = i + 1; j < trainStations.size(); j++) {
                TrainStation trainStationEnd = trainStations.get(j); // 得到结束站
                sumKM = sumKM.add(trainStationEnd.getKm()); // 计算历程之和
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();

                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                String trainType = dailyTrain.getType();
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);


                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }

        LOG.info("结束生成日期【{}】车次【{}】的余票信息", DateUtil.formatDate(date), trainCode);
    }


    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {

        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andStartEqualTo(start)
                .andEndEqualTo(end);
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isNotEmpty(dailyTrainTickets)) {
            return dailyTrainTickets.get(0);
        } else {
            return null;
        }

    }


}
