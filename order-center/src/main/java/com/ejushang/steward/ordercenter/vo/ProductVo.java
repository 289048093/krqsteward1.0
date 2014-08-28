package com.ejushang.steward.ordercenter.vo;

import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.ordercenter.constant.ProductLocation;
import com.ejushang.steward.ordercenter.constant.ProductStyle;
import com.ejushang.steward.ordercenter.domain.Brand;
import com.ejushang.steward.ordercenter.domain.ProductCategory;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Joyce.qu
 * Date: 14-5-14
 * Time: 下午1:43
 * To change this template use File | Settings | File Templates.
 */
public class ProductVo {
    private Integer id;

    private String name;
    /**
     * 商品编号
     */
    private String productNo;
    /**
     * 商品条形码
     */
    private String sku;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 图片地址
     */
    private String picUrl;

    /**
     * 市场价
     */
    private String marketPrice;
    /**
     * 现价*
     */
    private String importPrice;
    /**
     * '最低价'*
     */
    private String minimumPrice;
    /**
     * 颜色
     */
    private String color;
    /**
     * 重量
     */
    private String weight;
    /**
     * 包装尺寸
     */
    private String boxSize;
    /**
     * 规格
     */
    private String speci;
    /**
     * 删除标志
     */
    private String deleted;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 品牌ID
     */
    private String brandId;
    /**
     * 品牌对象
     */
    private BrandVo brand;
    /**
     * 产品分类ID
     */
    private String categoryId;
    /**
     * 产品分类对象
     */
    private ProductCategoryVo category;
    /**
     * 产地
     */
    private String orgin;
    /**
     * 产品类型 如：爆款等
     */
    private String style;
    /**
     * 库位赠品 如：正常.缺位等
     */
    private String location;

    private String operatorName;

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(String importPrice) {
        this.importPrice = importPrice;
    }

    public String getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(String minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(String boxSize) {
        this.boxSize = boxSize;
    }

    public String getSpeci() {
        return speci;
    }

    public void setSpeci(String speci) {
        this.speci = speci;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public BrandVo getBrand() {
        return brand;
    }

    public void setBrand(BrandVo brand) {
        this.brand = brand;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ProductCategoryVo getCategory() {
        return category;
    }

    public void setCategory(ProductCategoryVo category) {
        this.category = category;
    }

    public String getOrgin() {
        return orgin;
    }

    public void setOrgin(String orgin) {
        this.orgin = orgin;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
