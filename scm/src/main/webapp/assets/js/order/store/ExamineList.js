/*
 * Created by king on 13-12-19
 */

Ext.define('Supplier.store.ExamineList', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    fields: [
        'orderStatus',
        'operatorName',
        {name:'updateTime',type: 'date', dateFormat: 'time'}

    ],
    //自动加载设为true

    autoLoad: false,

    proxy: {
        type: 'ajax',
        url: 'order/approveLogs',
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.obj.orderApproves',
            messageProperty: 'msg'
        },

    },

    pageSize: 30000
});