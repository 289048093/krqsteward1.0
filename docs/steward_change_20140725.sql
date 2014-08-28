
/*===================================*/
/*  售前换货功能调整加上对应的表 */
/*===================================*/
use steward;

alter table t_order_item add exchange_order_item_id int(11) comment '售前换货原来的订单项信息ID';

drop table if exists t_exchange_order_item;

/*==============================================================*/
/* Table: t_exchange_order_item                                 */
/*==============================================================*/
create table t_exchange_order_item
(
  id                   int(11) not null auto_increment,
  product_id           int(11) not null comment '商品id',
  product_code         varchar(32) comment '商品编码',
  product_sku          varchar(32) not null comment '商品条形码',
  product_name         varchar(32) comment '商品名称',
  buy_count            int(11) comment '购买数量',
  spec_info            varchar(256) comment '颜色规格尺寸重量,分号分隔',
  create_time          datetime default '0000-00-00 00:00:00' comment '创建时间',
  update_time          datetime default '0000-00-00 00:00:00' comment '更新时间',
  operator_id          int(11),
  primary key (id)
);

delete from t_order_item where id = 33543;
insert into t_exchange_order_item values(null, 4019, 'DK013', '4895161311654', '戴德朗尼系列500ML运动保温瓶', 1, '不锈钢色;一件套;0.28kg;0.28kg', now(), now(), null);
update t_order_item set product_id=4020, product_code='DK014', product_sku='4895161311760', product_name='戴德朗尼系列750ML运动保温瓶' where id = 33210;