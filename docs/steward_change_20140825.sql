/*===================================*/
/* invoice添加三个字段*/
/*===================================*/
use steward;

alter table t_invoice add sf_express_type varchar(11)  comment '顺丰类型';
alter table t_invoice add dest_code varchar(11)  comment '目的地编码';
alter table t_invoice add origin_code varchar(11)  comment '寄送地编码';

