
/*===================================*/
/* 修正TAO_BAO平台的所有订单优惠，及其订单项优惠金额*/
/*===================================*/
use steward;

update `steward`.`t_original_order`
set all_discount_fee=discount_fee

where
platform_type='TAO_BAO';

update `steward`.`t_original_order_item`
set all_part_mjz_discount=part_mjz_discount
where original_order_id
in
(select id from `steward`.`t_original_order` where platform_type='TAO_BAO');

