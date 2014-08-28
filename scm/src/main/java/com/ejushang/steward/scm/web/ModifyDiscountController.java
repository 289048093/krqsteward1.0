package com.ejushang.steward.scm.web;

import com.ejushang.steward.common.util.EJSDateUtils;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.OriginalOrder;
import com.ejushang.steward.ordercenter.domain.OriginalOrderItem;
import com.ejushang.steward.ordercenter.service.OriginalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/8/20
 * Time: 15:39
 */
@Controller
public class ModifyDiscountController {

    @Autowired
    private OriginalOrderService originalOrderService;

    @RequestMapping("/np/modifyDiscount")
    public void modifyDiscount(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date start,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date end, HttpServletResponse response) throws Exception {
        Date startDate = EJSDateUtils.parseDate("2014-08-01 00:00:00", EJSDateUtils.DateFormatType.DATE_FORMAT_STR);
        Date endDate = EJSDateUtils.getCurrentDate();

        if(start != null){
            startDate = start;
        }
        if(end != null){
            endDate = end;
        }

        if(startDate.compareTo(endDate) > 0){
            throw new Exception("结束时间不能大于开始时间");
        }

        OriginalOrder originalOrder = new OriginalOrder();
        originalOrder.setPlatformType(PlatformType.JING_DONG);
        originalOrder.setProcessed(null);
        List<OriginalOrder> originalOrderList = originalOrderService.findOriginalOrderFullinfos(originalOrder,startDate,endDate);

        originalOrderService.resetDiscountInfo(originalOrderList);

        PrintWriter out = response.getWriter();
        out.write("重新设置优惠成功：开始时间：" +EJSDateUtils.formatDate(startDate, EJSDateUtils.DateFormatType.DATE_FORMAT_STR)
                +",结束时间："+EJSDateUtils.formatDate(endDate, EJSDateUtils.DateFormatType.DATE_FORMAT_STR)+",共有"+ originalOrderList.size() + "条订单执行。");

    }

}
