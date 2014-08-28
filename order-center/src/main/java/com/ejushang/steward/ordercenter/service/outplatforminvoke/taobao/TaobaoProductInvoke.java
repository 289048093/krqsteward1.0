package com.ejushang.steward.ordercenter.service.outplatforminvoke.taobao;

import com.ejushang.steward.common.Application;
import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.openapicenter.tb.api.ProductApi;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.Product;
import com.ejushang.steward.ordercenter.domain.ProductPlatform;
import com.ejushang.steward.ordercenter.service.ProductPlatformService;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.AbstractProductInvoke;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ApiInvokeException;
import com.ejushang.steward.ordercenter.service.transportation.ProductService;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.domain.Item;
import com.taobao.api.domain.Sku;
import com.taobao.api.response.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品的淘宝API调用类<p/>
 * 淘宝商品Item对应智库诚的同一产品编码的一系列产品，如果一个产品编码（outerProductNo)下有多个条形码(SKU),则以多规格的形式处理，
 * 每个具体的商品对应<a href='http://api.taobao.com/apidoc/dataStruct.htm?path=cid:4-dataStructId:17-apiId:164-invokePath:skus'>淘宝的SKU</a>
 * <br/>
 * 智库诚Product的外部平台商品编码（outerProductNo） 为淘宝的Item的商家编码(outer_id);<br/>
 * 智库诚Product的条形码（sku） 为淘宝Item下的SKU的商家编码(outer_id)。
 * User:  Sed.Lee(李朝)
 * Date: 14-5-29
 * Time: 上午9:48
 */
@Component
public class TaobaoProductInvoke extends AbstractProductInvoke {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPlatformService productPlatformService;

    @Override
    public PlatformType getType() {
        return PlatformType.TAO_BAO;
    }

    @Override
    public String getShopUid() {
        return "1675300784";
    }

    /**
     * 从淘宝同步数据到智库诚
     * <p/>
     * 同步一口价、促销价、产品链接
     *
     * @param outerProductNo
     * @param sku
     */
    @Override
    public void downSyncProductInfo(String outerProductNo, String sku, ProductPlatform productPlatform) throws ApiInvokeException {
        // valid args
        outerProductNo = validParams(outerProductNo, sku);
        Item item = findItemByProductNo(outerProductNo);
        copyProperties(item, sku, productPlatform);
    }

    /**
     * 产品下架
     * <p/>
     * 如果产品下没有其他规格，直接下架产品，如果有其他规格的，则把此规格的sku库存数量设置为0
     *
     * @param outerProductNo
     * @param sku
     */
    @Override
    public void productDelisting(String outerProductNo, String sku) throws ApiInvokeException {
        operateItem(outerProductNo, sku, new ItemHandler() {
                    @Override
                    public void operate(Item item) throws ApiException {
                        if (!isOnSale(item)) {
                            return;
                        }
                        ProductApi api = new ProductApi(getShopAuth().getSessionKey());
                        ItemUpdateDelistingResponse response = api.itemUpdateDelisting(item.getNumIid());
                        assertSuccess(response);
                    }
                }, new TaobaoSkuHandler() {
                    @Override
                    public void operate(Item item, Sku taobaoSku) throws ApiException {
                        if (!isOnSale(item)) {
                            return;
                        }
                        try {
                            updateSkuQuantity(item.getNumIid(), taobaoSku.getSkuId() + ":0");  //把产品库存置为0则自动下架
                        } catch (ApiException e) {
                            throw new ApiException(e.getMessage(), e);
                        }
                    }
                }
        );
    }

