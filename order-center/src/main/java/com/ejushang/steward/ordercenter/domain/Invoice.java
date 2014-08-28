package com.ejushang.steward.ordercenter.domain;

import com.ejushang.steward.common.domain.Receiver;
import com.ejushang.steward.common.domain.util.EntityClass;
import com.ejushang.steward.ordercenter.constant.SfConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "t_invoice")
@Entity
public class Invoice implements EntityClass<Integer>{

    private Integer id;

    private Receiver receiver;

    private String shippingNo;

    private String shippingComp;

   private SfConstant.SF_EXPRESS_TYPE sfExpressType;
  //目的地编码
    private String destCode;
   //寄送方编码
    private String originCode;
   @Column(name="dest_code")
    public String getDestCode() {
        return destCode;
    }

    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }
@Column(name="origin_code")
    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Column(name = "shipping_no")
    @Basic
    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }


    @Column(name = "shipping_comp")
    @Basic
    public String getShippingComp() {
        return shippingComp;
    }

    public void setShippingComp(String shippingComp) {
        this.shippingComp = shippingComp;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
    @Column(name = "sf_express_type")
    @Basic
    public SfConstant.SF_EXPRESS_TYPE getSfExpressType() {
        return sfExpressType;
    }

    public void setSfExpressType(SfConstant.SF_EXPRESS_TYPE sfExpressType) {
        this.sfExpressType = sfExpressType;
    }

    /**
     * 拆单的时候复制发货信息
     * @return
     */
    public Invoice copyForSplit() {
        Invoice copiedInvoice = new Invoice();
        copiedInvoice.setReceiver(getReceiver().clone());
        copiedInvoice.setShippingComp(getShippingComp());
        copiedInvoice.setShippingNo(getShippingNo());
        return copiedInvoice;

    }
}
