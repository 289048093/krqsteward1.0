
/*===================================*/
/* t_order_fetch新增fetch_data_type列 */
/* t_refund加上货运单号和物流公司       */
/*===================================*/
use steward;

alter table t_order_fetch add fetch_data_type varchar(30) default 'FETCH_ORDER';

alter table t_refund add shipping_no varchar(128) default null;

alter table t_refund add shipping_comp varchar(64) default null;

alter table t_refund add buyer_alipay_no varchar(128) comment '买家支付宝账号（淘宝）' default null ;