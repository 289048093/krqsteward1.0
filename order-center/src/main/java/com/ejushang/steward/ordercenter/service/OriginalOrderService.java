package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.common.util.NumberUtil;
import com.ejushang.steward.ordercenter.constant.OriginalOrderStatus;
import com.ejushang.steward.ordercenter.constant.PromotionType;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.service.transportation.ProductService;
import com.jd.open.api.sdk.domain.order.CouponDetail;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.sun.management.VMOption;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: Shiro
 * Date: 14-4-17
 * Time: 上午11:57
 */
@Service
@Transactional
public class OriginalOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OriginalOrderService.class);

    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private ProductService productService;

    /**
     * 查询
     *
     * @param originalOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<OriginalOrder> getOriginalOrder(OriginalOrder originalOrder, Date startTime, Date endTime) {
        if (logger.isInfoEnabled()) {
            logger.info(String.format("getOriginalOrder方法参数为originalOrder[{}],startTime[{}],endTime[{}]", originalOrder, startTime, endTime));
        }
        Search search = new Search(OriginalOrder.class);
        if (originalOrder != null) {
            if (StringUtils.isNotBlank(originalOrder.getPlatformOrderNo())) {
                search.addFilterLike("platformOrderNo", "%" + originalOrder.getPlatformOrderNo().trim() + "%");
            }
            if (!NumberUtil.isNullOrZero(originalOrder.getShopId())) {
                search.addFilterEqual("shopId", originalOrder.getShopId());
            }
            if (!NumberUtil.isNullOrZero(originalOrder.getProcessed())) {
                search.addFilterEqual("processed", originalOrder.getProcessed());
            }
            if (!NumberUtil.isNullOrZero(originalOrder.getOutShopId())) {
                search.addFilterEqual("outShopId", originalOrder.getOutShopId());
            }
            if (!NumberUtil.isNullOrZero(originalOrder.getStatus())) {
                search.addFilterEqual("status", originalOrder.getStatus());
            }
            if(originalOrder.getPlatformType() != null){
                search.addFilterEqual("platformType",originalOrder.getPlatformType());
            }
            if (startTime != null) {
                search.addFilterGreaterOrEqual("buyTime", startTime);
            }
            if (endTime != null) {
                search.addFilterLessOrEqual("buyTime", endTime);
            }
        }
        return generalDAO.search(search);
    }

    /**
     * 获取订单、订单项、订单优惠信息
     * @param originalOrder
     * @param startTime
     * @param endTime
     * @return
     */
    @Transactional(readOnly = true)
    public List<OriginalOrder> findOriginalOrderFullinfos(OriginalOrder originalOrder, Date startTime, Date endTime){
        List<OriginalOrder> originalOrderList = getOriginalOrder(originalOrder,startTime,endTime);

        if(CollectionUtils.isNotEmpty(originalOrderList)){
            for(OriginalOrder originalOrderOri : originalOrderList){
                OriginalOrderItem originalOrderItem = new OriginalOrderItem();
                originalOrderItem.setOriginalOrderId(originalOrderOri.getId());
                List<OriginalOrderItem> originalOrderItemList = findOriginalOrderItem(originalOrderItem);
                if(CollectionUtils.isNotEmpty(originalOrderItemList)) {
                    originalOrderOri.setOriginalOrderItemList(originalOrderItemList);
                }

                PromotionInfo promotionInfo = new PromotionInfo();
                promotionInfo.setOriginalOrderId(originalOrderOri.getId());
                List<PromotionInfo> promotionInfoList = findPromotionInfos(promotionInfo);
                if(CollectionUtils.isNotEmpty(promotionInfoList)){
                    originalOrderOri.setPromotionInfoList(promotionInfoList);
                }
            }
        }
        return originalOrderList;
    }

    /**
     * 根据条件检索原始订单项
     * @param originalOrderItem
     * @return
     */
    public List<OriginalOrderItem> findOriginalOrderItem(OriginalOrderItem originalOrderItem){
        Search search = new Search(OriginalOrderItem.class);
        if (originalOrderItem != null) {
            if (NumberUtil.isNotNullOrNotZero(originalOrderItem.getOriginalOrderId())) {
                search.addFilterEqual("originalOrderId", originalOrderItem.getOriginalOrderId());
            }
        }
        return generalDAO.search(search);
    }

    /**
     * 根据条件查询唯一的原始订单
     *
     * @param originalOrder
     * @return
     */
    @Transactional(readOnly = true)
    public OriginalOrder getOriginalOrderByCondition(OriginalOrder originalOrder) {
        Search search = new Search(OriginalOrder.class);
        if (originalOrder != null) {
            if (StringUtils.isNotBlank(originalOrder.getPlatformOrderNo())) {
                search.addFilterEqual("platformOrderNo", originalOrder.getPlatformOrderNo());
            }
        }

        List<OriginalOrder> originalOrderList = generalDAO.search(search);

        return CollectionUtils.isEmpty(originalOrderList) ? null : originalOrderList.get(0);
    }

    /**
     * 根据条件查询唯一的原始订单项
     *
     * @param originalOrderItem
     * @return
     */
    @Transactional(readOnly = true)
    public OriginalOrderItem getOriginalOrderItemByCondition(OriginalOrderItem originalOrderItem) {
        if (logger.isInfoEnabled()) {
            logger.info("getOriginalOrderItemByCondition方法参数为OriginalOrderItem[{}]", originalOrderItem);
        }
        Search search = new Search(OriginalOrderItem.class);
        if (originalOrderItem != null) {
            if (StringUtils.isNotBlank(originalOrderItem.getSku())) {
                search.addFilterEqual("sku", originalOrderItem.getSku());
            }
            if (StringUtils.isNotBlank(originalOrderItem.getPlatformSubOrderNo())) {
                search.addFilterEqual("platformSubOrderNo", originalOrderItem.getPlatformSubOrderNo());
            }
            if (!NumberUtil.isNullOrZero(originalOrderItem.getOriginalOrderId())) {
                search.addFilterEqual("originalOrderId", originalOrderItem.getOriginalOrderId());
            }
        }
        List<OriginalOrderItem> originalOrderItemList = generalDAO.search(search);

        return CollectionUtils.isEmpty(originalOrderItemList) ? null : originalOrderItemList.get(0);
    }

    /**
     * 添加或修改原始订单
     *
     * @param originalOrder
     */
    @Transactional
    public void saveOriginalOrder(OriginalOrder originalOrder) {
        generalDAO.saveOrUpdate(originalOrder);
    }

    /**
     * 添加或修改原始订单项
     *
     * @param originalOrderItem
     */
    @Transactional
    public void saveOriginalOrderItem(OriginalOrderItem originalOrderItem) {
        generalDAO.saveOrUpdate(originalOrderItem);
    }

    /**
     * 添加或修改优惠订单详情
     *
     * @param promotionInfo
     */
    @Transactional
    public void savePromotionInfo(PromotionInfo promotionInfo) {
        generalDAO.saveOrUpdate(promotionInfo);
    }

    /**
     * 删除方法
     *
     * @param id
     */
    @Transactional
    public void deleteOriginalOrder(Integer id) {
        if (logger.isInfoEnabled()) {
            logger.info("deleteOriginalOrder方法参数为id[{}]", id);
        }
        generalDAO.removeById(OriginalOrder.class, id);
    }

    @Transactional(readOnly = true)
    public List<OriginalOrder> findUnprocessedOriginalOrders() {
        Search search = new Search(OriginalOrder.class);
        return generalDAO.search(search.addFilterEqual("processed", false).addFilterEqual("discard", false).addSortAsc("modifiedTime"));
    }

    @Transactional
    public void createAnalyzeLog(OriginalOrder originalOrder, boolean processed, String errorMsg) {
        OrderAnalyzeLog log = new OrderAnalyzeLog();
        log.setCreateTime(new Date());
        log.setOriginalOrderId(originalOrder.getId());
        log.setProcessed(processed);
        log.setMessage(errorMsg);
        generalDAO.saveOrUpdate(log);
    }

    /**
     * 猜测原始订单对应的品牌
     * @param originalOrder
     */
    @Transactional
    public void guessOriginalOrderBrand(OriginalOrder originalOrder, boolean deleteBeforeGuess) {
        try {
            if(deleteBeforeGuess) {
                //先删除原始猜测的记录
                deleteOriginalOrderBrands(originalOrder);
            }

            List<OriginalOrderItem> originalOrderItemList = originalOrder.getOriginalOrderItemList();
            if(originalOrderItemList == null || originalOrderItemList.isEmpty()) {
                return;
            }
            for(OriginalOrderItem originalOrderItem : originalOrderItemList) {
                String sku = originalOrderItem.getSku();
                if(StringUtils.isBlank(sku)) continue;
                Mealset mealset = mealSetService.findBySku(sku);
                if(mealset != null) {
                    for(MealsetItem mealsetItem : mealset.getMealsetItemList()) {
                        Product product = mealsetItem.getProduct();
                        if(product == null || product.getBrandId() == null) {
                            continue;
                        }
                        createOriginalOrderBrand(originalOrder, originalOrderItem, product.getBrandId());
                    }
                } else {
                    Product product = productService.findProductBySKU(sku);
                    if(product == null || product.getBrandId() == null) {
                        continue;
                    }
                    createOriginalOrderBrand(originalOrder, originalOrderItem, product.getBrandId());
                }

            }
        } catch (Exception e) {
            logger.error("猜测原始订单对应品牌的时候发生错误", e);
        }
    }

    @Transactional
    public void guessAllOriginalOrderBrand() {
        List<OriginalOrder> originalOrders = findAll();
        //先删除表中所有记录
        deleteOriginalOrderBrands(null);
        //猜测原始订单品牌
        int i=0;
        for(OriginalOrder originalOrder : originalOrders) {
            guessOriginalOrderBrand(originalOrder, false);
            if(++i % 100 == 0) {
                generalDAO.getSession().flush();
            }
        }

    }

    @Transactional(readOnly = true)
    public List<OriginalOrder> findAll() {
        return generalDAO.findAll(OriginalOrder.class);
    }

    /**
     * 删除原始订单与品牌的关系记录
     * @param originalOrder 如果为null,删除表中所有记录
     */
    private void deleteOriginalOrderBrands(OriginalOrder originalOrder) {
        String sql = "delete from t_original_order_brand ";
        if(originalOrder != null) {
            sql += " where original_order_id = " + originalOrder.getId();
        }
        generalDAO.getSession().createSQLQuery(sql).executeUpdate();

    }

    private void createOriginalOrderBrand(OriginalOrder originalOrder, OriginalOrderItem originalOrderItem, Integer brandId) {
        OriginalOrderBrand originalOrderBrand = new OriginalOrderBrand();
        originalOrderBrand.setOriginalOrderId(originalOrder.getId());
        originalOrderBrand.setOriginalOrderItemId(originalOrderItem.getId());
        originalOrderBrand.setBrandId(brandId);
        generalDAO.saveOrUpdate(originalOrderBrand);
    }

    @Transactional(readOnly = true)
    public OriginalOrder get(Integer id) {
        return generalDAO.get(OriginalOrder.class, id);
    }

    /**
     * 保存指定状态的原始订单至数据库
     * @param originalOrderList
     */
    public void saveOriginalOrders(List<OriginalOrder> originalOrderList) {

        if(CollectionUtils.isEmpty(originalOrderList)){
            return;
        }

        for(OriginalOrder originalOrder : originalOrderList){

            if(!(StringUtils.equalsIgnoreCase(originalOrder.getStatus(), OriginalOrderStatus.WAIT_SELLER_SEND_GOODS.toString())
                    || StringUtils.equalsIgnoreCase(originalOrder.getStatus(), OriginalOrderStatus.WAIT_BUYER_CONFIRM_GOODS.toString())
                    || StringUtils.equalsIgnoreCase(originalOrder.getStatus(), OriginalOrderStatus.TRADE_FINISHED.toString()))
                    ){
                continue;
            }
            // 保存原始订单
            saveOriginalOrder(originalOrder);
            for (OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()) {
                // 保存原始订单项
                originalOrderItem.setOriginalOrderId(originalOrder.getId());
                saveOriginalOrderItem(originalOrderItem);
            }
            for (PromotionInfo promotionInfo : originalOrder.getPromotionInfoList()) {
                // 保存订单优惠记录
                promotionInfo.setOriginalOrderId(originalOrder.getId());
                savePromotionInfo(promotionInfo);
            }
        }
    }

    /**
     * 根据条件查询优惠详情
     * @param promotionInfo
     * @return
     */
    @Transactional(readOnly = true)
    public List<PromotionInfo> findPromotionInfos(PromotionInfo promotionInfo){
        Search search = new Search(PromotionInfo.class);
        if (promotionInfo != null) {
            if (StringUtils.isNotBlank(promotionInfo.getPlatformOrderNo())) {
                search.addFilterEqual("platformOrderNo", promotionInfo.getPlatformOrderNo());
            }
            if (StringUtils.isNotBlank(promotionInfo.getSkuId())) {
                search.addFilterEqual("skuId", promotionInfo.getSkuId());
            }
            if(NumberUtil.isNotNullOrNotZero(promotionInfo.getOriginalOrderId())){
                search.addFilterEqual("originalOrderId",promotionInfo.getOriginalOrderId());
            }
        }
        List<PromotionInfo> promotionInfoList = generalDAO.search(search);
        return promotionInfoList;
    }

    /**
     * 根据条件查询唯一的优惠详情
     * @param promotionInfo
     * @return
     */
    @Transactional(readOnly = true)
    public PromotionInfo getPromotionInfo(PromotionInfo promotionInfo){
        List<PromotionInfo> promotionInfoList = findPromotionInfos(promotionInfo);
        return CollectionUtils.isNotEmpty(promotionInfoList) ? promotionInfoList.get(0) : null;
    }

    /**
     * 重置订单优惠信息（主要用于京东订单）
     * @param originalOrderList
     */
    public void resetDiscountInfo(List<OriginalOrder> originalOrderList){
        if(CollectionUtils.isEmpty(originalOrderList)){
            return;
        }

        for(OriginalOrder originalOrder : originalOrderList){
            originalOrder.setDiscountFee(getDiscountFee(originalOrder.getPromotionInfoList()));
            originalOrder.setSelfDiscountFee(getSelfDiscountFee(originalOrder.getPromotionInfoList()));


            if(CollectionUtils.isEmpty(originalOrder.getOriginalOrderItemList())){
                continue;
            }

            Money totalPayableFee = getTotalPayableFee(originalOrder);

            for(OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()){
                originalOrderItem.setPartMjzDiscount(getItemMjzDiscountFee(originalOrderItem.getPayableFee(),
                        totalPayableFee,originalOrder.getDiscountFee()));
                originalOrderItem.setSelfPartMjzDiscount(getItemMjzDiscountFee(originalOrderItem.getPayableFee(),
                        totalPayableFee,originalOrder.getSelfDiscountFee()));
                saveOriginalOrderItem(originalOrderItem);
            }

            saveOriginalOrder(originalOrder);
        }
    }

    /**
     * 获得订单总应付金额
     * @param originalOrder
     * @return
     */
    public Money getTotalPayableFee(OriginalOrder originalOrder){
        Money totalPayableFee = Money.valueOf(0);
        if(originalOrder == null || CollectionUtils.isEmpty(originalOrder.getOriginalOrderItemList())){
            return totalPayableFee;
        }

        for(OriginalOrderItem originalOrderItem : originalOrder.getOriginalOrderItemList()){
            totalPayableFee = totalPayableFee.add(originalOrderItem.getPayableFee());
        }
        return totalPayableFee;
    }

    /**
     * 计算获取分摊优惠金额
     * 计算公式：实付金额所占百分比 * 店铺优惠金额
     * @return
     */
    private Money getItemMjzDiscountFee(Money curPayableFee,Money totalPayableFee,Money discountFee){
        Money partMjzDiscountFee = Money.valueOf(0);
        if(curPayableFee == null || totalPayableFee == null || discountFee == null){
            return partMjzDiscountFee;
        }
        // 获得当前实付金额所占百分比
        BigDecimal paymentPercentBig = getPaymentPercent(curPayableFee,totalPayableFee);
        // 计算店铺优惠金额的分摊金额
        BigDecimal partMjzDiscountFeeBig = paymentPercentBig.multiply(new BigDecimal(discountFee.getAmount()));
        // 转为Money对象
        partMjzDiscountFee = Money.valueOf(partMjzDiscountFeeBig.doubleValue());

        return partMjzDiscountFee;
    }

    /**
     * 计算实付金额所占百分比
     * @return
     */
    private BigDecimal getPaymentPercent(Money curPayableFee,Money totalPayableFee){
        if(curPayableFee == null || totalPayableFee == null || totalPayableFee.getCent() == 0l){
            return BigDecimal.ZERO;
        }
        BigDecimal paymentPercentBig = new BigDecimal(curPayableFee.getCent()).divide(new BigDecimal(totalPayableFee.getCent()),4,BigDecimal.ROUND_HALF_UP);
        return paymentPercentBig;
    }

    /**
     * 获取整单优惠(需要分摊减去的优惠：100-店铺优惠，35-满返满送(返现))
     * @param promotionInfoList 订单优惠详情
     * @return
     */
    private Money getDiscountFee(List<PromotionInfo> promotionInfoList) {
        Money discountFee = Money.valueOf(0);
        if(promotionInfoList == null){
            return discountFee;
        }
        for(PromotionInfo promotionInfo : promotionInfoList){
            if(StringUtils.isBlank(promotionInfo.getSkuId())
                    && (StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_SHOUJIHONGBAO.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_JINGDOU.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_JINGDONGQUAN.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_LIPINKA.getDesc())
            )){
                discountFee = discountFee.add(promotionInfo.getDiscountFee());
            }
        }
        return discountFee;
    }

    /**
     * 获取整单优惠(需要分摊减去的优惠：100-店铺优惠，35-满返满送(返现))
     * @param promotionInfoList 订单优惠详情
     * @return
     */
    private Money getSelfDiscountFee(List<PromotionInfo> promotionInfoList) {
        Money selfDiscountFee = Money.valueOf(0);
        if(promotionInfoList == null){
            return selfDiscountFee;
        }
        for(PromotionInfo promotionInfo : promotionInfoList){
            if(StringUtils.isBlank(promotionInfo.getSkuId())
                    && (StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_TAOZHUANG.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_SHANTUAN.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_TUANGOU.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_DANPINCUXIAO.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_MANFANMANSONG.getDesc())
                    || StringUtils.equals(promotionInfo.getCouponType(), PromotionType.JD_DIANPU.getDesc())
            )){
                selfDiscountFee = selfDiscountFee.add(promotionInfo.getDiscountFee());
            }
        }
        return selfDiscountFee;
    }

}
