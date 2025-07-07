package com.jiawa.train.batch.feign;

import com.jiawa.train.common.response.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.batch.fegin
 * @BelongsClassName: BusinessFeign
 * @Author: yuanql
 * @CreateTime: 2023-07-26  20:52
 * @Description: 业务类的fegin调用
 * @Version: 1.0
 */


//@FeignClient(name = "business", url = "http://localhost:8002/business")
@FeignClient("business")
public interface BusinessFeign {

    @GetMapping("/business/hello")
    String Hello();

    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
