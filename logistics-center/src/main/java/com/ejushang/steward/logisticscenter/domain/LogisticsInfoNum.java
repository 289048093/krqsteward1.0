package com.ejushang.steward.logisticscenter.domain;

/**
 * User: JBoss.WU
 * Date: 14-8-12
 * Time: 下午2:31
 * To change this template use File | Settings | File Templates.
 */
public class LogisticsInfoNum {
    //物流编号
    private String shippingNo;
    //进入顺丰返回的订单No
   private String orderNo;
      //原先订单OrderNo
   private String firstOrderNo;

    //目的地编码
    private String destCode;
    //寄送方编码
    private String originCode;

    private Exception exception;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }



    public String getDestCode() {
        return destCode;
    }

    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getFirstOrderNo() {
        return firstOrderNo;
    }

    public void setFirstOrderNo(String firstOrderNo) {
        this.firstOrderNo = firstOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }
}
