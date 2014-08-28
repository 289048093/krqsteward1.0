
/*===================================*/
/* t_original_order 新增：all_discount_fee 订单所有的优惠金额 */
/* t_original_order_item 新增：all_part_mjz_discount 包含所有优惠的优惠分摊 */
/* t_promotion_info 新增：platform_order_no 交易或订单项id */
/*===================================*/
use steward;

alter table t_original_order add all_discount_fee bigint(20) default 0 comment '订单所有的优惠金额' after discount_fee;
alter table t_original_order_item add all_part_mjz_discount bigint(20) default 0  comment '包含所有优惠的优惠分摊' after part_mjz_discount;
alter table t_promotion_info add platform_order_no varchar(50) comment '交易或订单项id' after original_order_id;
