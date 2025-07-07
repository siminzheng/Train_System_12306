package com.jiawa.train.business.feign;

import com.jiawa.train.common.req.MemberTicketReq;
import com.jiawa.train.common.response.CommonResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.business.feign
 * @BelongsClassName: MemberFeign
 * @Author: yuanql
 * @CreateTime: 2023-07-31  19:41
 * @Description:
 * @Version: 1.0
 */

@FeignClient(name = "membner", url = "http://localhost:8001/member/")
public interface MemberFeign {

    @GetMapping("/feign/ticket/save")
    CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req);

}
