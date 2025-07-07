package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.business.req.DailyTrainStationQueryReq;
import com.jiawa.train.business.req.DailyTrainStationSaveReq;
import com.jiawa.train.business.resp.DailyTrainStationQueryResp;
import com.jiawa.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Resource
    private DailyTrainStationService dailyTrainStationService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req){

        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> query(@Valid DailyTrainStationQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<DailyTrainStationQueryResp> list = dailyTrainStationService.querList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }


}
