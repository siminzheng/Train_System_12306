package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.DailyTrainTicketQueryReq;
import com.jiawa.train.business.req.DailyTrainTicketSaveReq;
import com.jiawa.train.business.resp.DailyTrainTicketQueryResp;
import com.jiawa.train.business.service.DailyTrainTicketService;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req){

        dailyTrainTicketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> query(@Valid DailyTrainTicketQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.querList(req);

        return new CommonResp<>(list);
    }


    @GetMapping("/query-list2")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList2(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList2(req);
        return new CommonResp<>(list);
    }

    @GetMapping("/query-list3")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList3(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList3(req);
        return new CommonResp<>(list);
    }


    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }


}
