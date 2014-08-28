

use steward;



   /*==============================================================*/
/* 添加订单字段                       */
/*==============================================================*/

alter table t_order add column offline_remark text ;

delete from t_business_log;
alter table t_business_log add column ip varchar(64) ;
alter table t_business_log add column resource_name varchar(64) ;
alter table t_business_log add column execution_time int(11) ;
alter table t_business_log add column operation_result boolean;
alter table t_business_log add column operation_exception varchar(512) ;
alter table t_business_log add column description text;
