Ext.define('Payment.store.PaymentList', {

    extend: 'Ext.data.Store',
    model: 'Payment.model.PaymentList',
    proxy: {
        type: 'ajax',
        api: {
            read: 'payment/list'
        },
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.obj.result',
            messageProperty: 'message',
            totalProperty: 'data.obj.totalCount'
        }
    },
    listeners: {
        beforeLoad:function(){
            Ext.apply(this.proxy.extraParams, Ext.getCmp('search').getForm().getValues());
        },
        exception: function (proxy, response, operation) {
            var data = Ext.decode(response.responseText);
            Ext.MessageBox.show({
                title: '警告',
                msg: data.msg,
                icon: Ext.MessageBox.ERROR,
                button: Ext.Msg.OK
            });
        }
    },
    //autoLoad: {start: 0, limit: 20},
    autoLoad: true,
    pageSize: 20

});
