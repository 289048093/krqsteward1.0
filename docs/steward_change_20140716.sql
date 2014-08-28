
/*===================================*/
/* t_original_order加上买家支付宝账号buyer_aplipay_no */
/* t_order加上买家支付宝账号buyer_aplipay_no */
/*===================================*/
use steward;

alter table t_original_order_item add title varchar(256) comment '商品名称' after outer_sku;
alter table t_order_fetch add fetch_start_time datetime comment '抓取开始时间' after id;