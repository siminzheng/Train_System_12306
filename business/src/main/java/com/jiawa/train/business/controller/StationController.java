package com.jiawa.train.business.controller;

import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.business.service.StationService;
import com.jiawa.train.common.response.CommonResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {

    @Resource
    private StationService stationService;


    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        List<StationQueryResp> stationQueryResps = stationService.querAll();
        return new CommonResp<>(stationQueryResps);
    }


}
