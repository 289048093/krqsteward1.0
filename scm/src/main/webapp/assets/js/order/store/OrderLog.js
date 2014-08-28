/*
 * Created by king on 13-12-19
 */

Ext.define('Supplier.store.OrderLog', {
    //不要忘了继承
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
});