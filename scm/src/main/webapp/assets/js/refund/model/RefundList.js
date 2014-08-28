/**
 * 日志类型列表
 */
Ext.define('Refund.model.RefundList', {
    extend: 'Ext.data.Model',
    fields: [
        'type',    //订单对象
        'online',
        'phase',
        'status',
        'originalRefund',
        'platformType',
        'nick',
        'platformSubOrderNo',
        'id',
        'name',
        'sku',
        'name',
        'shop',
        'buyerName',
        'platformRefundNo',
        {name:'shopName',type:'string',mapping:'shop.nick'},
        'orderItem',
        {name:'sku',type:'string',mapping:'orderItem.product.sku'},
        {name:'platformOrderNo',type:'string',mapping:'orderItem.platformOrderNo'},
        {name:'productNo',type:'string',mapping:'orderItem.product.productNo'},
        {name:'productName',type:'string',mapping:'orderItem.product.name'},
        'orderItemVo',
        {name:'brandName',type:'string',mapping:'orderItemVo.brandName'},
        {name:'outerSku',type:'string',mapping:'orderItem.outerSku'},
        'refundFee',
        'actualRefundFee',
        'receiverMobile',
        'receiverPhone',
        'buyerId',
        'reason',
        'description',
        'buyerAlipayNo',
        'remark',
        'alsoReturn',
        'shippingNo',
        'shippingComp',
        'postFee',
        'postPayer',
        {name: 'createTime', type: 'date', dateFormat: 'time'},
        {name: 'refundTime', type: 'date', dateFormat: 'time'},
        {name: 'revisitTime', type: 'date', dateFormat: 'time'},
    ],
    idProperty:'id'
});