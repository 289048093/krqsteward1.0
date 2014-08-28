package com.ejushang.steward.ordercenter.orderService;

import com.ejushang.steward.common.Application;
import com.ejushang.steward.common.domain.Receiver;
import com.ejushang.steward.common.util.JsonResult;
import com.ejushang.steward.common.util.JsonUtil;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.OriginalOrder;
import com.ejushang.steward.ordercenter.service.OrderService;
import com.ejushang.steward.ordercenter.service.OriginalOrderService;
import com.ejushang.steward.scm.ScmApplication;
import com.ejushang.steward.scm.web.OrderController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Shiro
 * Date: 14-4-17
 * Time: 下午2:13
 */
@Transactional
@ContextConfiguration("classpath*:mvc-context.xml")
public class OrderServiceExchangeGoodsTest extends BaseTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderController orderController;

    /**
     * 简单1换1
     */
    @Test
    @Rollback(false)
    @Transactional
    public void testExchangeGoods1() {
        Integer orderItemId = 47493;
        List<Map<String, String>> exchangeDetails = new ArrayList<Map<String, String>>();
        addExchangeDetail(exchangeDetails, 4338, 1);

        orderController.exchangeGoods(orderItemId, JsonUtil.object2Json(exchangeDetails));

    }

    @Test
    @Rollback(false)
    @Transactional
    public void testDeleteExchangeOrderItem1() {
        Integer orderItemId = 47493;

        orderService.deleteOrderItemById(orderItemId);
    }


    /**
     * 2个橙色凳子换一个橙色一个蓝色
     */
    @Test
    @Rollback(false)
    @Transactional
    public void testExchangeGoods2() {
        Integer orderItemId = 47469;
        List<Map<String, String>> exchangeDetails = new ArrayList<Map<String, String>>();
        addExchangeDetail(exchangeDetails, 4374, 1);
        addExchangeDetail(exchangeDetails, 4372, 1);

        orderController.exchangeGoods(orderItemId, JsonUtil.object2Json(exchangeDetails));

    }

    @Test
    @Rollback(false)
    @Transactional
    public void testDeleteExchangeOrderItem2() {
        Integer orderItemId = 47469;

        orderService.deleteOrderItemById(orderItemId);
    }

    private void addExchangeDetail(List<Map<String, String>> exchangeDetails, Integer productId, Integer count) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("productId", String.valueOf(productId));
        map.put("count", String.valueOf(count));
        exchangeDetails.add(map);
    }

}
