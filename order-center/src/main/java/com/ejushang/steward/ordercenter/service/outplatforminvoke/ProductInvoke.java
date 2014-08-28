package com.ejushang.steward.ordercenter.service.outplatforminvoke;

import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.ProductPlatform;
import com.taobao.api.ApiException;

/**
 * User:  Sed.Lee(李朝)
 * Date: 14-5-29
 * Time: 上午9:52
 */
public interface ProductInvoke {

    /**
     * 获取平台类型
     *
     * @return 平台类型
     */
    PlatformType getType();

    /**
     * 店铺的Uid ，只考虑主店铺
     * @return
     */
    String getShopUid();

    /**
     * 从外部平台同步数据
     *
     * @param outerProductNo 可选，因为淘宝需要通过产品编码定位产品大类。如果不填，则调用淘宝Api时会到数据库通过sku查找产品编码
     * @param sku
     */
    void downSyncProductInfo(String outerProductNo, String sku, ProductPlatform productPlatform) throws ApiInvokeException;

    /**
     * 商品下架
     *
     * @param outerProductNo 可选，因为淘宝需要通过产品编码定位产品大类。如果不填，则调用淘宝Api时会到数据库通过sku查找产品编码
     * @param sku
     * @throws ApiException
     */
    void productDelisting(String outerProductNo, String sku) throws ApiInvokeException;

    /**
     * 商品上架
     *
     * @param outerProductNo 可选，因为淘宝需要通过产品编码定位产品大类。如果不填，则调用淘宝Api时会到数据库通过sku查找产品编码
     * @param sku
     * @throws ApiException
     */
    void productListing(String outerProductNo, String sku) throws ApiInvokeException;

    /**
     * 更新淘宝平台库存(按产品平台定义的百分比，或者按平台百分比)
     * <p/>
     * <b>更新库存前，先判断是否上架，如果是没有上架，则不更新库存</b>
     *
     * @param outerProductNo       可选，因为淘宝需要通过产品编码定位产品大类。如果不填，则调用淘宝Api时会到数据库通过sku查找产品编码
     * @param sku
     * @param productPlatform 可选，如果为null，则根据sku和platformType查询
     * @throws ApiException
     */
    void updateProductPlatformStorage(String outerProductNo, String sku, ProductPlatform productPlatform) throws ApiInvokeException;
}
