
/*===================================*/
/* t_original_order加上买家支付宝账号buyer_aplipay_no */
/* t_order加上买家支付宝账号buyer_aplipay_no */
/*===================================*/
use steward;

alter table t_original_order add buyer_alipay_no varchar(128) comment '买家支付宝账号（淘宝）' after buyer_id;
alter table t_order add buyer_alipay_no varchar(128) comment '买家支付宝账号（淘宝）' after buyer_id;
alter table t_refund add buyer_alipay_no varchar(128) comment '买家支付宝账号（淘宝）' default null ;

alter table t_product add outer_product_no varchar(32) comment '外部平台产品编码' default null ;