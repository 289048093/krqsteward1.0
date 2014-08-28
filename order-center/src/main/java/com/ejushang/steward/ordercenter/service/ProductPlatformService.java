package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.domain.Platform;
import com.ejushang.steward.ordercenter.domain.Product;
import com.ejushang.steward.ordercenter.domain.ProductPlatform;
import com.ejushang.steward.ordercenter.domain.Storage;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ProductInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * User:  Sed.Lee(李朝)
 * Date: 14-5-21
 * Time: 下午1:19
 */
@Service
@Transactional(readOnly = true)
public class ProductPlatformService {
    private static final Logger logger = LoggerFactory.getLogger(ProductPlatformService.class);

    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private StorageService storageService;

    public List<ProductPlatform> listByProductId(Integer productId) {
        Search search = new Search(ProductPlatform.class);
        search.addFilterEqual("prodId", productId);
        //noinspection unchecked
        return generalDAO.search(search);
    }

    /**
     * 查找平台下有没有产品记录，用于删除平台时判断
     *
     * @param platformId
     * @return
     */
    public List<ProductPlatform> listByPlatformId(Integer platformId) {
        Search search = new Search(ProductPlatform.class);
        search.addFilterEqual("platformId", platformId);
        //noinspection unchecked
        return generalDAO.search(search);
    }

    /**
     * 通过产品sku和平台类型查找平台
     *
     * @param productSku
     * @param type
     * @return
     */
    public ProductPlatform findBuProductSkuAndPlatformType(String productSku, PlatformType type) {
        Search search = new Search(ProductPlatform.class)
                .addFilterEqual("product.sku", productSku)
                .addFilterEqual("platform.type", type);
        List res = generalDAO.search(search);
        return res.isEmpty() ? null : (ProductPlatform) res.get(0);
//        return (ProductPlatform) generalDAO.searchUnique(search);
    }

    /**
     * 获取平台实时应该分配的库存，即总库存*占比
     *
     * @param sku
     * @param platformType
     * @return
     */
    public Integer getPlatformRealStorage(String sku, PlatformType platformType) {
        Search search = new Search(ProductPlatform.class).addFilterEqual("product.sku", sku).addFilterEqual("platform.type", platformType);
        ProductPlatform pp = (ProductPlatform) generalDAO.searchUnique(search);
        Storage storage = storageService.findByProductId(pp.getProdId());
        Integer rate = pp.getStoragePercent();
        if (rate == null) {
            rate = generalDAO.get(Platform.class, pp.getPlatformId()).getStoragePercent();
        }
        return (int) Math.ceil(storage.getAmount() * rate / 100.0);
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<ProductPlatform> findAll() {
        Search search = new Search(ProductPlatform.class).addFilterEqual("product.deleted", false);
        return generalDAO.search(search);
    }
}
