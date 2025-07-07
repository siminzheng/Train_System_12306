package com.jiawa.train.business.controller;

import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.DailyTrainTicketService;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> dailyTrainTicketResps = dailyTrainTicketService.querList(req);
        return new CommonResp<>(dailyTrainTicketResps);
    }

}
