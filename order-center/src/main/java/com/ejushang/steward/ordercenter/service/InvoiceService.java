package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.domain.Employee;
import com.ejushang.steward.common.domain.Receiver;
import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import com.ejushang.steward.common.page.Page;
import com.ejushang.steward.common.util.*;
import com.ejushang.steward.ordercenter.constant.OrderItemReturnStatus;
import com.ejushang.steward.ordercenter.constant.OrderReturnStatus;
import com.ejushang.steward.ordercenter.constant.OrderStatus;
import com.ejushang.steward.ordercenter.constant.OrderType;
import com.ejushang.steward.ordercenter.domain.*;
import com.ejushang.steward.ordercenter.service.transportation.BrandService;
import com.ejushang.steward.ordercenter.util.Joiner;
import com.ejushang.steward.ordercenter.util.OrderExcelUtil;
import com.ejushang.steward.ordercenter.util.OrderUtil;
import com.ejushang.steward.ordercenter.vo.CollectInvoiceOrderVo;
import com.ejushang.steward.ordercenter.vo.OrderItemVo;
import com.ejushang.steward.ordercenter.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * User: Blomer
 * Date: 14-4-14
 * Time: 下午4:32
 */
@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private OrderFlowService orderFlowService;

    @Autowired
    private GeneralDAO generalDAO;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private OrderApproveService orderApproveService;

    /**
     * 订单返回客服
     * 已验货||已打印||已确认->待处理
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public void orderBackToWaitProcess(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderBackToWaitProcess方法，参数orderIds:%s", Arrays.toString(orderIds)));
        }
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.valueOf(order.getStatus().toString());
            OrderStatus to = OrderStatus.WAIT_PROCESS;
            orderFlowService.changeStatus(order, from, to, false);
        }
    }

    /**
     * 根据orderIds 进行物流单的打印
     *
     * @param orderIds
     * @return 要打印的物流单的信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> deliveryPrint(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的deliveryPrint方法,参数orderIds[%s]", Arrays.toString(orderIds)));
        }
        Search search = new Search(Order.class);
        search.addFilterIn("id", orderIds)
                .addSortAsc("invoice.shippingNo");
        //noinspection unchecked
        List<Order> orderList = generalDAO.search(search);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(orderIds.length);
        for (Order order : orderList) {
//        for (Integer id : orderIds) {
//            Order order = generalDAO.get(Order.class, id);
//            if (order == null) {
//                throw new StewardBusinessException(String.format("id为[%d]的订单不存在", id));
//            }
            Map<String, Object> map = new HashMap<String, Object>();
            Invoice invoice=order.getInvoice();
            if(invoice==null){
                throw new StewardBusinessException("找不到物流信息");
            }
            Repository repository=order.getRepo();
            if(repository==null){
                throw new   StewardBusinessException("找不到对应的仓库");
            }
            Integer goodsCount=0;
             for(OrderItem orderItem:order.getOrderItemList()){
                  goodsCount=goodsCount+orderItem.getBuyCount();
             }
            map.put("receiverPhone", invoice.getReceiver().getReceiverPhone());
            map.put("receiverMobile", invoice.getReceiver().getReceiverMobile());
            map.put("receiverAddress", invoice.getReceiver().getReceiverAddress());
            map.put("receiverState", invoice.getReceiver().getReceiverState());
            map.put("receiverCity", invoice.getReceiver().getReceiverCity());
            map.put("receiverDistrict", invoice.getReceiver().getReceiverDistrict());
            map.put("receiverName", invoice.getReceiver().getReceiverName());
            map.put("receiverZip", invoice.getReceiver().getReceiverZip());
            map.put("deliveryTime", new Date());
            map.put("repoName", order.getRepo().getName());
            map.put("repoId", order.getRepoId());
            map.put("chargePhone", repository.getChargePhone());
            map.put("chargeMobile", repository.getChargeMobile());
            map.put("repoAddress", repository.getAddress());
            map.put("shippingNo",invoice.getShippingNo());
            map.put("destCode",invoice.getDestCode());
            map.put("originCode",invoice.getOriginCode());
            map.put("sfExpressType",invoice.getSfExpressType());
            map.put("custid",repository.getCustid());
            map.put("orderNo",order.getOrderNo());
            map.put("goodsCount",goodsCount);
            List<OrderItem> orderItemList = order.getOrderItemList();
            List<Map<String, Object>> orderItemMapList = new ArrayList<Map<String, Object>>(orderItemList.size());
            for (OrderItem orderItem : orderItemList) {
                OrderItemVo orderItemVo = new OrderItemVo();
                OrderUtil.getOrderItemVo(orderItemVo, orderItem, order.getType(), null, true);
                Map<String, Object> orderItemMap = new HashMap<String, Object>();
                orderItemMap.put("productNo", orderItemVo.getProductCode());
                orderItemMap.put("buyCount", orderItemVo.getBuyCount());
                orderItemMapList.add(orderItemMap);
                if (order.getType().equals(OrderType.CHEAT)) {
                    break;
                }
            }
            map.put("orderItemMapList", orderItemMapList);
            mapList.add(map);
        }
//        }
        return mapList;
    }

    /**
     * 根据orderIds 进行订单打印
     *
     * @param orderIds
     * @return 要打印的订单的信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> orderPrint(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderPrint方法,参数orderIds[%s]", Arrays.toString(orderIds)));
        }
        List<Order> orders = listRefundingOrderByIds(orderIds);
        if (!orders.isEmpty()) {
            String orderNos = Joiner.join(orders, ",", new Joiner.Operator<Order>() {
                @Override
                public String convert(Order order) {
                    return order.getOrderNo();
                }
            });
            throw new StewardBusinessException(String.format("存在正在退款的订单，订单号[%s]", orderNos));
        }

        Search search = new Search(Order.class);
        search.addFilterIn("id", orderIds)
                .addSortAsc("invoice.shippingNo");
        //noinspection unchecked
        List<Order> orderList = generalDAO.search(search);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(orderIds.length);
        for (Order order : orderList) {
//            OrderVo orderVo= OrderUtil.getOrderVo(order);
            Map<String, Object> map = new HashMap<String, Object>();
            //map.put("orderNo", order.getOriginalOrder()); //订单模块暂时先不弄
            Money finalTotalFee = Money.valueOf("0");
            List<OrderItem> orderItemList = listInvoiceOrderItem(order.getId());
            if (!orderItemList.isEmpty()) {   //判断order中的OrderItemList是否为空
                List<Map<String, Object>> orderItemMapList = new ArrayList<Map<String, Object>>(orderItemList.size());
                for (OrderItem orderItem : orderItemList) {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    OrderUtil.getOrderItemVo(orderItemVo, orderItem, order.getType(), null, true);
                    Map<String, Object> orderItemMap = new HashMap<String, Object>();
                    orderItemMap.put("id", orderItem.getId());
                    Money actualFee = null;
//                    if(!order.getType().equals(OrderType.CHEAT)){
//                        orderItemMap.put("prodCode", orderItem.getProduct().getProductNo());
//                        orderItemMap.put("prodPrice", orderItem.getDiscountPrice());
//                        orderItemMap.put("prodName", orderItem.getProduct().getName());
//                        orderItemMap.put("color", orderItem.getProduct().getColor());
//                        orderItemMap.put("speci", orderItem.getProduct().getSpeci());
//                        orderItemMap.put("prodCount", orderItem.getBuyCount());
//                        orderItemMap.put("discountFee", orderItem.getSharedDiscountFee());
//                        actualFee = orderItem.getUserActualPrice();
//                    }
//                    else{
//                    orderItemMap.put("prodCode", orderItem.getProduct().getProductNo());
//                    orderItemMap.put("prodPrice", orderItem.getDiscountPrice());
//                    orderItemMap.put("prodName", orderItem.getProduct().getName());
//                    orderItemMap.put("color", orderItem.getProduct().getColor());
//                    orderItemMap.put("speci", orderItem.getProduct().getSpeci());
//                    orderItemMap.put("prodCount", orderItem.getBuyCount());
//                    orderItemMap.put("discountFee", orderItem.getSharedDiscountFee());
//                     actualFee = orderItem.getUserActualPrice();
//                        orderItemMap.put("prodCode", "嘻唰唰专用");
//                        orderItemMap.put("prodPrice", "0.00");
//                        orderItemMap.put("prodName", "嘻唰唰专用");
//                        orderItemMap.put("color", "");
//                        orderItemMap.put("speci", "");
//                        orderItemMap.put("prodCount", 1);
//                        orderItemMap.put("discountFee", "0.00");
//                        actualFee=Money.valueOf("0.00");
//                    }
                    orderItemMap.put("prodCode", orderItemVo.getProductCode());
                    orderItemMap.put("prodPrice", orderItemVo.getDiscountPrice());
                    orderItemMap.put("prodName", orderItemVo.getProductName());
                    orderItemMap.put("color", orderItemVo.getColor());
                    orderItemMap.put("speci", StringUtils.isBlank(orderItem.getProduct().getSpeci()) ? null : orderItem.getProduct().getSpeci());
                    orderItemMap.put("prodCount", orderItemVo.getBuyCount());
                    orderItemMap.put("discountFee", orderItemVo.getSharedDiscountFee());
                    actualFee = Money.valueOf(orderItemVo.getUserActualPrice());
                    orderItemMap.put("actualFee", actualFee);  //总金额= 单价*实际交易数量-优惠金额
//                    orderItemMap.put("totalFee", actualFee);
                    finalTotalFee = finalTotalFee.add(actualFee); //将各个订单项的金额相加,再加上邮费，得出合计
                    orderItemMapList.add(orderItemMap);
                    if (order.getType().equals(OrderType.CHEAT)) {
                        break;
                    }
                }
                map.put("orderItemMapList", orderItemMapList);
            }
            Invoice invoice = order.getInvoice();
            Receiver receiver = invoice.getReceiver();
            map.put("receiverPhone", receiver.getReceiverPhone());
            map.put("receiverMobile", receiver.getReceiverMobile());
            map.put("receiverName", receiver.getReceiverName());
            map.put("receiverAddress", receiver.getReceiverAddress());
            map.put("buyerMessage", order.getBuyerMessage());
            map.put("shippingNo", invoice.getShippingNo());
            map.put("shippingComp", invoice.getShippingComp());
            map.put("payTime", order.getPayTime());
            map.put("invoicePostFee", order.getInvoicePostFee());
            map.put("finalTotalFee", finalTotalFee.add(order.getSharedPostFee()));
            map.put("remark", order.getRemark());
            if (order.getType().equals(OrderType.CHEAT)) {
                map.put("actualFee", "0.00");
            } else {
                map.put("actualFee", order.getActualFee());
            }
            map.put("postFee", order.getSharedPostFee());
            map.put("outOrderNo", order.getPlatformOrderNo());
            map.put("chargePerson", order.getShop().getTitle());
            map.put("repoAddress", order.getRepo().getAddress());
            map.put("offlineRemark",order.getOfflineRemark());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 订单确认打印 已确认->已打印
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public void orderAffirmPrint(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderAffirmPrint方法，参数orderIds:%s", Arrays.toString(orderIds)));
        }
        List<Order> orders = listRefundingOrderByIds(orderIds);
        if (!orders.isEmpty()) {
            String orderNos = Joiner.join(orders, ",", new Joiner.Operator<Order>() {
                @Override
                public String convert(Order order) {
                    return order.getOrderNo();
                }
            });
            throw new StewardBusinessException(String.format("存在正在退款的订单，订单号[%s]", orderNos));
        }
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.CONFIRMED;
            OrderStatus to = OrderStatus.PRINTED;

            orderFlowService.changeStatus(order, from, to, true);
        }
    }

    /**
     * 查询正在退款订单
     *
     * @param ids
     * @return
     */
    private List<Order> listRefundingOrderByIds(Integer[] ids) {
        String idsStr = Joiner.join(ids, ",");
        String hql = String.format("select order_ from Order order_ " +
                "where order_.id " +
                "in ( select item_.orderId from OrderItem item_ where item_.refunding=true and item_.orderId in (%s) )", idsStr);
        //noinspection unchecked
        return generalDAO.getSession().createQuery(hql).list();
    }

    /**
     * 查询正在退款订单
     *
     * @param shipping_no
     * @return
     */
    private List<Order> listRefundingOrderByShippings(String[] shipping_no) {
        String idsStr = "'"+Joiner.join(shipping_no, "','")+"'";
        String hql = String.format("select distinct order2_ from OrderItem item_ left join item_.order order2_ where item_.refunding=true and order2_.invoice.shippingNo in (%s))  ", idsStr);
        //noinspection unchecked
        return generalDAO.getSession().createQuery(hql).list();
    }

    /**
     * 返回已导入 已打印->已确认
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public void orderBackToConfirm(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderBackToConfirm方法,参数orderIds:%s", Arrays.toString(orderIds)));
        }
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.PRINTED;
            OrderStatus to = OrderStatus.CONFIRMED;
            orderFlowService.changeStatus(order, from, to, true);
        }
    }

    /**
     * 订单批量验证 已打印->已验证
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public String orderBatchExamine(Integer[] orderIds) {
        List<Order> orders = listRefundingOrderByIds(orderIds);
        if (!orders.isEmpty()) {
            String orderNos = Joiner.join(orders, ",", new Joiner.Operator<Order>() {
                @Override
                public String convert(Order order) {
                    return order.getOrderNo();
                }
            });
            throw new StewardBusinessException(String.format("存在正在退款的订单，订单号[%s]", orderNos));
        }
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderBatchExamine方法，参数orderIds:%s", Arrays.toString(orderIds)));
        }
        StringBuilder sb = new StringBuilder();
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.PRINTED;
            OrderStatus to = OrderStatus.EXAMINED;
            try {
                orderFlowService.changeStatus(order, from, to, true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sb.append("外部平台订单编号[").append(order.getPlatformOrderNo()).append("]的订单验货异常：").append(e.getMessage()).append(";\r\n");
            }
        }
        if (StringUtils.isNotBlank(sb)) {
            sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
        //TODO 同步外部订单状态为已发货
    }

    /**
     * 验货功能
     *
     * @param shippingNos
     * @return 在验货时要显示的Vo
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> orderInspection(String[] shippingNos) {
        List<Order> orders = listRefundingOrderByShippings(shippingNos);
        if (!orders.isEmpty()) {
            String orderNos = Joiner.join(orders, ",", new Joiner.Operator<Order>() {
                @Override
                public String convert(Order order) {
                    return order.getOrderNo();
                }
            });
            throw new StewardBusinessException(String.format("正在退款的订单，订单号[%s]", orderNos));
        }

        Search search = new Search(Order.class);
        search.addFilterIn("invoice.shippingNo", shippingNos)
                .addFilterNotEqual("orderReturnStatus", OrderReturnStatus.RETURNED)
                .addFilterEqual("valid", true)
//                .addFilterEqual("refunding", false)
                .addFilterEqual("status", OrderStatus.PRINTED);
        //noinspection unchecked
        List<Order> orderList = generalDAO.search(search);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>(orderList.size());
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<String, Object>();
            boolean refunding = false; //是否处于退货状态
            List<OrderItem> orderItemList = listInvoiceOrderItem(order.getId());
            if (!orderItemList.isEmpty()) {
                List<Map<String, Object>> orderItemInspectionVoMapList = new ArrayList<Map<String, Object>>(orderItemList.size());
                for (OrderItem orderItem : orderItemList) {
                    if (orderItem.getRefunding()) {  //正在申请退款的订单项
                        refunding = true;
                        continue;
                    }
                    Map<String, Object> orderItemInspectionVoMap = new HashMap<String, Object>();
                    orderItemInspectionVoMap.put("skuCode", orderItem.getProductSku());
                    orderItemInspectionVoMap.put("prodName", orderItem.getProductName());
                    orderItemInspectionVoMap.put("orderNo", orderItem.getOrder().getOrderNo());
                    orderItemInspectionVoMap.put("prodCode", orderItem.getProductCode());
                    if (orderItem.getProduct() != null) {
                        if (productCategoryService.get(orderItem.getProduct().getCategory().getId()) != null) {
                            orderItemInspectionVoMap.put("categoryName", orderItem.getProduct().getCategory().getName());
                        }
                        //查找对应的创库()的这个商品的库存
                        Storage storage = storageService.findByProductIdAndRepositoryId(orderItem.getProductId(), order.getRepoId());
                        orderItemInspectionVoMap.put("amoumt", storage == null ? 0 : storage.getAmount());
                        if (brandService.get(orderItem.getProduct().getBrandId()) != null) {
                            orderItemInspectionVoMap.put("brandName", orderItem.getProduct().getBrand().getName());
                        }
                    }
                    //orderItemInspectionVoMap.put("prodPrice", orderItem.getProduct().getImportPrice());
                    orderItemInspectionVoMap.put("prodPrice", orderItem.getPrice());
                    orderItemInspectionVoMap.put("prodCount", orderItem.getBuyCount());
                    orderItemInspectionVoMapList.add(orderItemInspectionVoMap);
                }
                map.put("orderItemInspectionVoMapList", orderItemInspectionVoMapList);
            }
            if (refunding) {   //正在申请退款的订单不显示
                continue;
            }
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("shippingNo", order.getInvoice().getShippingNo());
            map.put("orderStatus", order.getStatus());
            map.put("shippingComp", order.getInvoice().getShippingComp());
            map.put("receiverName", order.getInvoice().getReceiver().getReceiverName());
            map.put("remark", order.getRemark());
            map.put("buyerMessage", order.getBuyerMessage());
            OrderApprove orderApprove = orderApproveService.findByOrderStatusWithOrderId(order.getStatus(), order.getId());
            if (null != orderApprove.getOperatorId()) {
                map.put("confirmUser", EmployeeUtil.getOperatorName(orderApprove.getOperatorId()));
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 返回已打印 已验货->已打印
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public void orderBackToPrint(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderBackToPrint方法，参数orderIds:%s", Arrays.toString(orderIds)));
        }
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.EXAMINED;
            OrderStatus to = OrderStatus.PRINTED;
            orderFlowService.changeStatus(order, from, to, true);
        }
    }

    /**
     * 订单发货 已验证->已发货
     *
     * @param orderIds
     * @return
     */
    @Transactional
    public void orderInvoice(Integer[] orderIds) {
        if (log.isInfoEnabled()) {
            log.info(String.format("OrderService中的orderInvoice方法，参数orderIds[%s]", Arrays.toString(orderIds)));
        }
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.EXAMINED;
            OrderStatus to = OrderStatus.INVOICED;
            orderFlowService.changeStatus(order, from, to, true);
        }
    }

    @Transactional(readOnly = true)
    public Invoice findInvoiceByOrderId(Integer id) {
        String hql = "select i from Order o join o.invoice i where o.id = ?";
        List<Invoice> results = generalDAO.query(hql, null, new Object[]{id});
        if (results.isEmpty()) return null;
        return results.get(0);
    }
    @Transactional(readOnly = true)
    public Invoice findInvoiceByOrderNo(String orderNo) {
        String hql = "select i from Order o join o.invoice i where  o.orderNo= ? ";
        List<Invoice> results = generalDAO.query(hql, null, new Object[]{orderNo});
        Invoice invoice=null;
        if (results.isEmpty()){
            return null;
        }
        else{
                invoice=results.get(0);
        }
        return invoice;
    }

    @Transactional
    public void save(Invoice invoice) {
        generalDAO.saveOrUpdate(invoice);
    }


    /**
     * 查询发货订单
     *
     * @param page
     */
    @Transactional(readOnly = true)
    public void listInvoiceOrder(Map<String, Object[]> map, Page page) throws ParseException {

        Map<String, String> conditionsMap = new HashMap<String, String>();
        //HQL条件的值
        List<Object> objects = new ArrayList<Object>();
        //将接受到的map参数解析并赋值给map1
        OrderUtil.getConditionMap(map, conditionsMap);
        //拼接HQL的变量
        StringBuilder stringBuilder = new StringBuilder();
        Employee employee = SessionUtils.getEmployee();
        List<Integer> repoIdList = new ArrayList<Integer>();
        if (employee.isRepositoryEmployee()) {
            Search search = new Search(RepositoryCharger.class).addFilterEqual("chargerId", employee.getId());
            search.setDistinct(true);
            List<RepositoryCharger> repositories = generalDAO.search(search);
            if (repositories.isEmpty()) {
                return;
            }
            for (RepositoryCharger repositoryCharger : repositories) {
                repoIdList.add(repositoryCharger.getRepository().getId());
            }
        }
        //拼接HQL的方法
        OrderUtil.orderCondition(conditionsMap, stringBuilder, objects, repoIdList);
        //执行HQL
        List<Order> orders = generalDAO.query(stringBuilder.toString(), page, objects.toArray());
        List<OrderVo> vos = new ArrayList<OrderVo>();
        for (Order order : orders) {
            List<OrderItem> validItems = null;
            if (conditionsMap.get("invoiceInvalid") == null) {
                validItems = listInvoiceOrderItem(order.getId());
            } else {
                validItems = generalDAO.search(new Search(OrderItem.class).addFilterEqual("valid", true).addFilterEqual("orderId", order.getId()));
            }
            OrderUtil.getOrderVo(order, vos, validItems, true,null);
        }
        page.setResult(vos);
    }

    public Workbook reportInvoiceOrders(Map<String, Object[]> map) throws ParseException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        OrderExcelUtil.createExcelInvoiceOrderTitle(sheet);
        int rowIndex = 2;//从第三行开始，一二行放title
        Map<String, String> conditionsMap = new HashMap<String, String>();
        //HQL条件的值
        List<Object> objects = new ArrayList<Object>();
