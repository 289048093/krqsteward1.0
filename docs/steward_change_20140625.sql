
/*===================================*/
/* t_invoice的部分字段可以为null      */
/*===================================*/
use steward;

alter table t_invoice modify receiver_name varchar(128) null;
alter table t_invoice modify receiver_state varchar(64) null;
alter table t_invoice modify receiver_city varchar(64) null;
alter table t_invoice modify receiver_address varchar(256) null;
