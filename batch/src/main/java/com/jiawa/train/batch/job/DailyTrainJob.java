package com.jiawa.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.jiawa.train.batch.feign.BusinessFeign;
import com.jiawa.train.common.response.CommonResp;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;


/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.batch.job
 * @BelongsClassName: TestJob
 * @Author: yuanql
 * @CreateTime: 2023-07-24  16:43
 * @Description: test
 * @Version: 1.0
 */


@DisallowConcurrentExecution // 禁用并发执行
public class DailyTrainJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

    @Resource
    private BusinessFeign businessFeign;
//
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));

        LOG.info("生成15天后的车次数据开始");
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 14);
        Date jdkDate = dateTime.toJdkDate();
        CommonResp<Object> commonResp = businessFeign.genDaily(jdkDate);
        LOG.info("生成每日车次数据结束, 结果：{}", commonResp);
        LOG.info("生成15天后的车次数据结束");
    }
}
