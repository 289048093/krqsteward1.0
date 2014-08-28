
/*===================================*/
/* t_original_order_item加上外部平台sku（京东）outer_sku */
/* t_order_item加上外部平台sku（京东）outer_sku */
/*===================================*/
use steward;

alter table t_original_order_item add outer_sku varchar(50) comment '外部平台sku（京东）' after sku;
alter table t_order_item add outer_sku varchar(50) comment '外部平台sku（京东）' after product_sku;


insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('淘宝',50,'http://item.taobao.com/item.htm','TAO_BAO_2');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('天猫供应商',50,'http://buy.ccb.com','TM_GYS');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('建行商城',50,'http://buy.ccb.com','JIAN_HANG');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('QQ网购',50,'http://buy.ccb.com','QQ_WG');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('QQ团购',50,'http://buy.ccb.com','QQ_TG');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('微信',50,'http://buy.ccb.com','WEI_XIN');
insert into  t_platform(name,storage_percent,prod_link_prefix,type)values('微博',50,'http://buy.ccb.com','WEI_BO');

INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('天猫供应商平台','天猫供应商平台店铺','TM_GYS','VENDOR',now(),now());
INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('建行商城平台','建行商城平台店铺','JIAN_HANG','SHOP',now(),now());
INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('QQ网购平台','QQ网购平台店铺','QQ_WG','SHOP',now(),now());
INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('QQ团购平台','QQ团购平台店铺','QQ_TG','SHOP',now(),now());
INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('微信平台','微信平台店铺','WEI_XIN','SHOP',now(),now());
INSERT INTO t_shop(nick,title,platform_type,shop_type,create_time,update_time) VALUES ('微博平台','微博平台店铺','WEI_BO','SHOP',now(),now());

alter table t_logistics_printinfo add repository_id int(50) comment '所属仓库';
alter table t_logistics_printinfo add page_height int(50) comment '打印页面高度';
alter table t_logistics_printinfo add page_width int(50) comment '打印页面宽度';
