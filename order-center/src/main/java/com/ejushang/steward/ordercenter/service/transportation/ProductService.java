package com.ejushang.steward.ordercenter.service.transportation;

import com.ejushang.steward.common.Application;
import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.page.Page;
import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.common.util.PoiUtil;
import com.ejushang.steward.ordercenter.constant.InOutStockType;
import com.ejushang.steward.ordercenter.constant.PlatformType;
import com.ejushang.steward.ordercenter.constant.ProductLocation;
import com.ejushang.steward.ordercenter.constant.ProductStyle;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.service.*;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ApiInvokeException;
import com.ejushang.steward.ordercenter.service.outplatforminvoke.ProductInvoke;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Product: liubin
 * Date: 14-3-10
 */
@Service
public class ProductService {
    static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private GeneralDAO generalDAO;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductPlatformService productPlatformService;

    @Autowired(required = false)
    private List<ProductInvoke> productInvokes = new ArrayList<ProductInvoke>();

    /**
     * 通过ID获得产品
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Product get(Integer id) {
        if (logger.isInfoEnabled()) {
            logger.info("通过商品id获得商品对象 id：" + id);
        }
        return generalDAO.get(Product.class, id);
    }

    /**
     * 通过ID获得产品
     *
     * @param categoryId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Product> getProductByCategoryId(Integer categoryId) {
        if (logger.isInfoEnabled()) {
            logger.info("通过产品分类id获得商品对象 categoryId：" + categoryId);
        }
        Search search = new Search(Product.class);
        search.addFilterEqual("categoryId", categoryId);
        return generalDAO.search(search);
    }


    /**
     * 通过查询条件获得产品List信息
     *
     * @param searchType
     * @param searchValue
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    public List<Product> findProductByAll(String searchType, String searchValue, Integer brandId, Page page) {
        Search search = new Search(Product.class);
        /**
         * 如果需要查询的值为空或者searchType为null就不做条件查询
         */
        if (!StringUtils.isBlank(searchType) && !StringUtils.isBlank(searchValue)) {
            search.addFilterLike(searchType, "%" + searchValue + "%");
        }
        if (brandId != null) {
            search.addFilterEqual("brandId", brandId);
        }
        search.addSortDesc("createTime").addPagination(page).addFilterEqual("deleted", false);
        return generalDAO.search(search);
    }

    /**
     * 查询出所有有效的产品，无分页
     *
     * @return
     */
    public List<Product> findAllWithNoPage() {
        return generalDAO.search(new Search(Product.class).addFilterEqual("deleted", false));
    }


    public List<Product> findProductAddOrder(String searchType, String searchValue, Page page, String platformType) {
        List<Product> products = findProductByAll(searchType, searchValue, null, page);
        for (Product product : products) {
            if (!StringUtils.isBlank(platformType)) {
                Money price = getPrice(product.getId(), platformService.findByType(PlatformType.valueOf(platformType)).getId());
                if (price != null && price.getAmount() != 0d) {
                    product.setMarketPrice(price);
                }
            }
        }
        return products;
    }


    /**
     * 通过SKU获得产品
     *
     * @param SKU
     * @return
     */
    @Transactional(readOnly = true)
    public Product findProductBySKU(String SKU) {
        if (logger.isInfoEnabled()) {
            logger.info("通过SKU查找产品  SKU：" + SKU);
        }
        Search search = new Search(Product.class);
        /**
         * 如果SKU不为空，进行模糊查询
         */
        if (!StringUtils.isBlank(SKU)) {
            search.addFilterEqual("sku", SKU);
        }
        return (Product) generalDAO.searchUnique(search);
    }


    /**
     * 单个保存<p/>
     * 如果出错则抛出异常，事物回滚
     *
     * @param product
     */
    @Transactional
    public void save(Product product, List<ProductPlatform> productPlatformList, Integer repositoryId, Integer repositoryNum) {
        String msg = saveOrUpdateProduct(product, productPlatformList, repositoryId, repositoryNum);
        if (StringUtils.isNotBlank(msg)) {
            throw new StewardBusinessException(msg);
        }
    }

    /**
     * 产品导入的保存
     * <p/>
     *
     * @param product
     * @param productPlatformList
     * @param repositoryId
     * @param repositoryNum
     * @return
     */
    private String saveForUpload(Product product, List<ProductPlatform> productPlatformList, Integer repositoryId, Integer repositoryNum) {
        return saveOrUpdateProduct(product, productPlatformList, repositoryId, repositoryNum);
    }

    private int countBySkuExceptById(String sku, Integer id) {
        Search search = new Search(Product.class);
        search.addFilterEqual("sku", sku).addFilterNotEqual("id", id);
        return generalDAO.count(search);
    }

    /**
     * 保存或更新
     *
     * @param product
     * @param productPlatformList
     * @param repositoryId
     * @param repositoryNum
     * @return errorMsg
     */
    private String saveOrUpdateProduct(Product product, List<ProductPlatform> productPlatformList, Integer repositoryId, Integer repositoryNum) {
        boolean isAdd;
        synchronized (this) {   // 防止添加或导入的时候，并发导致出现相同sku的问题，
            if (product.getId() == null && isProductExistBySKU(product.getSku())) {
                throw new StewardBusinessException("SKU已存在");
            }
//            if (product.getId() != null && countBySkuExceptById(product.getSku(), product.getId()) > 0) {
//                throw new StewardBusinessException(String.format("SKU[%s]已使用", product.getSku()));
//            }
            isAdd = product.getId() == null;
            generalDAO.saveOrUpdate(product);
            for (ProductPlatform productPlatform : productPlatformList) {
                productPlatform.setProdId(product.getId());
            }
            this.saveProductPlatform(productPlatformList);
        }
        try {
            if (isAdd) {
                storageService.storageIncrementForProductInit(product.getId(), repositoryId, repositoryNum, InOutStockType.IN_STOCK_TYPE_INIT, null, true);
            } else {
                Storage storage = storageService.findByProductId(product.getId());
                if (!storage.getRepositoryId().equals(repositoryId)) {
                    storage.setRepositoryId(repositoryId);
                    generalDAO.saveOrUpdate(storage);
                }
            }
        } catch (ApiInvokeException e) {
            logger.info(e.getMessage(), e);
            return e.getMessage();         // 产品导入时候异常
        }
        return syncOutPlatformDate(product);
    }

    /**
     * 与外部平台同步产品数据
     *
     * @param product
     */
    private String syncOutPlatformDate(Product product) {
        String productNo = product.getOuterProductNo();
        String sku = product.getSku();
        StringBuilder s = new StringBuilder();
        for (ProductInvoke pi : productInvokes) {
            ProductPlatform productPlatform = null;
            try {
                productPlatform = productPlatformService.findBuProductSkuAndPlatformType(sku, pi.getType());
                // TODO  新加入的平台处理
                if (productPlatform == null || !productPlatform.getSynStatus()) { //如果不需要同步则跳过
                    continue;
                }
                pi.downSyncProductInfo(productNo, sku, productPlatform);  //同步数据到本系统
                generalDAO.saveOrUpdate(productPlatform);
                //是否上架
                if (productPlatform.getPutaway()) {
                    pi.productListing(productNo, sku);
                    pi.updateProductPlatformStorage(productNo, sku, productPlatform);//按百分比更新库存
                } else {
                    pi.productDelisting(productNo, sku);
                }
            } catch (ApiInvokeException e) {
                logger.info(e.getMessage(), e);
                productPlatform.setSynStatus(false);
                generalDAO.saveOrUpdate(productPlatform);
                s.append(e.getMessage()).append(",");
            }
        }
        if (StringUtils.isNotBlank(s)) {
            return s.substring(0, s.length() - 1);
        }
        return "";
    }

    /**
     * 通过数组删除产品
     *
     * @param array
     */
    @Transactional
    public void delete(int[] array) {
        for (int proId : array) {
            Product product = this.get(proId);
            if (existNormalMealsetByProductId(proId)) {
                throw new StewardBusinessException(String.format("产品[%s]存在于套餐中，不能删除", product.getName()));
            }
            product.setDeleted(true);
            generalDAO.saveOrUpdate(product);
        }
    }

    /**
     * 判断产品是否存在未删除的套餐中
     *
     * @param productId
     * @return
     */
    private boolean existNormalMealsetByProductId(Integer productId) {
        Search search = new Search(MealsetItem.class).addFilterEqual("mealset.deleted", false).addFilterEqual("productId", productId);
        return generalDAO.count(search) > 0;
    }

    /**
     * 通过SKU判断产品是否存在
     *
     * @param sku
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isProductExistBySKU(String sku) {
        Search search = new Search(Product.class).addFilterEqual("sku", sku);
        int count = generalDAO.count(search);
        return count > 0;
    }


    @Transactional
    public String leadInProduct(List<Row> sheetRows) {
        ProductExcelUploadDBData dbData = new ProductExcelUploadDBData();
        StringBuilder warnMsg = new StringBuilder();
        for (Row row : sheetRows) {
            try {
                String msg = validateExcel(row, row.getRowNum(), dbData);
                if (StringUtils.isNotBlank(warnMsg)) {
                    warnMsg.append(msg).append(";");
                }
            } catch (Exception e) {
                String errMsg = e instanceof StewardBusinessException
                        ? e.getMessage()
                        : e instanceof DataException
                        ? String.format("第[%1$d]行导入数据错误，可能是数据太长，请检查数据:%2$s", row.getRowNum() + 1, e.getMessage())
                        : String.format("第[%1$d]行导入数据错误:%2$s", row.getRowNum() + 1, e.getMessage());
                logger.error("产品导入错误：" + e.getMessage(), e);
                throw new StewardBusinessException(errMsg, e);
            }
        }
        return warnMsg.toString();
    }

    /**
     * 导单前验证excel表单是否合法
     *
     * @param row
     * @return
     */
    private String validateExcel(Row row, int index, ProductExcelUploadDBData dbData) throws ApiInvokeException {


        /**
         * 平台-商品的set
         */

        List<ProductPlatform> productPlatformList = new ArrayList<ProductPlatform>();


        int platformInitIndex = ProductExcelInfo.PLATFORM_INDEX.value;//excel 平台前面的下标
        for (int i = 0; i < ProductExcelInfo.PLATFORM_NUM.value; i++) {
            Platform p = dbData.findPlatformByType(ProductExcelInfo.getPlatformTypeByIndex(i));
            ProductPlatform productPlatform = generateExcelProductPlatform(row, platformInitIndex, p);
            platformInitIndex += ProductExcelInfo.PLATFORM_CELL_NUM.value;
            productPlatform.setPlatformId(p.getId());
            productPlatformList.add(productPlatform);
        }
        String repositoryNum = PoiUtil.getStringCellValue(row, ProductExcelInfo.STORAGE_NUM.value);     //总库存
        Product product = generateProductFromRow(row, index, dbData);
        /**
         * 仓库
         */
        String repositoryName = PoiUtil.getStringCellValue(row, ProductExcelInfo.REPOSITORY_NAME.value);     //仓库

        /**
         * 判断仓库是否存在
         */
        Repository repository = dbData.findRepositoryByName(repositoryName);
        assertNotNull(repository, String.format("仓库[%1$s]不存在，错误行:%2$d", repositoryName, index + 1));
        /**
         * 平台的存入
         */
        if (logger.isInfoEnabled()) {
            logger.info("存入商品信息：product" + product);
        }
        return saveForUpload(product, productPlatformList, repository.getId(), StringUtils.isBlank(repositoryNum) ? 0 : Integer.parseInt(repositoryNum));
    }

    private Product generateProductFromRow(Row row, int rowNum, ProductExcelUploadDBData dbData) {
        int cellNum = 0;
        String name = PoiUtil.getStringCellValue(row, cellNum++);         //产品名称
        String sku = PoiUtil.getStringCellValue(row, cellNum++);         //条形码
        String prodNo = PoiUtil.getStringCellValue(row, cellNum++);           //产品编号
        String outerProductNo = PoiUtil.getStringCellValue(row, cellNum++);
        String brandName = PoiUtil.getStringCellValue(row, cellNum++);        //品牌名
        String proCatgoryName = PoiUtil.getStringCellValue(row, cellNum++);   //分类
        String color = PoiUtil.getStringCellValue(row, cellNum++);           //颜色
        String weight = PoiUtil.getStringCellValue(row, cellNum++);          //重量
        String boxSize = PoiUtil.getStringCellValue(row, cellNum++);          //包装尺寸
        String speci = PoiUtil.getStringCellValue(row, cellNum++);           //规格
//        String importPrice = PoiUtil.getStringCellValue(row, cellNum++);         //现价
        String marketPrice = PoiUtil.getStringCellValue(row, cellNum++);    //市场价
        String minimumPrice = PoiUtil.getStringCellValue(row, cellNum++);        //最低价
        String location = PoiUtil.getStringCellValue(row, cellNum++);           //库位
        String style = PoiUtil.getStringCellValue(row, cellNum++); //商品类型
        String orgin = PoiUtil.getStringCellValue(row, cellNum++);     //产地
        String description = PoiUtil.getStringCellValue(row, cellNum);     //描述


        Brand brand = dbData.findBrandByName(brandName);
        assertNotNull(brand, String.format("品牌[%1$s]不存在，错误行:%2$d", brandName, rowNum + 1));
        ProductCategory productCategory = dbData.findProductCategoryByName(proCatgoryName);
        assertNotNull(productCategory, String.format("产品类目[%1$s]不存在，错误行:%2$d", proCatgoryName, rowNum + 1));
        if (dbData.containSku(sku)) {
            throw new StewardBusinessException(String.format("产品的条形码[%s]已经存在", sku));
        } else {
            dbData.addSku(sku);
        }
        /**商品数据注入*/
        return getProduct(name, sku, prodNo, outerProductNo, color, weight, boxSize, speci, marketPrice, minimumPrice, location, style, orgin, description, brand, productCategory);
    }

    private void assertNotNull(Object obj, String errorMsg) {
        if (obj == null) {
            throw new StewardBusinessException(errorMsg);
        }
    }

    /**
     * 平台信息的动态读入
     *
     * @param row
     * @param initIndex
     * @return
     */
    private ProductPlatform generateExcelProductPlatform(Row row, int initIndex, Platform platform) {
        if (platform == null) {
            throw new StewardBusinessException("平台数据读取异常！");
        }
        ProductPlatform productPlatform = new ProductPlatform();
        productPlatform.setPlatformId(platform.getId());
        String priceRegex = "\\d+(\\.\\d+)?";
        String price = PoiUtil.getStringCellValue(row, initIndex++);
        if (!StringUtils.isBlank(price) && !price.matches(priceRegex)) {
            throw new StewardBusinessException(String.format("产品一口价[%s]错误", price));
        }
        productPlatform.setPrice(Money.valueOf(price));     //一口价
        String discountPrice = PoiUtil.getStringCellValue(row, initIndex++);
        if (!StringUtils.isBlank(discountPrice) && !discountPrice.matches(priceRegex)) {
            throw new StewardBusinessException(String.format("产品促销价[%s]错误", discountPrice));
        }
        productPlatform.setDiscountPrice(Money.valueOf(discountPrice));     //促销价
        productPlatform.setPutaway(this.isTrue(PoiUtil.getStringCellValue(row, initIndex++)));        //是否上架
        Double storageNum = PoiUtil.getNumericCellValue(row, initIndex++);
        productPlatform.setStorageNum(storageNum == null ? null : storageNum.intValue());     //库存
        Double percent = PoiUtil.getNumericCellValue(row, initIndex++);
        productPlatform.setStoragePercent(percent == null ? null : percent.intValue());     //库存占比
        productPlatform.setSynStatus(this.isTrue(PoiUtil.getStringCellValue(row, initIndex++)));     //同步状态
        productPlatform.setPlatformUrl(PoiUtil.getStringCellValue(row, initIndex));     //链接
        return productPlatform;
    }

    private Product getProduct(String name, String SKU, String prodNo, String outerProductNo, String color, String weight, String boxSize, String speci, String marketPrice, String minimumPrice, String location, String style, String orgin, String description, Brand brand, ProductCategory productCategory) {
        Product product = new Product();//findDeletedBySku(SKU);
//        if (product == null) {
//            product = new Product();
//        }
        product.setBrandId(brand.getId());
        product.setBrand(brand);
        product.setName(name);
        product.setProductNo(prodNo);
        product.setOuterProductNo(outerProductNo);
        product.setSku(SKU);
        product.setCategoryId(productCategory.getId());
        product.setCategory(productCategory);
        product.setDescription(description);
        product.setPicUrl("");// TODO  暂时没有
        String priceRegex = "\\d+(\\.\\d+)?";
        if (!StringUtils.isBlank(marketPrice) && !marketPrice.matches(priceRegex)) {
            throw new StewardBusinessException(String.format("产品市场价[%s]错误", marketPrice));
        }
        product.setMarketPrice(Money.valueOf(marketPrice));
//        product.setImportPrice(Money.valueOf(importPrice));
        if (!StringUtils.isBlank(minimumPrice) && !minimumPrice.matches(priceRegex)) {
            throw new StewardBusinessException(String.format("产品最低价[%s]错误", minimumPrice));
        }
        product.setMinimumPrice(Money.valueOf(minimumPrice));
        product.setColor(color);
        product.setWeight(weight);
        product.setBoxSize(boxSize);
        product.setSpeci(speci);
        product.setOrgin(orgin);
        product.setStyle(ProductStyle.valueOf(style));
        product.setLocation(ProductLocation.enumValueOf(location));
        return product;
    }

    /**
     * 统计品牌id下的产品数量
     *
     * @param brandId
     * @return
     */
    @Transactional(readOnly = true)
    public int countByBrandId(int brandId) {
        return generalDAO.count(new Search(Product.class).addFilterEqual("brandId", brandId).addFilterEqual("deleted", false));
    }

    /**
     * 产品导入时数据库查询的数据,不考虑数据并发同步的问题
     */
    private class ProductExcelUploadDBData {
        private Map<String, Brand> brandMap = new HashMap<String, Brand>();
        private Map<String, Repository> repositoryMap = new HashMap<String, Repository>();
        private Map<String, ProductCategory> categoryMap = new HashMap<String, ProductCategory>();
        private Map<PlatformType, Platform> platformMap = new HashMap<PlatformType, Platform>();
        private Set<String> skus = new HashSet<String>();

        ProductExcelUploadDBData() {
            init();
        }

        @Transactional(readOnly = true)
        void init() {
            List<Brand> brands = brandService.findAll();
            for (Brand brand : brands) {
                brandMap.put(brand.getName(), brand);
            }
            List<Repository> repositories = repositoryService.findRepositoryAll();
            for (Repository repository : repositories) {
                repositoryMap.put(repository.getName(), repository);
            }
            List<ProductCategory> categories = productCategoryService.findAll();
            for (ProductCategory category : categories) {
                categoryMap.put(category.getName(), category);
            }
            List<Platform> platforms = platformService.findPlatform();
            for (Platform platform : platforms) {
                platformMap.put(PlatformType.valueOf(platform.getType()), platform);   // ?平台类型为何是String而不是platformType enum
            }
            List<Product> products = generalDAO.findAll(Product.class);
            for (Product product : products) {
                skus.add(product.getSku());
            }
        }

        boolean containSku(String sku) {
            return skus.contains(sku);
        }

        void addSku(String sku) {
            skus.add(sku);
        }

        Brand findBrandByName(String name) {
            return brandMap.get(name);
        }

        Repository findRepositoryByName(String name) {
            return repositoryMap.get(name);
        }

        ProductCategory findProductCategoryByName(String name) {
            return categoryMap.get(name);
        }

        Platform findPlatformByType(PlatformType type) {
            return platformMap.get(type);
        }

    }

    private boolean isTrue(String str) {
        return "是".equals(str);
    }
    /*************************商品-平台*************************/
    /**
     * 根据平台和商品ID查询
     *
     * @param productPlatform
     * @return
     */
    @Transactional(readOnly = true)
    public List<ProductPlatform> findProductPlatform(ProductPlatform productPlatform) {
        if (logger.isInfoEnabled()) {
            logger.info("查找产品-平台  productPlatform：" + productPlatform);
        }
        Search search = new Search(ProductPlatform.class);
        search.addFilterEqual("platformId", productPlatform.getPlatformId()).addFilterEqual("prodId", productPlatform.getProdId());
        //noinspection unchecked
        return generalDAO.search(search);
    }

    /**
     * 根据平台和商品ID查询
     *
     * @param prodId
     * @param platformId
     * @return
     */
    @Transactional(readOnly = true)
    public ProductPlatform getProductPlatform(Integer prodId, Integer platformId) {
        if (logger.isInfoEnabled()) {
            logger.info("查找产品-平台  prodId：" + prodId + "  platformId " + platformId);
        }
        Search search = new Search(ProductPlatform.class);
        search.addFilterEqual("platformId", platformId).addFilterEqual("prodId", prodId);
        if (generalDAO.search(search).size() == 0) {
            return null;
        }
        return (ProductPlatform) generalDAO.search(search).get(0);
    }


    /**
     * 修改产品-平台数据
     *
     * @param productPlatformList
     * @return
     */
    @Transactional
    public void saveProductPlatform(List<ProductPlatform> productPlatformList) {
        if (logger.isInfoEnabled()) {
            logger.info("修改产品-平台数组  productPlatformList：" + productPlatformList);
        }
        generalDAO.saveOrUpdate(productPlatformList);
    }

    /**
     * 删除产品-平台数据
     *
     * @param productPlatform
     * @return
     */
    @Transactional
    public void deleteProductPlatform(ProductPlatform productPlatform) {
        if (logger.isInfoEnabled()) {
            logger.info("删除产品-平台  productPlatform：" + productPlatform);
        }
    }

    /**
     * 查询产品的促销价
     *
     * @param productId
     * @param platformId
     * @return
     */
    public Money getDiscountPrice(Integer productId, Integer platformId) {
        Search search = new Search(ProductPlatform.class)
                .addFilterEqual("prodId", productId)
                .addFilterEqual("platformId", platformId);
        ProductPlatform pp = (ProductPlatform) generalDAO.searchUnique(search);

        if (pp == null) {
            return null;
        }
        return pp.getDiscountPrice();
    }

    /**
     * 获取商品平台的一口价
     *
     * @param productId
     * @param platformId
     * @return
     */
    public Money getPrice(Integer productId, Integer platformId) {
        Search search = new Search(ProductPlatform.class)
                .addFilterEqual("prodId", productId)
                .addFilterEqual("platformId", platformId);
        ProductPlatform pp = (ProductPlatform) generalDAO.searchUnique(search);
        if (pp == null) {
            return null;
        }
        if (logger.isInfoEnabled()) {
            logger.info("++++++++++++++++++" + productId + "========" + pp.getPrice().toString());
        }
        return pp.getPrice();
    }


    /**
     * excel导入文件相关信息
     */
    private enum ProductExcelInfo {

        /**
         * 仓库名称
         */
        REPOSITORY_NAME(16),
        /**
         * 库存数量
         */
        STORAGE_NUM(17),
        /**
         * 平台单元格起始index
         */
        PLATFORM_INDEX(18),

        /**
         * 一个平台单元格数量
         */
        PLATFORM_CELL_NUM(7),

        /**
         * excel文件中平台数量(excel模版或数据导入同一调用此值，避免模版与数据库的平台数量不一致而导致的数据错误)
         */
        PLATFORM_NUM(3),
        /**
         * 平台在excel中的排序号
         */
        TB_INDEX(0),

        EJS_INDEX(1),

        JD_INDEX(2);

        public int value;

        private ProductExcelInfo(int value) {
            this.value = value;
        }

        /**
         * 获取excel中对应的平台类型
         *
         * @param index 平台在excel中的排序号
         * @return
         */
        public static PlatformType getPlatformTypeByIndex(int index) {
            return index == TB_INDEX.value ? PlatformType.TAO_BAO :
                    index == EJS_INDEX.value ? PlatformType.EJS :
                            index == JD_INDEX.value ? PlatformType.JING_DONG : null;
        }


        private class SyncProduct implements Runnable {

            private LinkedBlockingQueue<Product> queue = new LinkedBlockingQueue<Product>();

            private StorageService storageService = (StorageService) Application.getBean("storageService");

            public SyncProduct() {

            }

            @Override
            public void run() {
                try {
                    Product product = null;
                    while ((product = queue.take()) != null) {
//                        try {
////                            storageService.storageIncrementForProductInit(product.getId(), repositoryId, repositoryNum, InOutStockType.IN_STOCK_TYPE_INIT, null);
////                            syncOutPlatformDate(product);
//                        } catch (ApiInvokeException e) {
//        //                    return e.getMessage();         // 产品导入时候异常
//                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
//                return null;
            }
        }

    }

}
