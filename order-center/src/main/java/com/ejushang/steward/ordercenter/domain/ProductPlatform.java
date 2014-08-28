package com.ejushang.steward.ordercenter.domain;

import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.common.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

/**
 * User: 龙清华
 * Date: 14-4-16
 * Time: 下午5:25
 */
@javax.persistence.Table(name = "t_prod_platform")
@Entity
public class ProductPlatform {
    private Integer id;
    /**
     * 平台ID
     */
    private Integer platformId;

    private Platform platform;
    /**
     * 商品ID
     */
    private Integer prodId;

    private Product product;
    /**
     * 一口价，掉牌价
     */
    private Money price;
    /**
     * 促销价
     */
    private Money discountPrice;
    /**
     * 库存占比，如果设置将覆写掉t_platform的占比值
     */
    private Integer storagePercent;
    /**
     * 库存数量
     */
    private Integer storageNum;
    /**
     * 是否上架
     */
    private boolean isPutaway = false;
    /**
     * 同步状态
     */
    private boolean synStatus = false;
    /**
     * 平台连接地址
     */
    private String platformUrl;


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", insertable = false, updatable = false)
    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @javax.persistence.Column(name = "platform_id")
    @Basic
    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    @javax.persistence.Column(name = "platform_url")
    @Basic
    public String getPlatformUrl() {
        return platformUrl;
    }

    public void setPlatformUrl(String platformUrl) {
        this.platformUrl = platformUrl;
    }

    @javax.persistence.Column(name = "prod_id")
    @Basic
    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id", insertable = false, updatable = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    @javax.persistence.Column(name = "price")
    @Basic
    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }


    @javax.persistence.Column(name = "discount_price")
    @Basic
    public Money getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Money discountPrice) {
        this.discountPrice = discountPrice;
    }


    @javax.persistence.Column(name = "storage_percent")
    @Basic
    public Integer getStoragePercent() {
        return storagePercent;
    }

    public void setStoragePercent(Integer storagePercent) {
        this.storagePercent = storagePercent;
    }


    @javax.persistence.Column(name = "storage_num")
    @Basic
    public Integer getStorageNum() {
        return storageNum;
    }

    public void setStorageNum(Integer storageNum) {
        this.storageNum = storageNum;
    }

    @javax.persistence.Column(name = "is_putaway")
    @Basic
    public boolean getPutaway() {
        return isPutaway;
    }

    public void setPutaway(boolean putaway) {
        isPutaway = putaway;
    }

    @javax.persistence.Column(name = "syn_status")
    @Basic
    public boolean getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(boolean synStatus) {
        this.synStatus = synStatus;
    }

    @Override
    public String toString() {
        return "ProductPlatform{" +
                "id=" + id +
                ", platformId=" + platformId +
                ", platform=" + platform +
                ", prodId=" + prodId +
                ", product=" + product +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                ", storagePercent=" + storagePercent +
                ", storageNum=" + storageNum +
                ", isPutaway=" + isPutaway +
                ", synStatus=" + synStatus +
                ", platformUrl='" + platformUrl + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProductPlatform) {
            ProductPlatform tmp = (ProductPlatform) obj;
            return NumberUtil.equals(tmp.getId(), getId())
                    && getPutaway() == tmp.getPutaway()
                    && getSynStatus() == tmp.getSynStatus()
                    && NumberUtil.equals(getStoragePercent(), tmp.getStoragePercent())
                    && StringUtils.equals(getPlatformUrl(), tmp.getPlatformUrl())
                    && (getDiscountPrice() != null && getDiscountPrice().equals(tmp.getDiscountPrice())) || (getDiscountPrice() == tmp.getDiscountPrice())
                    && NumberUtil.equals(getPlatformId(), tmp.getPlatformId())
                    && NumberUtil.equals(getProdId(), tmp.getProdId())
                    && NumberUtil.equals(getStorageNum(), tmp.getStorageNum());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getId() << 5) - getId() ;
    }

}
