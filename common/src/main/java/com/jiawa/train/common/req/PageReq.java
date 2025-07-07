package com.jiawa.train.common.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.common.request
 * @BelongsClassName: PageReq
 * @Author: yuanql
 * @CreateTime: 2023-07-21  11:24
 * @Description: 查询分页
 * @Version: 1.0
 */


public class PageReq {

    @NotNull(message = "【页码】不能为空")
    private Integer page;

    @NotNull(message = "【页码】不能为空")
    @Max(value = 100, message = "【每页条数】不能超过100")
    private Integer size;


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageReq{");
        sb.append("page=").append(page);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
