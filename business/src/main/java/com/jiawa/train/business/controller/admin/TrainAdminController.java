package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.TrainQueryReq;
import com.jiawa.train.business.req.TrainSaveReq;
import com.jiawa.train.business.resp.TrainQueryResp;
import com.jiawa.train.business.service.TrainSeatService;
import com.jiawa.train.business.service.TrainService;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @Resource
    private TrainSeatService trainSeatService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req){

        trainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> query(@Valid TrainQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<TrainQueryResp> list = trainService.querList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        List<TrainQueryResp> trainQueryResps = trainService.querAll();
        return new CommonResp<>(trainQueryResps);
    }


    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);
        return new CommonResp<>();
    }


}
