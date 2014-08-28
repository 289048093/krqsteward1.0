
/*===================================*/
/* t_invoice的部分字段可以为null      */
/*===================================*/
use steward;

alter table t_order_item add spec_info varchar(256) comment '颜色规格尺寸重量,分号分隔';


drop table if exists t_gift_brand;
drop table if exists t_gift_prod;
drop table if exists t_gift_brand_item;

drop table if exists t_activity;

/*==============================================================*/
/* Table: t_activity                                            */
/*==============================================================*/
create table t_activity
(
  id                   int(11) not null auto_increment,
  type                 varchar(20) default null comment 'PRODUCT,BRAND',
  product_id           int(11) default null comment '产品id',
  brand_id             int(11) default null comment '品牌id',
  actual_fee_begin     bigint(20) default null comment '起始价格',
  actual_fee_end       bigint(20) default null comment '结束价格',
  in_use               tinyint(1) default 1 comment '是否启用(0是表示未启用, 1表示已启用)默认是已启用1',
  remark               varchar(255),
  create_time          datetime default '0000-00-00 00:00:00',
  update_time          datetime default '0000-00-00 00:00:00',
  operator_id          int(11) default null,
  primary key (id)
);


drop table if exists t_activity_item;

/*==============================================================*/
/* Table: t_activity_item                                       */
/*==============================================================*/
create table t_activity_item
(
  id                   int(11) not null auto_increment,
  activity_id          int(11) not null comment '优惠活动id',
  product_id           int(11) not null comment '赠送商品id',
  amount               int(11) comment '赠送数量',
  primary key (id)
);
