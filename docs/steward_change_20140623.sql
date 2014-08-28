
/*===================================*/
/* t_original_order的部分字段可以为null      */
/*===================================*/
use steward;

alter table t_original_order modify receiver_city varchar(64) null;
alter table t_original_order modify payable_fee bigint(20) null;
alter table t_original_order modify buyer_id varchar(128) null;
alter table t_original_order modify receiver_name varchar(128) null;
alter table t_original_order modify receiver_state varchar(64) null;
alter table t_original_order modify receiver_address varchar(256) null;
alter table t_original_order modify platform_type varchar(10) null;
alter table t_original_order modify platform_order_no varchar(100) null;
alter table t_original_order modify shop_id bigint(11) null;
