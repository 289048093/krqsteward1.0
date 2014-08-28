package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.domain.Area;
import com.ejushang.steward.common.domain.City;
import com.ejushang.steward.common.domain.Province;
import com.ejushang.steward.common.domain.Receiver;
import com.ejushang.steward.common.exception.SfBusinessException;
import com.ejushang.steward.common.service.EmployeeService;
import com.ejushang.steward.common.util.JaxbUtil;
import com.ejushang.steward.ordercenter.constant.SfConstant;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.domain.sfreq.Cargo;
import com.ejushang.steward.ordercenter.domain.sfreq.Request;
import com.ejushang.steward.ordercenter.domain.sfreq.Body;
import com.ejushang.steward.ordercenter.domain.sfreq.ordersearch.SfOrderSearchReq;
import com.ejushang.steward.ordercenter.domain.sfreq.ordersearch.SfOrderSearchReqBody;
import com.ejushang.steward.ordercenter.domain.sfreq.ordersearch.SfOrderSearchRequest;
import com.ejushang.steward.ordercenter.domain.sfrsp.Response;
import com.ejushang.steward.ordercenter.domain.sfrsp.ordersearch.SfOrderSearchResponse;
import com.ejushang.steward.ordercenter.service.transportation.ProductService;
import com.ejushang.steward.ordercenter.util.SfAddressUtil;
import com.sf.integration.expressservice.service.CommonServiceService;
import com.sf.integration.expressservice.service.IService;
import com.sf.integration.expressservice.service.SfexpressServiceResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * User: Baron.Zhang
 * Date: 2014/8/18
 * Time: 11:33
 */
@Service
@Transactional
public class SfCommonService {

