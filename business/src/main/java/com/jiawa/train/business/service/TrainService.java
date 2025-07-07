package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.Train;
import com.jiawa.train.business.domain.TrainExample;
import com.jiawa.train.business.mapper.TrainMapper;
import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
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
public class TrainService {

    @Resource
    private TrainMapper trainMapper;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    public void save(TrainSaveReq req) {

        DateTime now = DateTime.now();


        Train train = BeanUtil.copyProperties(req, Train.class);

        if (ObjectUtil.isNull(train.getId())) {

            // 保存之前，先校验唯一键是否存在
            Train trainDB = selectBuUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);

        }

        //trainMapper.insert(train);

    }


    public PageResp<TrainQueryResp> querList(TrainQueryReq req) {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code desc");
        TrainExample.Criteria criteria  = trainExample.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trainsList = trainMapper.selectByExample(trainExample);


        PageInfo<Train> pageInfo = new PageInfo<>(trainsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<TrainQueryResp> list = BeanUtil.copyToList(trainsList, TrainQueryResp.class);

        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        trainMapper.deleteByPrimaryKey(id);
    }


    public List<TrainQueryResp> querAll() {
        List<Train> trainList = selectAll();

        List<TrainQueryResp> trainQueryResps = BeanUtil.copyToList(trainList, TrainQueryResp.class);
        return trainQueryResps;
    }


    public List<Train> selectAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");

        return trainMapper.selectByExample(trainExample);
    }

    private Train selectBuUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria().andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


}
