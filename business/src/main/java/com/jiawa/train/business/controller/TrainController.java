package com.jiawa.train.business.controller;

import com.jiawa.train.business.resp.TrainQueryResp;
import com.jiawa.train.business.service.TrainService;
import com.jiawa.train.common.response.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        List<TrainQueryResp> trainQueryResps = trainService.querAll();
        return new CommonResp<>(trainQueryResps);
    }

}
