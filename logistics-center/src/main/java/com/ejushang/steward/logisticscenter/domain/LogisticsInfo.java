package com.ejushang.steward.logisticscenter.domain;

import com.ejushang.steward.common.domain.util.EntityClass;
import com.ejushang.steward.common.domain.util.OperableData;
import com.ejushang.steward.common.hibernate.UglyTimestampUtil;

import javax.persistence.*;
import java.util.Date;

/**
 * User: liubin
 * Date: 14-3-28
 */
@javax.persistence.Table(name = "t_logistics_info")
@Entity
public class LogisticsInfo implements EntityClass<Integer>, OperableData {

    private Integer id;

    private String orderNo;

    private String expressNo;

    private String expressCompany;

    private String sendTo;

    private String expressInfo;

    private Boolean expressStatus;

    private Date firstTime;

    private Date latestTime;

    private Boolean wasRequest;

    private Date createTime;

    private Date updateTime;



    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @javax.persistence.Column(name = "order_no")
    @Basic
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }


    @javax.persistence.Column(name = "express_no")
    @Basic
    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }


    @javax.persistence.Column(name = "express_company")
    @Basic
    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }


    @javax.persistence.Column(name = "send_to")
    @Basic
    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }


    @javax.persistence.Column(name = "express_info")
    @Basic
    public String getExpressInfo() {
        return expressInfo;
    }

    public void setExpressInfo(String expressInfo) {
        this.expressInfo = expressInfo;
    }


    @javax.persistence.Column(name = "express_status")
    @Basic
    public Boolean getExpressStatus() {
        return expressStatus;
    }

    public void setExpressStatus(Boolean expressStatus) {
        this.expressStatus = expressStatus;
    }


    @javax.persistence.Column(name = "first_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = UglyTimestampUtil.convertTimestampToDate(firstTime);
    }


    @javax.persistence.Column(name = "latest_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(Date latestTime) {
        this.latestTime = UglyTimestampUtil.convertTimestampToDate(latestTime);
    }


    @javax.persistence.Column(name = "was_request")
    @Basic
    public Boolean getWasRequest() {
        return wasRequest;
    }

    public void setWasRequest(Boolean wasRequest) {
        this.wasRequest = wasRequest;
    }


    @javax.persistence.Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = UglyTimestampUtil.convertTimestampToDate(createTime);
    }


    @javax.persistence.Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setOperatorId(Integer operatorId) {
    }

    @Override
    @Transient
    public Integer getOperatorId() {
        return null;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = UglyTimestampUtil.convertTimestampToDate(updateTime);
    }

}
