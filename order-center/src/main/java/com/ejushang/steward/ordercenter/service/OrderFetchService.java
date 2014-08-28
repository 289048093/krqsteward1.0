package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.page.Page;

import com.ejushang.steward.ordercenter.constant.FetchDataType;
import com.ejushang.steward.ordercenter.constant.FetchOptType;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.OrderFetch;
import com.ejushang.steward.ordercenter.domain.Product;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Shiro
 * Date: 14-4-8
 * Time: 下午1:54
 */
@Service
public class OrderFetchService {
    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有抓单记录
     *
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderFetch> findAll(Page page) {
        Search search = new Search(OrderFetch.class);
        search.addSortDesc("fetchTime").addPagination(page);
        return generalDAO.search(search);
    }

    /**
     * 保存方法，测试用
     *
     * @param orderFetch
     */
    @Transactional
    public void save(OrderFetch orderFetch) {
        generalDAO.saveOrUpdate(orderFetch);
    }

    /**
     * 根据平台类型和店铺id查询最新的一条抓取记录
     *
     * @param outPlatFormType
     * @param shopId
     * @return
     */
    @Transactional(readOnly = true)
    public OrderFetch findLastOrderFetch(PlatformType outPlatFormType, Integer shopId, FetchDataType fetchDataType) {
        Search search = new Search(OrderFetch.class);
        search.addFilterEqual("platformType", outPlatFormType).addFilterEqual("shopId", shopId)
                .addFilterEqual("fetchDataType",fetchDataType)
                .addSortDesc("fetchTime");
        List<OrderFetch> orderFetchs = generalDAO.search(search);

        return CollectionUtils.isEmpty(orderFetchs) ? null : orderFetchs.get(0);
    }

    /**
     * 根据平台类型和店铺id查询最新的一条抓取记录
     *
     * @param outPlatFormType
     * @param shopId
     * @return
     */
    @Transactional(readOnly = true)
    public OrderFetch findLastOrderFetch(PlatformType outPlatFormType, Integer shopId, FetchDataType fetchDataType,FetchOptType fetchOptType) {
        Search search = new Search(OrderFetch.class);
        search.addFilterEqual("platformType", outPlatFormType).addFilterEqual("shopId", shopId)
                .addFilterEqual("fetchDataType",fetchDataType)
                .addFilterEqual("fetchOptType",fetchOptType)
                .addSortDesc("fetchTime");
        List<OrderFetch> orderFetchs = generalDAO.search(search);

        return CollectionUtils.isEmpty(orderFetchs) ? null : orderFetchs.get(0);
    }

    /**
     * 根据平台类型和店铺id查询最新的一条抓取记录
     *
     * @param outPlatFormType
     * @param shopId
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderFetch> findOrderFetchByCondition(PlatformType outPlatFormType, Integer shopId, FetchDataType fetchDataType,FetchOptType fetchOptType,Page page) {
        Search search = new Search(OrderFetch.class);
        if(outPlatFormType != null){
            search.addFilterEqual("platformType", outPlatFormType);
        }
        if(shopId != null){
            search.addFilterEqual("shopId", shopId);
        }
        if(fetchDataType != null){
            search.addFilterEqual("fetchDataType", fetchDataType);
        }
        if(fetchOptType != null){
            search.addFilterEqual("fetchOptType", fetchOptType);
        }
        search.addSortDesc("fetchTime").addPagination(page);

        List<OrderFetch> orderFetchs = generalDAO.search(search);
        return orderFetchs;
    }

    /**
     * 删除除最新记录之外的所有抓取记录
     */
    public void deleteOrderFetchsExLastRecord(){

        StringBuffer sqlBuffer = new StringBuffer(" DELETE FROM `steward`.`t_order_fetch` ")
                .append(" WHERE ")
                .append(" id NOT IN ")
                .append(" (SELECT id FROM (SELECT max(id) AS id FROM `steward`.`t_order_fetch` ")
                .append(" GROUP BY platform_type,shop_id,fetch_data_type,fetch_opt_type) AS maxt_t); ")
                ;

        String sql = sqlBuffer.toString();

        jdbcTemplate.execute(sql);
    }
}
