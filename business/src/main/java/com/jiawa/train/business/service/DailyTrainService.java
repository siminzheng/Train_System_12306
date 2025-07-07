package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.DailyTrain;
import com.jiawa.train.business.domain.DailyTrainExample;
import com.jiawa.train.business.domain.Train;
import com.jiawa.train.business.mapper.DailyTrainMapper;
import com.jiawa.train.business.req.DailyTrainQueryReq;
import com.jiawa.train.business.req.DailyTrainSaveReq;
import com.jiawa.train.business.resp.DailyTrainQueryResp;
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
public class DailyTrainService {

    @Resource
    private DailyTrainMapper dailyTrainMapper;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);

    @Resource
    private TrainService trainService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainTicketService trainTicketService;


    public void save(DailyTrainSaveReq req) {

        DateTime now = DateTime.now();


        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);

        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);

        }

        //dailyTrainMapper.insert(dailyTrain);

    }


    public PageResp<DailyTrainQueryResp> querList(DailyTrainQueryReq req) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc, code desc");
        DailyTrainExample.Criteria criteria  = dailyTrainExample.createCriteria();

        if (ObjectUtil.isNotEmpty(req.getCode())) {
            criteria.andCodeEqualTo(req.getCode());
        }
        if (ObjectUtil.isNotNull(req.getDate())) {
            criteria.andDateEqualTo(req.getDate());
        }


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<DailyTrain> dailyTrainsList = dailyTrainMapper.selectByExample(dailyTrainExample);


        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrainsList, DailyTrainQueryResp.class);

        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }

    /**
     * 生成某日所有车次信息，包括车次、车站、车厢、座位
     * @param date
     */

    @Transactional
    public void genDaily(Date date) {

        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
               genDailyTrain(date, train);
        }
    }

    @Transactional
    public void genDailyTrain(Date date, Train train) {
        LOG.info("开始生成日期【{}】车次【{}】的信息", DateUtil.formatDate(date), train.getCode());
        // 删除改车次已有的数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria()
                .andDateEqualTo(date)
                .andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);

        // 生成该车次的数据
        DateTime now = DateTime.now();

        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());

        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);

        // 生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());

        // 生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());
//
        // 生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());
//
//        // 生成该车次的座位余票数据
        trainTicketService.genDaily(dailyTrain, date, train.getCode());


        LOG.info("结束生成日期【{}】车次【{}】的信息", DateUtil.formatDate(date), train.getCode());
    }


}
