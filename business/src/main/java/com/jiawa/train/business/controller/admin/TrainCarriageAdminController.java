package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.context.LoginMemberContext;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.business.req.TrainCarriageQueryReq;
import com.jiawa.train.business.req.TrainCarriageSaveReq;
import com.jiawa.train.business.resp.TrainCarriageQueryResp;
import com.jiawa.train.business.service.TrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Resource
    private TrainCarriageService trainCarriageService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req){

        trainCarriageService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> query(@Valid TrainCarriageQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<TrainCarriageQueryResp> list = trainCarriageService.querList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }


}
