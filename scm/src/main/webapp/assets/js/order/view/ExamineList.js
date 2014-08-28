/*
 * Created by king on 13-12-17
 */

Ext.define('Supplier.view.ExamineList', {
    extend: 'Ext.grid.Panel',
    region: 'center',
    id: 'ExamineList',
    itemId: 'ExamineList',
    alias: 'widget.ExamineList',
    store: Ext.create('Ext.data.Store', {
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
    }),
    forceFit: true,
    split: true,
    viewConfig: {
        enableTextSelection: true
    },
    initComponent: function () {
        this.columns = [
            {text: '订单状态', dataIndex: 'orderStatus', sortable: true,
                renderer: function (value) {
                    return value['value'];
                }
            },
            {text: '操作人', dataIndex: 'operatorName',
                renderer: function (value) {
                    if (value == null) {
                        return '系统';
                    } else {
                        return value;
                    }
                }
            },
            {text: '状态更新时间', dataIndex: 'updateTime', width: 260, sortable: true, renderer: Ext.util.Format.dateRenderer('Y-m-d H:i:s')},
        ];
        this.callParent(arguments);
    }
})