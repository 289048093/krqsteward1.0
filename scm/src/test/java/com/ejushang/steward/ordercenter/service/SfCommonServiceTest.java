package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.constant.SfConstant;
import com.ejushang.steward.ordercenter.domain.Order;
import com.ejushang.steward.ordercenter.domain.sfreq.Request;
import com.ejushang.steward.ordercenter.domain.sfreq.ordersearch.SfOrderSearchRequest;
import com.ejushang.steward.ordercenter.domain.sfrsp.Response;
import com.ejushang.steward.ordercenter.domain.sfrsp.ordersearch.SfOrderSearchResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutionException;

/**
 * User: Baron.Zhang
 * Date: 2014/8/18
 * Time: 17:48
 */
public class SfCommonServiceTest extends BaseTest {

    @Autowired
    private SfCommonService sfCommonService;

    @Autowired
    private OrderService orderService;

    @Test
    public void testGetSFLogisticsGeneratorNum() throws ExecutionException, InterruptedException {
        Order order = orderService.findOrderFullInfoById(1);

        Request request = sfCommonService.getRequest(order, SfConstant.SF_EXPRESS_TYPE.BIAO_ZHUN_KUAI_DI);

        String reqStr = sfCommonService.getReqStr(request);

        System.out.println(reqStr);

        Response response = sfCommonService.getSFLogisticsGeneratorNum(order, SfConstant.SF_EXPRESS_TYPE.BIAO_ZHUN_KUAI_DI);

        System.out.println("");
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Order order = orderService.findOrderFullInfoById(1);

        SfOrderSearchRequest request = sfCommonService.getSfOrderSearchRequest(order.getOrderNo());

        String reqStr = sfCommonService.getSfOrderSearchRequestStr(request);

        System.out.println(reqStr);

        SfOrderSearchResponse response = sfCommonService.getExistedSfMailNo(order.getOrderNo());

        System.out.println("");

    }

}
