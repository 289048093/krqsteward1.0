
/*===================================*/
/* 新增错误记录表,记录确认发货的错误     */
/*===================================*/
use steward;

drop table if exists t_error_info;

/*==============================================================*/
/* Table: t_error_info                                          */
/*==============================================================*/
create table t_error_info
(
  id                   int(11) not null auto_increment,
  title                varchar(512) default null,
  detail               text,
  create_time          datetime default '0000-00-00 00:00:00',
  extra_info_one       varchar(256) default null,
  extra_info_two       varchar(256) default null,
  extra_info_three     varchar(256) default null,
  primary key (id)
);


