package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.util.EJSDateUtils;
import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.OriginalOrder;
import com.sun.management.VMOption;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/8/22
 * Time: 14:20
 */
public class OriginalOrderServiceTest extends BaseTest{

    @Autowired
    private OriginalOrderService originalOrderService;

    @Test
    public void testFindOriginalOrderFullinfos(){

        OriginalOrder originalOrder = new OriginalOrder();
        originalOrder.setPlatformType(PlatformType.JING_DONG);
        originalOrder.setProcessed(null);

        Date startDate = EJSDateUtils.parseDate("2014-08-19 00:00:00", EJSDateUtils.DateFormatType.DATE_FORMAT_STR);
        Date endDate = EJSDateUtils.getCurrentDate();


        List<OriginalOrder> originalOrderList = originalOrderService.findOriginalOrderFullinfos(originalOrder,startDate,endDate);

        System.out.println(originalOrderList.size());

        originalOrderService.resetDiscountInfo(originalOrderList);

    }



}
