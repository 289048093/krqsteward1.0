/*
 * Created by king on 13-12-19
 */

Ext.define('Supplier.store.OrderList', {
    //不要忘了继承
    extend: 'Ext.data.Store',
    //记得设置model
    //model: 'Supplier.model.OrderList',
    fields: [
        {name: 'id', type: 'int'},
        'generateType',
        'orderType',
        'orderStatus',
        'orderReturnStatus',
        'orderNo',
        'outPlatformType',
        {name:'platformType',type:'string',mapping:'outPlatformType.name'},
        'shopName',
        'shopId',
        'platformOrderNo',
        {name:'outActualFee',type:'float'},
        {name:'actualFee',type:'float'},
        {name:'sharedDiscountFee',type:'float'},
        {name:'postFee',type:'float'},
        {name:'goodsFee',type:'float'},
        'itemName',
        {name:'itemCount',type:'float'},
        {name:'itemNumCount',type:'float'},
        'specInfo',
        'orderItemIds',
        'hasPostFee',
        'refunding',
        'refunding',
        'buyerMessage',
        'remark',
        'offlineRemark',
        'buyerId',
        'buyTime',
        'payTime',
        'repoName',
        'receiverState',
        'receiverCity',
        'receiptTitle',
        'receiptContent',
        'receiverDistrict',
        'receiverAddress',
        'receiverName',
        'buyerAlipayNo',
        'receiverZip',
        //'receiverPhone',
        {name:'receiverPhone',type:'string',defaultValue:''},
        {name:'receiverMobile',type:'string',defaultValue:''},
        //'receiverMobile',
        'shippingComp',
        'shippingNo',
        'orderApproves',
        'orderHandleLogs',
    ],
    //自动加载设为true
    autoSync: true,
    autoLoad: true,
    remoteSort: false,
    proxy: {
        type: 'ajax',
        noCache:false,
       // limitParam: 'limitNum',
        api: {
            read: 'order/list',
            create: '/assets/js/order/data/orderList.json',
            update: '/order/updateByOrder',
            destroy: '/assets/js/order/data/orderList.json'
        },
        actionMethods:{
           read: "POST",
        },
        reader: {
            type: 'json',
            successProperty: 'success',
            root: 'data.obj.result',
            messageProperty: 'msg',
            goodFees:'data.obj.goodFees',
            totalProperty: 'data.obj.totalCount'
        },
        writer: {
            type: 'json',
            encode: true,
            writeAllFields: false,
            //root: 'data.obj.result'
            root: 'data'
        },
        listeners: {
            exception: function (proxy, response, operation) {
                var data = Ext.decode(response.responseText);
                Ext.MessageBox.show({
                    title: '警告',
                    msg: data.msg,
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.Msg.OK
                });
            }
        }
    },
    //sorters:[{property:"orderStatus",direction:"ASC", root: 'data.obj.result',}],
    listeners: {
        write: function (proxy, operation) {
            var com = Espide.Common,
                data = Ext.decode(operation.response.responseText);
            if (data.success) {

                com.tipMsg('操作成功', '订单修改成功');
                com.reLoadGird('OrderList', 'search', false);

            } else {
                Ext.Msg.show({
                    title: '错误',
                    msg: data.msg,
                    buttons: Ext.Msg.YES,
                    icon: Ext.Msg.WARNING
                });
            }
        },
        beforeLoad:function(){
            Ext.apply(this.proxy.extraParams, {limit:Ext.getCmp('limit').getValue()});
        },
        load: function (store, records, successful, eOpts) {

            //显示总货款
            //Ext.getCmp('OrderList').down("#goodsFee").setValue(this.proxy.reader.jsonData.data.obj.goodsFee)

            if (store.getTotalCount()>=0){
                Ext.getCmp('OrderList').down("#orderConut").setValue(store.getTotalCount());
            }
        }
    },
    pageSize: 30
});