    /**
     * 产品上架
     *
     * @param outerProductNo
     * @param sku
     */
    @Override
    public void productListing(String outerProductNo, String sku) throws ApiInvokeException {
        final Integer storageNum = productPlatformService.getPlatformRealStorage(sku, getType());
        if (storageNum <= 0) {//库存为0则不做上架操作
            return;     //TODO   是否终止上架操作
        }
        operateItem(outerProductNo, sku, new ItemHandler() {
                    @Override
                    public void operate(Item item) throws ApiException {
                        try {
                            listingItem(item.getNumIid(), Long.valueOf(storageNum));
                        } catch (Exception e) {
                            String errorMsg = (e.getCause() instanceof SocketTimeoutException) ? "网络连接超时，请重试" : e.getMessage();
                            throw new ApiException(errorMsg, e);
                        }
                    }
                }, new TaobaoSkuHandler() {
                    @Override
                    public void operate(Item item, Sku taobaoSku) throws ApiException {
                        try {
                            String skuidQuantity = taobaoSku.getSkuId() + ":" + storageNum;
                            updateSkuQuantity(item.getNumIid(), skuidQuantity);
                            if (!isOnSale(item)) {//如果商品未上架，则先上架商品，否则，更新商品数量
                                listingItem(item.getNumIid(), item.getNum());
                                List<Sku> skus = item.getSkus();   //多个sku的产品 当Ware已经下架的时候 单独上架一个SKU,则其他SKU得设置库存为0 ,避免其他sku也上架的情况
                                for (Sku tbSku : skus) {
                                    if (!taobaoSku.getSkuId().equals(tbSku.getSkuId())) {
                                        updateSkuQuantity(item.getNumIid(), tbSku.getSkuId() + ":0");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new ApiException(e.getMessage(), e);
                        }
                    }
                }
        );
    }

    /**
     * 更新淘宝平台库存
     * <p/>
     * <b>更新库存前，先判断是否上架，如果是没有上架，则不更新库存</b>
     *
     * @param outerProductNo
     * @param sku
     */
    @Override
    @Transactional(readOnly = true)
    public void updateProductPlatformStorage(String outerProductNo, String sku, ProductPlatform productPlatform) throws ApiInvokeException {
        if (productPlatform == null) {
            productPlatform = productPlatformService.findBuProductSkuAndPlatformType(sku, getType());
        }
        if (!productPlatform.getPutaway()) {//如果不上架，则不修改库存
            return;
        }
        Integer storageNum = productPlatformService.getPlatformRealStorage(sku, getType());

        final long updateNum = storageNum < 0 ? 0L : Long.valueOf(storageNum);//如果库存买超了，则改为0，下架     //TODO 是直接下架 还是 抛出异常信息？
//        if (storageNum < 0) {
//            throw new ApiInvokeException(String.format("Sku为[%s]的产品库存小于零，不能同步库存到淘宝，建议进行入库操作", sku));
//        }
        productPlatform.setStorageNum(storageNum);
        operateItem(outerProductNo, sku, new ItemHandler() {
                    @Override
                    public void operate(Item item) throws ApiException {
                        if (item.getNum() != null && item.getNum().equals(updateNum)) {
                            return;
                        }
                        updateItemNum(item.getNumIid(), updateNum);
                    }
                }, new TaobaoSkuHandler() {
                    @Override
                    public void operate(Item item, Sku taobaoSku) throws ApiException {
                        if (taobaoSku.getQuantity() != null && taobaoSku.getQuantity().equals(updateNum)) {
                            return;
                        }
                        updateSkuQuantity(item.getNumIid(), taobaoSku.getSkuId() + ":" + updateNum);
                    }
                }
        );
        generalDAO.saveOrUpdate(productPlatform);
    }

    /**
     * taobao api更新Item库存
     *
     * @param num_iid
     * @param num
     * @throws ApiException
     */
    private void updateItemNum(Long num_iid, Long num) throws ApiException {
        ProductApi api = getProductApi();
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("num_iid", num_iid);
        args.put("num", num);
        ItemUpdateResponse response = null;
        try {
            response = api.itemUpdate(args);
        } catch (Exception e) {
            String errorMsg = (e.getCause() instanceof SocketTimeoutException) ? "网络连接超时，请重试" : e.getMessage();
            throw new ApiException(errorMsg, e);
        }
        if (response != null) {
            assertSuccess(response);
        }
    }

    /**
     * 淘宝商品操作
     * <p/>
     * 判断Item下是否有其他规格，如果无，则对Item直接调用ItemHandler,否则对Item下的SKU调用TaobaoSkuHandler
     *
     * @param outerProductNo
     * @param sku
     * @param itemHandler
     * @param skuHandler
     * @throws ApiException
     */
    private void operateItem(String outerProductNo, String sku, ItemHandler itemHandler, TaobaoSkuHandler skuHandler) throws ApiInvokeException {
        outerProductNo = validParams(outerProductNo, sku);
        Item item = findItemByProductNo(outerProductNo);
        List<Sku> skuList = item.getSkus();
        if (skuList == null || skuList.isEmpty()) {  //Item下面没有更多taobaoSKU，则Item即要查找的商品
            if (!outerProductNo.equals(sku)) { // Item下面没有更多taobaoSKU,则产品编码和条形码应该是一致的
                throw new ApiInvokeException("淘宝没有找到该商品，请检查外部平台产品编码的正确性");
            }
            try {
                itemHandler.operate(item);
            } catch (ApiException e) {
                throw new ApiInvokeException(e.getMessage(), e);
            }
        } else {
            Sku destSku = null;
            for (Sku tbSku : item.getSkus()) {
                if (sku.equals(tbSku.getOuterId())) {  //Item下有多种规格（taobao SKU),则要比对Item下的每个规格的outer_id
                    destSku = tbSku;
                    break;
                }
            }
            if (destSku == null) {
                throw new ApiInvokeException(String.format("淘宝没有找到该sku的商品，请检查外部平台产品编码和sku正确性，可能是存在多个相同商家编码为[%s]的产品，商品链接：%s", outerProductNo, generateURL(item.getNumIid())));
            }
            try {
                skuHandler.operate(item, destSku);
            } catch (ApiException e) {
                throw new ApiInvokeException(e.getMessage(), e);
            }
        }
    }

    /**
     * 淘宝商品上架
     *
     * @param numIid
     * @param num
     * @throws Exception
     */
    private void listingItem(Long numIid, Long num) throws Exception {
        if (num < 0) {
            return;
        }
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("num_iid", numIid);
        args.put("num", num);
        ItemUpdateListingResponse response = getProductApi().itemUpdateListing(args);
        assertSuccess(response);
    }

    /**
     * 是否已经下架
     *
     * @param item
     * @return
     */
    private boolean isOnSale(Item item) {
        return TaobaoProductStatus.onsale.name().equals(item.getApproveStatus());
    }

    /**
     * 淘宝规格商品数量修改
     *
     * @param numIid taobao的Item.numIid
     * @param skuNum taobao的库存properties   skuId:storageNum
     * @return
     * @throws Exception
     */
    private Item updateSkuQuantity(Long numIid, String skuNum) throws ApiException {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("num_iid", numIid);
        args.put("skuid_quantities", skuNum);
        SkusQuantityUpdateResponse skusQuantityUpdateResponse = null;
        try {
            skusQuantityUpdateResponse = getProductApi().updateSkusQuantity(args);
        } catch (Exception e) {
            String errorMsg = (e.getCause() instanceof SocketTimeoutException) ? "网络连接超时，请重试" : e.getMessage();
            throw new ApiException(errorMsg, e);
        }
        assertSuccess(skusQuantityUpdateResponse);
        return skusQuantityUpdateResponse.getItem();
    }

    /**
     * 验证参数
     *
     * @param outerProductNo
     * @param sku
     * @return outerProductNo
     */
    private String validParams(String outerProductNo, String sku) throws ApiInvokeException {
        if (StringUtils.isBlank(sku)) {
            throw new ApiInvokeException("参数sku不能为空");
        }
        if (StringUtils.isBlank(outerProductNo)) {
            Product product = productService.findProductBySKU(sku);
            if (product == null) {
                throw new ApiInvokeException("没有找到该sku的产品");
            }
            outerProductNo = product.getOuterProductNo();
        }
        return outerProductNo;
    }

    /**
     * 拷贝淘宝商品属性到系统商品平台
     *
     * @param item
     * @param sku
     * @param productPlatform
     */
    private void copyProperties(Item item, String sku, ProductPlatform productPlatform) {
        List<Sku> skuList = item.getSkus();
        if (skuList == null || skuList.isEmpty()) {//没有其他规格
            productPlatform.setPrice(Money.valueOf(item.getPrice()));
            Long itemNum = item.getNum();
            productPlatform.setStorageNum(itemNum == null ? 0 : itemNum.intValue());
            //TODO 促销价
        } else {
            for (Sku tbSku : skuList) {
                if (tbSku.getOuterId().equals(sku)) {
                    productPlatform.setPrice(Money.valueOf(tbSku.getPrice()));
                    Long skuNum = tbSku.getQuantity();
                    productPlatform.setStorageNum(skuNum == null ? 0 : skuNum.intValue());
                    //TODO 促销价
//                productPlatform
                }
            }
        }
        Long numIid = item.getNumIid();
        if (numIid != null) {
            productPlatform.setPlatformUrl(generateURL(numIid));
        }
    }

    /**
     * 通过产品编码查询到天猫的商品
     *
     * @param outerProductNo
     * @return
     */
    private Item findItemByProductNo(String outerProductNo) throws ApiInvokeException {
        ProductApi api = getProductApi();
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("outer_id", outerProductNo);
        args.put("fields", "num_iid,num,price,sku,list_time,delist_time,approve_status");
        ItemsCustomGetResponse response = null;
        try {
            response = api.itemsCustomGet(args);
        } catch (Exception e) {
            String errorMsg = (e.getCause() instanceof SocketTimeoutException) ? "网络连接超时，请重试" : e.getMessage();
            throw new ApiInvokeException("调用淘宝查询商品API出错：" + errorMsg, e);
        }
        List<Item> itemList = response.getItems();
        if (itemList == null || itemList.isEmpty()) {
            throw new ApiInvokeException(String.format("淘宝上没有找到外部平台产品编号为[%s]的相关产品信息,或者存在多个该商家编码的商品", outerProductNo));
        }
        assertSuccess(response);
        return itemList.get(0);
    }

    /**
     * 通过产品条形码（sku)查找淘宝商品sku信息
     * <p/>
     * 当该产品sku下没有其他sku时，查询结果为null，如邮费补差这类没有其他规格的产品
     *
     * @param sku
     * @return
     * @throws Exception
     */
    private Sku findTaobaoSkuByProductSku(String sku) throws Exception {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("outer_id", sku);
        args.put("fields", "sku_id,num_iid,quantity,price,status");
        List<Sku> taobaoSkus = getProductApi().getSkusCustom(args);
        return taobaoSkus != null ? taobaoSkus.get(0) : null;
    }

    /**
     * 获取产品接口
     * <p/>
     * 与session绑定，一个session一个API instance，因为API的sessionKey是有有效期的
     *
     * @return
     */
    private ProductApi getProductApi() {
        String key = "TAOBAO_PRODUCT_API";
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        ProductApi api = (ProductApi) session.getAttribute(key);
        if (api == null) {
            api = new ProductApi(getShopAuth().getSessionKey());
            session.setAttribute(key, api);
        }
        return api;
    }

    /**
     * 生成链接地址
     *
     * @param numIid
     * @return
     */
    private String generateURL(Long numIid) {
        return String.format(StringUtils.trimToEmpty(getUriPattern()), numIid);
    }

    /**
     * 获取链接模版
     *
     * @return
     */
    private String getUriPattern() {
        return Application.getInstance().getConfigValue(Application.PropertiesKey.TAOBAO_ITEM_URI_PATTERN.value);
    }

    /**
     * 判断请求是否有错误
     *
     * @param response
     * @return
     */
    private void assertSuccess(TaobaoResponse response) {
        if (StringUtils.isNotBlank(response.getErrorCode())) {
            String errorMsg = StringUtils.isBlank(response.getSubMsg()) ? response.getMsg() : response.getSubMsg();
            throw new StewardBusinessException("淘宝产品信息同步异常：" + errorMsg);
        }
    }


    /**
     * /**
     * User:  Sed.Lee(李朝)
     * Date: 14-5-30
     * Time: 上午10:15
     */
    private static interface ItemHandler {
        void operate(Item item) throws ApiException;
    }

    /**
     * User:  Sed.Lee(李朝)
     * Date: 14-5-30
     * Time: 上午10:30
     */
    private static interface TaobaoSkuHandler {

        void operate(Item item, Sku taobaoSku) throws ApiException;
    }
}
