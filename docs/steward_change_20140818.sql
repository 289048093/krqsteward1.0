
/*===================================*/
/* 修正TAO_BAO平台的所有订单优惠，及其订单项优惠金额*/
/*===================================*/
use steward;

alter table t_original_order add all_discount_fee bigint(20) default 0 comment '订单所有的优惠金额' after discount_fee;

alter table t_repository add column custid varchar(255) comment '月结帐号';