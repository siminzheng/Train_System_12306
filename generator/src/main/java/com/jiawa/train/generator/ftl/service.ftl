package com.jiawa.train.${module}.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.common.util.SnowUtil;
import com.jiawa.train.${module}.config.${Module}Application;
import com.jiawa.train.${module}.domain.${Domain};
import com.jiawa.train.${module}.domain.${Domain}Example;
import com.jiawa.train.${module}.mapper.${Domain}Mapper;
import com.jiawa.train.${module}.req.${Domain}QueryReq;
import com.jiawa.train.${module}.req.${Domain}SaveReq;
import com.jiawa.train.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service {

    @Resource
    private ${Domain}Mapper ${domain}Mapper;

    private static final Logger LOG = LoggerFactory.getLogger(${Module}Application.class);


    public void save(${Domain}SaveReq req) {

        DateTime now = DateTime.now();


        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);

        if (ObjectUtil.isNull(${domain}.getId())) {
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        } else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});

        }

        //${domain}Mapper.insert(${domain});

    }


    public PageResp<${Domain}QueryResp> querList(${Domain}QueryReq req) {
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.setOrderByClause("id desc");
        ${Domain}Example.Criteria criteria  = ${domain}Example.createCriteria();


        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<${Domain}> ${domain}sList = ${domain}Mapper.selectByExample(${domain}Example);


        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}sList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}sList, ${Domain}QueryResp.class);

        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;


    }


    public void delete(Long id) {
        ${domain}Mapper.deleteByPrimaryKey(id);
    }


}
