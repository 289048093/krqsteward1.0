/**
 * author     : 梦龙
 * createTime : 14-5-9 下午1:18
 * description:
 */
/**
 * Created by king on 13-12-23
 */

Ext.define('Supplier.view.ExchangeOnsaleWin.ExchangeOnsaleWin', {
    extend: 'Ext.window.Window',
    alias: 'widget.exchangeOnsale',
    title: '售前换货',
    collapsible: true,
    closeAction: 'destroy',
    id: 'ExchangeOnsaleWin',
    constrain:true,
    autoShow: false,
    modal: true,
    layout: 'border',
    animateTarget:Ext.getBody(),
    width: 1200,
    height: 650,
    initComponent: function () {
        Ext.ClassManager.setAlias('Ext.selection.CheckboxModel','selection.checkboxmodel');
        this.items = [
            //{xtype: 'ExchangeInfo'},
            {xtype: 'exchangeList'},
            {xtype: 'ExchangeItem'},
            {xtype: 'ExchangeOnsaleCart'},
        ];
        this.buttons = {
            layout: {
                pack: 'center'
            },
            items: [
                {text: '确定', itemId: 'Exchangesubmit'},
                {text: '取消', itemId: 'cancel'}
            ]
        };
        this.callParent(arguments);
    }
})