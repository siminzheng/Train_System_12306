package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.domain.TrainSeat;
import com.jiawa.train.business.domain.TrainSeatExample;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.mapper.TrainSeatMapper;
import com.jiawa.train.business.req.TrainSeatQueryReq;
import com.jiawa.train.business.req.TrainSeatSaveReq;
import com.jiawa.train.business.resp.TrainSeatQueryResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainSeatService {

    @Resource
    private TrainSeatMapper trainSeatMapper;


    @Resource
    private TrainCarriageService trainCarriageService;


    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    public void save(TrainSeatSaveReq req) {

        DateTime now = DateTime.now();


        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);

        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);

        }

        //trainSeatMapper.insert(trainSeat);

    }


    public PageResp<TrainSeatQueryResp> querList(TrainSeatQueryReq req) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");
        TrainSeatExample.Criteria criteria  = trainSeatExample.createCriteria();

        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainSeat> trainSeatsList = trainSeatMapper.selectByExample(trainSeatExample);


        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatsList, TrainSeatQueryResp.class);

        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }


    @Transactional // 事务
    public void genTrainSeat(String trainCode) {
        DateTime now = DateTime.now();
        // 清空当前车次下的所有座位记录
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);

        // 查找当前车次下的所有的车厢
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        LOG.info("当前车次下的车厢数目：{}", trainCarriages.size());

        // 循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : trainCarriages) {

            // 拿到车厢数据：行数、座位类型（得到列数）
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            LOG.info("当前车厢的行数（排数）：{}", rowCount);
            int seatIndex = 1;

            // 根据车厢的座位类型，筛选出所有的列，比如车厢类型是一等座，则筛选出columList={ABDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
            LOG.info("根据车厢的座位类型，筛选出所有的列: {}", colEnumList);
            LOG.info("+++++++++++++车座类型为：{}++++++++++++++++", seatType);

            // 循环行数
            for (int row = 1; row <= rowCount; row++) {

                // 循环列数
                for (SeatColEnum seat : colEnumList) {
                    // 构造座位数据并保存到数据库
                    TrainSeat trainSeat = new TrainSeat();

                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seat.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);

                    trainSeatMapper.insert(trainSeat);
                }
            }
        }
    }


    public List<TrainSeat> selectByTrainCode(String trainCode) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        trainSeatExample.setOrderByClause("`id` asc");
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);

        return trainSeatMapper.selectByExample(trainSeatExample);
    }


}
