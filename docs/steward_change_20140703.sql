
/*===================================*/
/* t_order_fetch新增字段     */
/*===================================*/
use steward;

alter table t_order_fetch add fetch_opt_type varchar(30) default 'AUTO' comment '抓取数据操作类型：手动\自动' after fetch_data_type;
