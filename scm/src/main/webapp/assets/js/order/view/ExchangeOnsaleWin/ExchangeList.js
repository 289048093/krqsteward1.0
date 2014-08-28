/**
 * author     : 梦龙
 * createTime : 14-5-9 上午10:10
 * description:
 */



Ext.define('Supplier.view.ExchangeOnsaleWin.ExchangeList', {
    extend: 'Ext.grid.Panel',
    region: 'north',
    alias: 'widget.exchangeList',
    id: 'ExchangeAddList',
    height: 180,
    split: true,
    forceFit: false,
    store: 'ItemList',
    selModel: {
        selType : 'checkboxmodel',//复选框选择模式Ext.selection.CheckboxModel
        mode: 'SINGLE',
        showHeaderCheckbox: true
    },
    viewConfig: Espide.Common.getEmptyText(),
    initComponent: function () {
//        this.tbar = [
//            Ext.create('Ext.form.Panel', {
//                layout: 'hbox',
//                border: false,
//                itemId: 'orderSearchForm',
//                id: 'orderSearchForm',
//                items: [
//
//                    {
//                        xtype: 'combo',
//                        hideLabel: true,
//                        name: 'conditionQuery',
//                        width: 120,
//                        queryMode: 'local',
//                        triggerAction: 'all',
//                        forceSelection: true,
//                        editable: false,
//                        value: 'platformOrderNo',
//                        store: [
//                            ['platformOrderNo', '外部平台订单编号'],
//                            ['orderNo', '智库城订单编号']
//                        ],
//                        margin: '0 5 0 0'
//                    },
//                    {
//                        xtype: 'textfield',
//                        emptyText: '请输入关键字',
//                        itemId: 'keyword',
//                        id: 'keyword',
//                        allowBlank: false,
//                        name: 'conditionValue',
//                        width: 120,
//                        margin: '0 5 0 0'
//                    },
//                    {
//                        xtype: 'button',
//                        text: '搜索',
//                        margin: '0 5 0 0',
//                        itemId: 'ChangeSearchBtn'
//                    }
//                ]
//            }),
//
//        ];
        this.columns = [


            { text: '商品id', dataIndex: 'id', width: 55, hidden: true},
            { text: '智库城订单编号', dataIndex: 'id', width: 170},
            { text: '外部平台订单项编号', dataIndex: 'platformSubOrderNo', width: 170},
            { text: '订单项类型', dataIndex: 'type', width: 90,},
            { text: '订单项状态', dataIndex: 'status', width: 90,scope: Espide.Common.orderItemStatus,renderer: Espide.Common.orderState.getData},
            { text: '线上退货状态', dataIndex: 'returnStatus', width: 170,
                renderer:function(value){
                    return value['value'];
                }
            },
            { text: '线下退货状态', dataIndex: 'offlineReturnStatus', width: 170,
                renderer:function(value){
                    return value['value'];
                }
            },
            { text: '是否已换货', dataIndex: 'exchanged', width: 90,renderer:function(value){
                if(value){return '已换货'}else{return '未换货'}}
            },
            { text: '换货状态', dataIndex: 'exchangedGoods', width: 90},
            { text: '商品编号', dataIndex: 'productCode', width: 90},
            { text: '商品名称', dataIndex: 'productName', width: 120, editor: {xtype: 'textfield', allowBlank: true}},
            { text: 'sku', dataIndex: 'productSku', width: 120},
            { text: '品牌', dataIndex: 'brandName', width: 100},
            { text: '类别', dataIndex: 'categoryName', width: 90},
            { text: '原价（一口价）', dataIndex: 'price', width: 170},
            { text: '促销价', dataIndex: 'discountPrice', width: 90},
            { text: '订货数量', dataIndex: 'buyCount', width: 90},
            { text: '库存', dataIndex: 'repoNum', width: 90},
            { text: '成交金额', dataIndex: 'actualFee', width: 90},
            { text: '货款', dataIndex: 'goodsFee', width: 90},
            { text: '价格描述', dataIndex: 'priceDescription', width: 180},

        ];
        this.callParent(arguments);
    }
});
