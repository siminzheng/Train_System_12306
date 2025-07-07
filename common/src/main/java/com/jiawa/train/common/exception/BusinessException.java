package com.jiawa.train.common.exception;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.common.exception
 * @BelongsClassName: BusinessException
 * @Author: yuanql
 * @CreateTime: 2023-07-09  22:12
 * @Description: 业主相关异常的 统一处理方法
 * @Version: 1.0
 */


public class BusinessException extends RuntimeException{

    private BusinessExceptionEnum businessExceptionEnum;

    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    public BusinessExceptionEnum getBusinessExceptionEnum() {
        return businessExceptionEnum;
    }

    public void setBusinessExceptionEnum(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    /**
     *  不写入堆栈信息，提高性能
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }


}
