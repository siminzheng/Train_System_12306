package com.jiawa.train.business.req;

import com.jiawa.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainStationQueryReq extends PageReq {

    private String trainCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DailyTrainStationQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }
}
