package com.jiawa.train.business.controller.admin;

import com.jiawa.train.common.response.CommonResp;
import com.jiawa.train.common.response.PageResp;
import com.jiawa.train.business.req.ConfirmOrderQueryReq;
import com.jiawa.train.business.req.ConfirmOrderDoReq;
import com.jiawa.train.business.resp.ConfirmOrderQueryResp;
import com.jiawa.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;



    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req){

        confirmOrderService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> query(@Valid ConfirmOrderQueryReq req){

        //req.setMemberId(LoginMemberContext.getId());
        PageResp<ConfirmOrderQueryResp> list = confirmOrderService.querList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }


}
