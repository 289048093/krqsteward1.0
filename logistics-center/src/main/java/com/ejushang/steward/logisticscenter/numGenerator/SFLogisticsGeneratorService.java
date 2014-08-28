package com.ejushang.steward.logisticscenter.numGenerator;

import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.logisticscenter.domain.LogisticsInfoNum;
import com.ejushang.steward.ordercenter.constant.SfConstant;
import com.ejushang.steward.ordercenter.domain.Invoice;
import com.ejushang.steward.ordercenter.domain.Order;
import com.ejushang.steward.ordercenter.domain.sfreq.ordersearch.SfOrderSearchRequest;
import com.ejushang.steward.ordercenter.domain.sfrsp.OrderResponse;
import com.ejushang.steward.ordercenter.domain.sfrsp.Response;
import com.ejushang.steward.ordercenter.domain.sfrsp.ordersearch.SfOrderSearchOrderResponse;
import com.ejushang.steward.ordercenter.domain.sfrsp.ordersearch.SfOrderSearchResponse;
import com.ejushang.steward.ordercenter.service.SfCommonService;
import com.taobao.top.link.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * User: JBoss.WU
 * Date: 14-8-12
 * Time: 下午2:04
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SFLogisticsGeneratorService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SFLogisticsGeneratorService.class);
    @Autowired
    private SfCommonService sfCommonService;

    public List<LogisticsInfoNum> querySFLogisticsGeneratorNums(List<Order> orders, SfConstant.SF_EXPRESS_TYPE expressType) {
        List<LogisticsInfoNum> list = new ArrayList<LogisticsInfoNum>();
        for (Order order : orders) {
            try {
                list.add(getExpno(order, expressType));
                // update
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Transactional
    public LogisticsInfoNum getExpno(Order order, SfConstant.SF_EXPRESS_TYPE expressType) throws ExecutionException, InterruptedException {
        Response response = null;
        LogisticsInfoNum logisticsInfoNum = new LogisticsInfoNum();
        try {
            response = sfCommonService.getSFLogisticsGeneratorNum(order, expressType);
        } catch (Exception e) {
            log.error(e.toString());
            logisticsInfoNum.setException(e);
        }

        logisticsInfoNum.setFirstOrderNo(order.getOrderNo());
        // update
        if (response.getBody() == null) {
            SfOrderSearchResponse res = null;
            try {
                res = sfCommonService.getExistedSfMailNo(order.getOrderNo());
            } catch (Exception e) {
                logisticsInfoNum.setException(e);
            }
            if (res.getBody() != null) {
                SfOrderSearchOrderResponse orderResponse = res.getBody().getOrderResponse();
                logisticsInfoNum.setException(null);
                logisticsInfoNum.setShippingNo(orderResponse.getMailno());
                logisticsInfoNum.setOrderNo(orderResponse.getOrderid());
                logisticsInfoNum.setDestCode(orderResponse.getDestcode());
                logisticsInfoNum.setOriginCode(orderResponse.getOrigincode());
            }
        }
        if (response.getBody() != null) {
            OrderResponse orderResponse = response.getBody().getOrderResponse();
            logisticsInfoNum.setException(null);
            logisticsInfoNum.setShippingNo(orderResponse.getMailno());
            logisticsInfoNum.setOrderNo(orderResponse.getOrderid());
            logisticsInfoNum.setDestCode(orderResponse.getDestcode());
            logisticsInfoNum.setOriginCode(orderResponse.getOrigincode());
        }
        return logisticsInfoNum;
    }
}
