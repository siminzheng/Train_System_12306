package com.jiawa.train.business.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.business.config.BusinessApplication;
import com.jiawa.train.business.domain.Station;
import com.jiawa.train.business.domain.StationExample;
import com.jiawa.train.business.mapper.StationMapper;
import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
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
public class StationService {

    @Resource
    private StationMapper stationMapper;

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);


    public void save(StationSaveReq req) {

        DateTime now = DateTime.now();


        Station station = BeanUtil.copyProperties(req, Station.class);

        if (ObjectUtil.isNull(station.getId())) {

            // 保存之前，先校验唯一键是否存在
            Station stationDB = selectBuUnique(req.getName());
            if (ObjectUtil.isNotEmpty(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }

            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);

        }

        //stationMapper.insert(station);

    }

    private Station selectBuUnique(String name) {
        StationExample stationExample = new StationExample();
        stationExample.createCriteria().andNameEqualTo(name);
        List<Station> list = stationMapper.selectByExample(stationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }


    public PageResp<StationQueryResp> querList(StationQueryReq req) {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("id desc");
        StationExample.Criteria critelia  = stationExample.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Station> stationsList = stationMapper.selectByExample(stationExample);


        PageInfo<Station> pageInfo = new PageInfo<>(stationsList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<StationQueryResp> list = BeanUtil.copyToList(stationsList, StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }


    public List<StationQueryResp> querAll() {
        StationExample stationExample = new StationExample();
        stationExample.setOrderByClause("name_pinyin asc");

        List<Station> stations = stationMapper.selectByExample(stationExample);

        List<StationQueryResp> stationQueryResps = BeanUtil.copyToList(stations, StationQueryResp.class);

        return stationQueryResps;
    }


}
