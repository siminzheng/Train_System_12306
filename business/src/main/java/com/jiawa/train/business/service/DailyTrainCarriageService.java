package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.DailyTrainCarriage;
import com.jiawa.train.business.domain.DailyTrainCarriageExample;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.mapper.DailyTrainCarriageMapper;
import com.jiawa.train.business.req.DailyTrainCarriageQueryReq;
import com.jiawa.train.business.req.DailyTrainCarriageSaveReq;
import com.jiawa.train.business.resp.DailyTrainCarriageQueryResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainCarriageService {

    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;

    @Resource
    private TrainCarriageService trainCarriageService;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    public void save(DailyTrainCarriageSaveReq req) {

        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());


        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);

        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);

        }

        //dailyTrainCarriageMapper.insert(dailyTrainCarriage);

    }


    public PageResp<DailyTrainCarriageQueryResp> querList(DailyTrainCarriageQueryReq req) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainCarriageExample.Criteria criteria  = dailyTrainCarriageExample.createCriteria();

        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrainCarriage> dailyTrainCarriagesList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);


        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriagesList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriagesList, DailyTrainCarriageQueryResp.class);

        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        dailyTrainCarriageMapper.deleteByPrimaryKey(id);
    }


    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("开始生成日期【{}】车次【{}】的车厢信息", DateUtil.formatDate(date), trainCode);

        // 删除某日某车次的车站信息
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);

        dailyTrainCarriageMapper.deleteByExample(dailyTrainCarriageExample);

        // 生成该车站的数据
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainCarriages)) {
            LOG.info("该车次没有车厢基础数据，生成该车厢车站信息结束");
            return;
        }
        for (TrainCarriage trainCarriage: trainCarriages) {
            DateTime now = DateTime.now();

            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());

            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        }
        LOG.info("结束生成日期【{}】车次【{}】的车厢信息", DateUtil.formatDate(date), trainCode);
    }


    public List<DailyTrainCarriage> selectBySeatType (Date date, String trainCode, String seatType) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        dailyTrainCarriageExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        List<DailyTrainCarriage> dailyTrainCarriages = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
        return dailyTrainCarriages;
    }


}
