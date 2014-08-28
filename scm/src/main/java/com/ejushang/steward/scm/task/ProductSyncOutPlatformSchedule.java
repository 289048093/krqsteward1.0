package com.ejushang.steward.scm.task;

import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.ordercenter.domain.Product;
import com.ejushang.steward.ordercenter.domain.ProductPlatform;
import com.ejushang.steward.ordercenter.domain.Storage;
import com.ejushang.steward.ordercenter.service.ProductPlatformService;
import com.ejushang.steward.ordercenter.service.StorageService;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ApiInvokeException;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ProductInvoke;
import com.ejushang.steward.ordercenter.service.transportation.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User:  Sed.Lee(李朝)
 * Date: 14-6-3
 * Time: 下午1:18
 */
@Component
public class ProductSyncOutPlatformSchedule {

    private static final Logger log = LoggerFactory.getLogger(ProductSyncOutPlatformSchedule.class);

    @Autowired
    private ProductPlatformService productPlatformService;

    @Autowired
    private StorageService storageService;

    @Autowired(required = false)
    private List<ProductInvoke> productInvokes = new ArrayList<ProductInvoke>();

    //    @Scheduled(cron = "0 0 0 * * ?")
    public void syncOutPlatformData() {
        for (Storage s : storageService.findAll()) {
            Product product = s.getProduct();
            for (ProductInvoke invoke : productInvokes) {
                ProductPlatform productPlatform = productPlatformService.findBuProductSkuAndPlatformType(product.getSku(), invoke.getType());
                if (productPlatform.getSynStatus() && productPlatform.getPutaway()) {
                    try {
                        invoke.updateProductPlatformStorage(product.getOuterProductNo(), product.getSku(), productPlatform);
                    } catch (ApiInvokeException e) {
                        log.info("每天定时同步产品库存到外部平台出错:" + e.getMessage(), e);
                    }
                }
            }
        }
    }
}
