/**
 * Created by king on 13-12-23
 */

Ext.define('Supplier.view.ExchangeOnsaleWin.ExchangeOnsaleCart', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.ExchangeOnsaleCart',
    region: 'south',
    height: 180,
    id: 'ExchangeOnsaleCart',
    forceFit: false,
    split: true,
   store: 'ExchangeWin.GoodCart',
    selType: 'checkboxmodel',
    viewConfig: Espide.Common.getEmptyText(),
    initComponent: function () {
        this.plugins = [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
            })
        ];
        this.columns = [
//            {text: '自增号', dataIndex: 'autoId', hidden: true},
            {text: '品牌Id', dataIndex: 'brandId',hidden:true},
            {text: '仓库名称', dataIndex: 'repositoryName',hidden:true},
            {text: '仓库Id', dataIndex: 'repositoryId',hidden:true},
            {text: '产品分类Id', dataIndex: 'prodCategoryId',hidden:true},
            {text: '订单项类型', dataIndex: 'orderItemType',
                scope: Espide.Common.orderItemType, renderer: Espide.Common.orderItemType.getData

            },
            {text: '商品编号', dataIndex: 'productNo',width:120},
            {text: '商品名称', dataIndex: 'name',width:150},
            {text: 'sku', dataIndex: 'sku',width:120},
            {text: '品牌', dataIndex: 'brandName'},
            {text: '类别', dataIndex: 'prodCategoryName'},
            {text: '价格', dataIndex: 'marketPrice', xtype: 'numbercolumn', format: '0.00', width: 130},
            {text: '促销价', dataIndex: 'discountPrice', xtype: 'numbercolumn', format: '0.00'},
            {text: '订货数量', dataIndex: 'count',
                editor: {
                    xtype: 'numberfield',
                    allowDecimals: false,
                    minValue: 1
                },
                renderer: function (value) {
                    return (value);
                }
            },
            {text: '库存', dataIndex: 'repositoryNum'},
            {text: '订单项优惠', dataIndex: 'discountFee', width: 130},
            {text: '整单优惠分摊', dataIndex: 'sharedDiscountFee', width: 130},
            {text: '线下换货邮费', dataIndex: 'exchangePostFee', width: 130,},
            {
                xtype: 'actioncolumn',
                text: '操作',
                menuDisabled: true,
                width: 50,
                items: [
                    {
                        iconCls: 'icon-remove'
                    }
                ],
                handler: function (view, rowIndex, colIndex, item, e, record) {
                    view.up('grid').getStore().remove(record);
                }
            }
        ];
        this.callParent(arguments);
    }
})