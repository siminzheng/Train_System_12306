package com.jiawa.train.batch.controller;

import com.jiawa.train.batch.feign.BusinessFeign;
import com.jiawa.train.batch.job.DailyTrainJob;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/business")
public class TestController {

    @Resource
    private BusinessFeign businessFeign;

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

    @GetMapping("/hello")
    public String hello() {

        String businessHelo = businessFeign.Hello();

        LOG.info(businessHelo);
        return "Hello World Batch!" + businessHelo;
    }
}
