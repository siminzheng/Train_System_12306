package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.TrainCarriage;
import com.jiawa.train.business.domain.TrainCarriageExample;
import com.jiawa.train.business.enums.SeatColEnum;
import com.jiawa.train.business.mapper.TrainCarriageMapper;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.exception.BusinessExceptionEnum;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageService {

    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    public void save(TrainCarriageSaveReq req) {

        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());


        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);

        if (ObjectUtil.isNull(trainCarriage.getId())) {

            // 保存之前，先校验唯一键是否存在
            TrainCarriage trainCarriageDB = selectBuUnique(req.getTrainCode(), req.getIndex());

            if (ObjectUtil.isNotEmpty(trainCarriageDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }


            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);

        }

        //trainCarriageMapper.insert(trainCarriage);

    }


    private TrainCarriage selectBuUnique(String trainCode, Integer index) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andIndexEqualTo(index);
        List<TrainCarriage> list = trainCarriageMapper.selectByExample(trainCarriageExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    public PageResp<TrainCarriageQueryResp> querList(TrainCarriageQueryReq req) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("train_code asc, `index` asc");
        TrainCarriageExample.Criteria criteria  = trainCarriageExample.createCriteria();

        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainCarriage> trainCarriagesList = trainCarriageMapper.selectByExample(trainCarriageExample);


        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriagesList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriagesList, TrainCarriageQueryResp.class);

        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }


    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("train_code asc, `index` asc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        List<TrainCarriage> list = trainCarriageMapper.selectByExample(trainCarriageExample);
        return list;

    }





}