//        sortParam:remark#ASC
        conditionsMap.put("sortParam", "o.invoice.shippingComp#ASC");
        //将接受到的map参数解析并赋值给map1
        OrderUtil.getConditionMap(map, conditionsMap);
        //拼接HQL的变量
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> repoIdList = new ArrayList<Integer>();
        Employee employee = SessionUtils.getEmployee();
        if (employee.isRepositoryEmployee()) {
            //noinspection unchecked
//            List<Repository> repositories = generalDAO.search(new Search(Repository.class).addFilterEqual("chargePersonId", employee.getId()));
//            for (Repository repository : repositories) {
//                repoIdList.add(repository.getId());
//            }
            List<RepositoryCharger> repositories = generalDAO.search(new Search(RepositoryCharger.class).addFilterEqual("chargerId", employee.getId()));
            for (RepositoryCharger repositoryCharger : repositories) {
                repoIdList.add(repositoryCharger.getRepository().getId());
            }
        }
        OrderUtil.orderCondition(conditionsMap, stringBuilder, objects, repoIdList);
        List<Order> orders = generalDAO.query(stringBuilder.toString(), null, objects.toArray());
//        if(orders.size()>5000){
//            throw new StewardBusinessException("导出订单不能超过5000条");
//        }
        List<OrderVo> vos = new ArrayList<OrderVo>();
        int productNum = 0;
        for (Order order : orders) {
            List<OrderItem> validItems = null;
            if (conditionsMap.get("invoiceInvalid") == null) {
                validItems = listInvoiceOrderItem(order.getId());
            } else {
                validItems = generalDAO.search(new Search(OrderItem.class).addFilterEqual("valid", true).addFilterEqual("orderId", order.getId()));
            }
            if (validItems.size() == 0) {
                throw new StewardBusinessException("导出失败，有订单没有对应的订单项");
            }
            OrderVo orderVo = OrderUtil.getOrderVo(order, vos, validItems, true,null);
            for (OrderItem orderItem : validItems) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;

                OrderItemVo orderItemVo = new OrderItemVo();
                OrderUtil.getOrderItemVo(orderItemVo, orderItem, order.getType(), null, true);
                OrderExcelUtil.renderInvoiceOrderVoExcel(row, cellIndex, orderVo, orderItemVo);
                if (order.getType().equals(OrderType.CHEAT)) {
                    productNum = productNum + 1;
                    break;
                } else {
                    productNum = productNum + orderItem.getBuyCount();
                }
            }

        }
        OrderExcelUtil.collectNum(rowIndex, workbook, sheet, orders.size(), productNum);
        return workbook;
    }


    /**
     * 订单查询条件拼接
     *
     * @param search
     * @param conditionType
     * @param conditionQuery
     * @param conditionValue
     */
    private void configOrderConditionSearch(Search search, String conditionType, String conditionQuery, String conditionValue) {
        if ("prodCode".equals(conditionQuery)) {
            conditionQuery = "orderItemList.product.productNo";
            search.setDistinct(true);
        }
        if ("prodName".equals(conditionQuery)) {
            conditionQuery = "orderItemList.product.name";
            search.setDistinct(true);
        }
        if ("shippingNo".equals(conditionQuery)) {
            conditionQuery = "invoice.shippingNo";
        }
        if ("has".equals(conditionType)) {
            search.addFilterILike(conditionQuery, String.format("%%%s%%", conditionValue));
        } else if ("!".equals(conditionType)) {
            search.addFilterNotEqual(conditionQuery, conditionValue);
        }
    }

    /**
     * 查询发货的订单的定的项
     * <p/>
     * 过滤了无效的/退货的/售前退货的订单项
     *
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderItem> listInvoiceOrderItem(Integer orderId) {
        Search search = new Search(OrderItem.class);
        search.addFilterEqual("valid", true)
                .addFilterEqual("returnStatus", OrderItemReturnStatus.NORMAL)
                .addFilterEqual("offlineReturnStatus", OrderItemReturnStatus.NORMAL)
//                .addFilterNotEqual("type", OrderItemType.EXCHANGE_AFTERSALE)
                .addFilterEqual("orderId", orderId);
        //        for (OrderItem oi : items) {
//            Storage storage = storageService.findByProductId(oi.getProductId());
//            oi.setRepoNum(storage == null ? 0 : storage.getAmount());
//        }
        //noinspection unchecked
        return generalDAO.search(search);
    }

    @Transactional(readOnly = true)
    public List<OrderItemVo> listInvoiceOrderItemByOrderId(Integer orderId, Boolean invalid) {
        Search search = new Search(OrderItem.class);
        search.addFilterEqual("valid", true);
        if (invalid == false) {
            search.addFilterEqual("returnStatus", OrderItemReturnStatus.NORMAL)
                    .addFilterEqual("offlineReturnStatus", OrderItemReturnStatus.NORMAL);
        }
//                .addFilterNotEqual("type", OrderItemType.EXCHANGE_AFTERSALE)
        search.addFilterEqual("orderId", orderId);
        //        for (OrderItem oi : items) {
//            Storage storage = storageService.findByProductId(oi.getProductId());
//            oi.setRepoNum(storage == null ? 0 : storage.getAmount());
//        }
        //noinspection unchecked
        List<OrderItem> orderItems = generalDAO.search(search);
        if (orderItems.size() == 0) {
            throw new StewardBusinessException("找不到对应订单项");
        }
        Order order = orderItems.get(0).getOrder();
        List<OrderItemVo> orderItemVos = new ArrayList<OrderItemVo>();

        for (OrderItem orderItem : orderItems) {
            OrderUtil.getInvoiceOrderItemVos(orderItem, order.getType(), orderItemVos);
            if (order.getType().equals(OrderType.CHEAT)) {
                break;
            }
        }
        return orderItemVos;
    }

    /**
     * 根据id查询收货信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Integer id) {
        Search search = new Search(Invoice.class);
        search.addFilterEqual("id", id);
        return (Invoice) generalDAO.searchUnique(search);
    }

    /**
     * 签收订单
     *
     * @param orderIds
     */
    @Transactional
    public void signed(Integer[] orderIds) {
        for (Integer orderId : orderIds) {
            Order order = generalDAO.get(Order.class, orderId);
            OrderStatus from = OrderStatus.INVOICED;
            OrderStatus to = OrderStatus.SIGNED;
            orderFlowService.changeStatus(order, from, to, false);
        }
    }

    /**
     * 查询物流单号是不是在系统已经存在
     *
     * @param numList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Invoice> findExistShippingNoInvoice(List<String> numList) {
        if (numList == null || numList.isEmpty()) return new ArrayList<Invoice>();
        Search search = new Search(Invoice.class);
        search.addFilterIn("shippingNo", numList);
        //noinspection unchecked
        return generalDAO.search(search);
    }

    /**
     * 查询物流单号是不是在系统已经存在，除去指定的订单号
     * <p/>
     * 联想批量物流单号时，如果需要覆盖的物流单号,则新生成的物流单号可以在需要覆盖的物流单号之中
     *
     * @param numList
     * @return
     */
    @Transactional(readOnly = true)
    public List<Invoice> findExistShippingNoInvoiceByNotInOrderIds(List<String> numList, Integer[] orderIds) {
        if (numList == null || numList.isEmpty()) return new ArrayList<Invoice>();
        String shippingNoKey = Joiner.join(numList, ",", new Joiner.Operator<String>() {
            @Override
            public String convert(String s) {
                return String.format("'%s'", s);
            }
        });
        String orderIdKey = Joiner.join(orderIds, ",");
        String hql = String.format("select i from Order o left join o.invoice i " +
                "where i.shippingNo in(%s) and o.id not in(%s) and o.valid=true", shippingNoKey, orderIdKey);
        Query query = generalDAO.getSession().createQuery(hql);
        //noinspection unchecked
        return query.list();
    }

    public Workbook collectInvoiceOrderExcel(Map<String, Object[]> map) throws ParseException {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int prodNum = 0;
        Map<String, String> conditionsMap = new HashMap<String, String>();
        //将接受到的map参数解析并赋值给map1
        OrderUtil.getConditionMap(map, conditionsMap);
        List<Order> orders = new ArrayList<Order>();
        //记录嘻唰唰订单，将其汇总到最后一列
        CollectInvoiceOrderVo lastVo = null;
        List<Object> objects = new ArrayList<Object>();
        List<CollectInvoiceOrderVo> collectInvoiceOrderVos = new ArrayList<CollectInvoiceOrderVo>();
        if (StringUtils.isBlank(conditionsMap.get("orderIds")) || conditionsMap.get("orderIds").equals("null")) {
            //拼接HQL的变量
            StringBuilder stringBuilder = new StringBuilder();
            List<Integer> repoIdList = new ArrayList<Integer>();
            Employee employee = SessionUtils.getEmployee();
            if (employee.isRepositoryEmployee()) {
                //noinspection unchecked
                String hql = "select distinct rc.repository from RepositoryCharger rc where rc.chargerId=" + employee.getId();
                List<Repository> repositories = generalDAO.getSession().createQuery(hql).list();
                for (Repository repository : repositories) {
                    repoIdList.add(repository.getId());
                }
            }
            OrderUtil.orderCondition(conditionsMap, stringBuilder, objects, repoIdList);
            orders = generalDAO.query(stringBuilder.toString(), null, objects.toArray());
        } else {
            String[] stringIds = conditionsMap.get("orderIds").split(",");
            Integer[] ids = new Integer[stringIds.length];
            for (int i = 0; i < stringIds.length; i++) {
                ids[i] = Integer.parseInt(stringIds[i]);
            }
            for (int i : ids) {
                Order order = generalDAO.get(Order.class, i);
                orders.add(order);
            }

        }
        for (Order order : orders) {
            List<OrderItemVo> orderItemVos = null;
            if (conditionsMap.get("invoiceInvalid") == null) {
                orderItemVos = listInvoiceOrderItemByOrderId(order.getId(), false);
            } else {
                orderItemVos = listInvoiceOrderItemByOrderId(order.getId(), true);

            }
            for (OrderItemVo orderItem : orderItemVos) {
                prodNum = prodNum + Integer.parseInt(orderItem.getBuyCount());
                CollectInvoiceOrderVo collectInvoiceOrderVo = new CollectInvoiceOrderVo();
                collectInvoiceOrderVo.setShippingNos(order.getInvoice().getShippingNo());
                collectInvoiceOrderVo.setBuyCount(Integer.parseInt(orderItem.getBuyCount()));
                collectInvoiceOrderVo.setProductName(orderItem.getProductName());
                collectInvoiceOrderVo.setProductNo(orderItem.getProductCode());
                collectInvoiceOrderVo.setGoodsFee(orderItem.getGoodsFee());
                collectInvoiceOrderVos.add(collectInvoiceOrderVo);
            }
        }
        List<CollectInvoiceOrderVo> collectInvoiceOrderVoList = new ArrayList<CollectInvoiceOrderVo>();
        for (int i = 0; i < collectInvoiceOrderVos.size(); i++) {
            CollectInvoiceOrderVo collectInvoiceOrderVo2 = new CollectInvoiceOrderVo();
            CollectInvoiceOrderVo collectInvoiceOrderVo = collectInvoiceOrderVos.get(i);
            collectInvoiceOrderVo2.setShippingNos(new String(collectInvoiceOrderVo.getShippingNos() != null ? collectInvoiceOrderVo.getShippingNos() : ""));
            collectInvoiceOrderVo2.setBuyCount(new Integer(collectInvoiceOrderVo.getBuyCount()));
            collectInvoiceOrderVo2.setGoodsFee(Money.valueOf(collectInvoiceOrderVo.getGoodsFee().toString()));
            collectInvoiceOrderVo2.setProductName(collectInvoiceOrderVo.getProductName());
            collectInvoiceOrderVo2.setProductNo(collectInvoiceOrderVo.getProductNo());
            for (int j = 0; j < collectInvoiceOrderVos.size(); j++) {
                if (i == j) {
                    continue;
                }
                CollectInvoiceOrderVo collectInvoiceOrderVo1 = collectInvoiceOrderVos.get(j);
                if (collectInvoiceOrderVo.getProductNo().equals(collectInvoiceOrderVo1.getProductNo())) {
                    collectInvoiceOrderVo2.setBuyCount(collectInvoiceOrderVo2.getBuyCount() + collectInvoiceOrderVo1.getBuyCount());
                    collectInvoiceOrderVo2.setGoodsFee(collectInvoiceOrderVo2.getGoodsFee().add(collectInvoiceOrderVo1.getGoodsFee()));
                    StringBuilder shippingNos = new StringBuilder();
                    if (StringUtils.isNotBlank(collectInvoiceOrderVo2.getShippingNos())) {
                        shippingNos.append(collectInvoiceOrderVo2.getShippingNos());
                    }
                    if (StringUtils.isNotBlank(collectInvoiceOrderVo1.getShippingNos())) {
                        if (StringUtils.isNotBlank(collectInvoiceOrderVo2.getShippingNos())) {
                            shippingNos.append(",").append(collectInvoiceOrderVo1.getShippingNos());
                        } else {
                            shippingNos.append(collectInvoiceOrderVo1.getShippingNos());
                        }
                    }
                    collectInvoiceOrderVo2.setShippingNos(shippingNos.toString());
                    collectInvoiceOrderVos.remove(j);
                    if (j > 0) {
                        j = j - 1;
                    }
                }
            }
            if (collectInvoiceOrderVo2.getProductName().equals("嘻唰唰专用")) {
                collectInvoiceOrderVo2.setGoodsFee(Money.valueOf("0.00"));
                lastVo = collectInvoiceOrderVo2;
            } else {
                collectInvoiceOrderVoList.add(collectInvoiceOrderVo2);
            }
        }

        OrderExcelUtil.collectInvoiceOrderExcelTitle(sheet);
        int rowNum = 2;
        for (int i = 0; i < collectInvoiceOrderVoList.size(); i++) {
            CollectInvoiceOrderVo collectInvoiceOrderVo = collectInvoiceOrderVoList.get(i);
            Row row = sheet.createRow(rowNum++);
            OrderExcelUtil.collectInvoiceOrderExcel(row, 0, collectInvoiceOrderVo);
        }
        if (lastVo != null) {
            Row row = sheet.createRow(rowNum++);
            OrderExcelUtil.collectInvoiceOrderExcel(row, 0, lastVo);
        }
        OrderExcelUtil.collectNum(rowNum, workbook, sheet, orders.size(), prodNum);
        return workbook;
    }

}

