/*
 * Created by king on 13-12-17
 */

Ext.define('Supplier.view.OrderLog', {
    extend: 'Ext.grid.Panel',
    region: 'center',
    id: 'OrderLog',
    itemId: 'OrderLog',
    alias: 'widget.OrderLog',
    store: Ext.create('Ext.data.Store', {
        extend: 'Ext.data.Store',
        //记得设置model
        fields: [
            'log'
        ],
        //自动加载设为true
        autoLoad: false,
        proxy: {
            type: 'ajax',
            url: 'order/approveLogs',
            reader: {
                type: 'json',
                root: 'data.obj.orderHandleLogs'
            },
        },
        pageSize: 30000
    }),
    height:'auto',
    autoScroll :true,
    forceFit: true,
    split: true,
    viewConfig: {
        enableTextSelection: true,
        forceFit: true
    },
    initComponent: function () {
        this.columns = [
            {text: '订单详细日志', dataIndex: 'log',width:500},
        ];
        this.callParent(arguments);
    }
})