    private static final Logger log = LoggerFactory.getLogger(OrderFlowService.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    @Transactional(readOnly = true)
    public Response getSFLogisticsGeneratorNum(Order order,SfConstant.SF_EXPRESS_TYPE expressType) throws ExecutionException, InterruptedException {

        if(log.isInfoEnabled()){
            log.info("顺丰下单：参数:{}",order);
        }

        CommonServiceService service = new CommonServiceService();

        IService port = service.getCommonServicePort();

        String requestXml = getReqStr(getRequest(order,expressType));
        if(log.isDebugEnabled()){
            log.debug("顺丰下单：参数:{}", requestXml);
        }

        javax.xml.ws.Response<SfexpressServiceResponse> rsp = port.sfexpressServiceAsync(requestXml);

        while(!rsp.isDone()){
            if(log.isDebugEnabled()){
                log.debug("顺丰下单：正在通讯中……");
            }
        }

        SfexpressServiceResponse response = rsp.get();
        String rspStr = response.getReturn();
        Response sfRsp = JaxbUtil.converyToJavaBean(rspStr, Response.class);
        if(log.isInfoEnabled()){
            log.info("顺丰下单：返回结果:{}",rspStr);
        }
        return sfRsp;
    }

    public String getReqStr(Request request){
        if(request == null){
            return null;
        }
        return JaxbUtil.convertToXml(request);
    }

    /**
     * 获取请求对象
     * @param orderOri
     * @return
     */
    public Request getRequest(Order orderOri,SfConstant.SF_EXPRESS_TYPE expressType){
        Request request = new Request();
        request.setHead(SfConstant.SF_REQ_HEAD);
        request.setLang(SfConstant.SF_LANG);
        request.setService(SfConstant.SF_ORDER_SERVICE);

        Body body = new Body();
        com.ejushang.steward.ordercenter.domain.sfreq.Order order = new com.ejushang.steward.ordercenter.domain.sfreq.Order();
        // 订单号
        order.setOrderid(orderOri.getOrderNo());
        // 快件产品类别(可根据需要定制扩展)
        order.setExpressType(expressType.getValue());

        // 获取仓库信息（作为发货信息）
        Repository repository = repositoryService.get(orderOri.getRepoId());
        if(repository == null){
            throw new SfBusinessException("订单对应的仓库不能为空");
        }


        // 获取店铺信息
        Shop shop = shopService.getById(orderOri.getShopId());
        if(repository == null){
            throw new SfBusinessException("订单对应的店铺不能为空");
        }
        // 获取收货人信息
        Invoice invoice = invoiceService.getInvoiceById(orderOri.getInvoiceId());
        if(invoice == null){
            throw new SfBusinessException("订单对应的收货人信息不能为空【invoice】");
        }
        Receiver receiver = invoice.getReceiver();
        if(receiver == null){
            throw new SfBusinessException("订单对应的收货人信息不能为空【receiver】");
        }

        if(shop != null) {
            // 设置发货人
            order.setJCompany(shop.getTitle());
            // 设置发货联系人
            order.setJContact(shop.getTitle());
        }

        if(repository != null) {
            order.setJTel(repository.getChargePhone());
            order.setJMobile(repository.getChargeMobile());
            // 设置省
            Province province = repository.getProvince();
            if(province == null){
                throw new SfBusinessException("订单对应的发货人省不能为空");
            }
            String provinceStr = province != null ? province.getName() : null;
            order.setJProvince(getProvince(provinceStr));
            // 设置市
            City city = repository.getCity();
            if(city == null){
                throw new SfBusinessException("订单对应的发货人市不能为空");
            }
            String cityStr = city != null ? city.getName() : null;
            order.setJCity(cityStr);
            // 设置区
            Area area = repository.getArea();
            String areaStr = area != null ? area.getName() : null;
            order.setJCounty(areaStr);
            // 设置详细地址
            order.setJAddress(repository.getAddress());

        }

        if(receiver != null) {
            // 设置收货人
            order.setDCompany(receiver.getReceiverName());
            // 设置收货联系人
            order.setDContact(receiver.getReceiverName());
            order.setDTel(receiver.getReceiverPhone());
            order.setDMobile(receiver.getReceiverMobile());
            if(StringUtils.isBlank(receiver.getReceiverState())){
                throw new SfBusinessException("订单对应的收货人省不能为空");
            }
            order.setDProvince(getProvince(receiver.getReceiverState()));
            if(StringUtils.isBlank(receiver.getReceiverCity())){
                throw new SfBusinessException("订单对应的收货人市不能为空");
            }
            order.setDCity(receiver.getReceiverCity());
            order.setDCounty(receiver.getReceiverDistrict());
            // 设置详细地址
            order.setDAddress(receiver.getReceiverAddress());
        }

        // 设置包裹数量
        order.setParcelQuantity(SfConstant.PARCEL_QUANTITY);
        // 设置邮费支付方式
        order.setPayMethod(SfConstant.PAY_METHOD.JI_FANG_FU.getValue());
        if(StringUtils.isBlank(repository.getCustid())){
            throw new SfBusinessException("订单对应的仓库月结账号不能为空");
        }
        // 设置月结账号
        order.setCustid(repository.getCustid());
        // 设置包裹总重量
        //order.setCargoTotalWeight(getCargoTotalWeight(orderOri).toString());
        //order.setSendstarttime("");
        order.setRemark(orderOri.getRemark());

        // 设置商品信息
        List<Cargo> cargoList = convertOrderItems2Cargos(orderOri.getOrderItemList());
        order.setCargo(cargoList);

        // 设置货到付款信息
        /*List<AddedService> addedServiceList = new ArrayList<AddedService>();
        AddedService addedService1 = new AddedService();
        addedService1.setName("COD");
        addedService1.setValue("2000");
        addedService1.setValue1("7501638868");
        addedServiceList.add(addedService1);
        order.setAddedService(addedServiceList);*/

        body.setOrder(order);
        request.setBody(body);
        return request;
    }

    /**
     * 批量将订单项转换为运单商品信息
     * @param orderItemList
     * @return
     */
    private List<Cargo> convertOrderItems2Cargos(List<OrderItem> orderItemList){
        List<Cargo> cargoList = new ArrayList<Cargo>();
        if(CollectionUtils.isEmpty(orderItemList)){
            return cargoList;
        }

        for(OrderItem orderItem : orderItemList){
            Cargo cargo = convertOrderItem2Cargo(orderItem);
            if(cargo != null){
                cargoList.add(cargo);
            }
        }
        return cargoList;
    }

    /**
     * 将订单项转换为运单商品信息
     * @param orderItem
     * @return
     */
    private Cargo convertOrderItem2Cargo(OrderItem orderItem){
        if(orderItem == null){
            return null;
        }

        Product product = productService.findProductBySKU(orderItem.getProductSku());
        if(product == null){
            return null;
        }

        Cargo cargo = new Cargo();
        cargo.setName(product.getName());
        cargo.setCount(String.valueOf(orderItem.getBuyCount()));
        //cargo.setUnit("");
        cargo.setWeight(getWeight(product.getWeight()));
        cargo.setAmount(orderItem.getActualFee().toString());
        //cargo.setCurrency("");
        //cargo.setSourceArea("");
        return cargo;
    }

    public static void main(String[] args) {
        System.out.println(getWeight("10.99KG"));
    }

    private static String getWeight(String weight){
        if(StringUtils.isBlank(weight)){
            return null;
        }

        String wg = weight.toLowerCase();

        if(wg.endsWith("kg")){
            wg = wg.substring(0,wg.length()-"kg".length());
        }

        return wg;
    }

    /**
     * 获取订单的总重量信息
     * @param order
     * @return
     */
    private BigDecimal getCargoTotalWeight(Order order){
        return BigDecimal.ONE;
    }

    /**
     * 获取顺丰省份信息
     * @param province
     * @return
     */
    private static String getProvince(String province){
        if(StringUtils.isBlank(province)){
            return null;
        }

        if(province.contains("北京")){
            return "北京";
        }

        if(province.contains("上海")){
            return "上海";
        }

        if(province.contains("重庆")){
            return "重庆";
        }

        if(province.contains("天津")){
            return "天津";
        }

        if(province.contains("新疆")){
            return "新疆维吾尔自治区";
        }

        if(province.contains("西藏")){
            return "西藏自治区";
        }

        if(province.contains("宁夏")){
            return "宁夏回族自治区";
        }

        if(province.contains("广西")){
            return "广西壮族自治区";
        }

        if(province.contains("内蒙古")){
            return "内蒙古自治区";
        }

        if(!province.endsWith("省")){
            province = province + "省";
        }

        return province;
    }

    /**
     * 获得顺丰要求的详细地址
     * @param address
     * @return
     */
    private static String getAddress(String address){
        if(StringUtils.isBlank(address)){
            return null;
        }

        List<String> provinceList = SfAddressUtil.provinceList;
        List<String> autoRegionList = SfAddressUtil.autoRegionList;
        List<String> mdugcList = SfAddressUtil.mdugcList;

        for(String str :provinceList){
            if(address.startsWith(str)){
                address = address.substring(str.length());
                if(!address.startsWith("省")){
                    return str+"省"+address;
                }
                return str+address;
            }
        }

        for(String str :autoRegionList){
            if(address.startsWith(str)){
                return address;
            }
        }

        for(String str :mdugcList){
            if(address.startsWith(str)){
                address = address.substring(str.length());
                if(address.startsWith("市")){
                    address = address.substring("市".length());
                    return str+address;
                }
                return str+address;
            }
        }
        return address;
    }

    /**
     * 查询已存在对应订单的顺丰运单号
     * @return
     */
    public SfOrderSearchResponse getExistedSfMailNo(String orderNo) throws ExecutionException, InterruptedException {
        if(log.isInfoEnabled()){
            log.info("查询订单的顺丰运单号：智库城订单号：{}",orderNo);
        }

        CommonServiceService service = new CommonServiceService();

        IService port = service.getCommonServicePort();

        // String requestXml = getReqStr(getRequest(order,expressType));
        String requestXml = getSfOrderSearchRequestStr(getSfOrderSearchRequest(orderNo));
        if(log.isDebugEnabled()){
            log.debug("查询订单的顺丰运单号：参数:{}", requestXml);
        }

        javax.xml.ws.Response<SfexpressServiceResponse> rsp = port.sfexpressServiceAsync(requestXml);

        while(!rsp.isDone()){
            if(log.isDebugEnabled()){
                log.debug("查询订单的顺丰运单号：正在通讯中……");
            }
        }

        SfexpressServiceResponse response = rsp.get();
        String rspStr = response.getReturn();
        SfOrderSearchResponse sfRsp = JaxbUtil.converyToJavaBean(rspStr, SfOrderSearchResponse.class);
        if(log.isInfoEnabled()){
            log.info("查询订单的顺丰运单号：返回结果:{}",rspStr);
        }
        return sfRsp;
    }

    public String getSfOrderSearchRequestStr(SfOrderSearchRequest request){
        if(request == null){
            return null;
        }
        return JaxbUtil.convertToXml(request);
    }

    public SfOrderSearchRequest getSfOrderSearchRequest(String orderNo){
        SfOrderSearchRequest request = new SfOrderSearchRequest();
        request.setService(SfConstant.SF_ORDER_SEARCH_SERVICE);
        request.setHead(SfConstant.SF_REQ_HEAD);
        request.setLang(SfConstant.SF_LANG);
        SfOrderSearchReqBody body = new SfOrderSearchReqBody();
        SfOrderSearchReq sfOrderSearchReq = new SfOrderSearchReq();
        //String orderId = "XJFS_07110006";
        sfOrderSearchReq.setOrderid(orderNo);
        body.setSfOrderSearchReq(sfOrderSearchReq);
        request.setBody(body);
        return request;
    }

}
