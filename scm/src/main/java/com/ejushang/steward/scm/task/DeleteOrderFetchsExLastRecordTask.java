package com.ejushang.steward.scm.task;

import com.ejushang.steward.ordercenter.service.OrderFetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * User: Baron.Zhang
 * Date: 2014/7/31
 * Time: 14:45
 */
@Component
public class DeleteOrderFetchsExLastRecordTask {

    private static final Logger log = LoggerFactory.getLogger(DeleteOrderFetchsExLastRecordTask.class);

    @Autowired
    private OrderFetchService orderFetchService;

    /**
     * 每天0点0分0秒触发1次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOrderFetchsExLastRecord(){
        if(log.isInfoEnabled()){
            log.info("开始删除除最新抓取记录外的其他记录。");
        }
        orderFetchService.deleteOrderFetchsExLastRecord();
        if(log.isInfoEnabled()){
            log.info("删除除最新抓取记录外的其他记录成功。");
        }
    }

}
