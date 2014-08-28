
/*===================================*/
/* 新增错误记录表,记录确认发货的错误     */
/*===================================*/
use steward;

drop table if exists t_original_order_brand;

/*==============================================================*/
/* Table: t_original_order_brand                                */
/*==============================================================*/
create table t_original_order_brand
(
  id                   int(11) not null auto_increment,
  original_order_item_id int(11) not null,
  brand_id             int(11) not null,
  original_order_id    int(11) not null,
  primary key (id)
);


drop table if exists t_order_analyze_log;

/*==============================================================*/
/* Table: t_order_analyze_log                                   */
/*==============================================================*/
create table t_order_analyze_log
(
  id                   int(11) not null auto_increment,
  processed            tinyint(1) default null comment '处理结果',
  message              varchar(256) not null comment '处理结果描述',
  create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
  original_order_id    int(11) not null,
  primary key (id)
);


alter table t_original_order add column discard tinyint(1) default 0 comment '是否被废弃';

drop table if exists t_activity_shop;

/*==============================================================*/
/* Table: t_activity_shop                                       */
/*==============================================================*/
create table t_activity_shop
(
   id                   int(11) not null auto_increment,
   activity_id          int(11),
   shop_id              int(11),
   create_time          datetime,
   operator_id          int(11),
   primary key (id)
);