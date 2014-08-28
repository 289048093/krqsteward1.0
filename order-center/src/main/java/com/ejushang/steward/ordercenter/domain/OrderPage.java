package com.ejushang.steward.ordercenter.domain;

import com.ejushang.steward.common.page.Page;
import com.ejushang.steward.common.util.Money;

/**
 * User: JBoss.WU
 * Date: 14-8-1
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class OrderPage extends Page {

    public OrderPage(int pageNo, int pageSize) {
        super(pageNo, pageSize);
    }
   private Money goodsFee;

    public Money getGoodsFee() {
        return goodsFee;
    }

    public void setGoodsFee(Money goodsFee) {
        this.goodsFee = goodsFee;
    }
}
