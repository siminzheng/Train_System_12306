package com.jiawa.train.business.req;

import com.jiawa.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {

    private String code;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "code='" + code + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }
}
