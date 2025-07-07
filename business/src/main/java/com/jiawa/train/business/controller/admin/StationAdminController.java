package com.jiawa.train.business.controller.admin;

import com.jiawa.train.business.req.StationQueryReq;
import com.jiawa.train.business.req.StationSaveReq;
import com.jiawa.train.business.resp.StationQueryResp;
import com.jiawa.train.business.service.StationService;
import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req){

        stationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> query(@Valid StationQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<StationQueryResp> list = stationService.querList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }


    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        List<StationQueryResp> stationQueryResps = stationService.querAll();
        return new CommonResp<>(stationQueryResps);
    }


}
