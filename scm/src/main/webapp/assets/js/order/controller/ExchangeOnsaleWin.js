/**
 * Created by king on 13-12-23
 */

Ext.define('Supplier.controller.ExchangeOnsaleWin', {
        extend: 'Ext.app.Controller',
        views: [

            'ExchangeOnsaleWin.ExchangeOnsaleWin',
            //'ExchangeOnsaleWin.ExchangeInfo',
            'ExchangeOnsaleWin.ExchangeList',
            'ExchangeOnsaleWin.ExchangeItem',
            'ExchangeOnsaleWin.ExchangeOnsaleCart',
        ],
        stores: ['ExchangeWin.GoodCart', 'GoodList'],
        models: ['ExchangeWin.GoodCart', 'GoodList'],
        init: function () {
            this.control({
                //线上换货搜索产品
                '#ExchangeSearchBtn': {
                    click: function (btn) {
                        var params = btn.up('form').getValues();
                        btn.up('grid').getStore().load({
                            params: params
                        });
                    }
                },

                //售前换货提交
                '#Exchangesubmit': {
                    click: function (btn) {
                        var ExchangeAddListSlc = Ext.getCmp('ExchangeAddList').getSelectionModel().getSelection(),
                            com = Espide.Common,
                            params = {},
                            goodCartStore = Ext.getCmp('ExchangeOnsaleCart').getStore();
                        //判断是否选择了数据
                        if (!Espide.Common.checkGridSel('ExchangeAddList', '请选择一条售前更换的订单项数据！')) {
                            return false;
                        }

                        if (goodCartStore.count() === 0) {
                            Ext.Msg.alert('警告', '请至少加入一个售前更换的商品!');
                            return;
                        }

                        //转换数值
                        var values = [];
                        goodCartStore.each(function (item, index, count) {
                            values[index] = {};
                            values[index].productId = item.get('productId') + '';
                            values[index].count = item.get('count');
                        });

                        //售前换货提交数据
                        Ext.Ajax.request({
                            params: {
                                oldOrderItemId: ExchangeAddListSlc[0].data.id,
                                exchangeDetails: JSON.stringify(values)
                            },
                            url: "order/exchangeGood",
                            success: function (response, options) {
                                var data = Ext.JSON.decode(response.responseText);
                                if (data.success) {
                                    Espide.Common.tipMsgIsCloseWindow(data, Ext.getCmp('ExchangeOnsaleWin'), 'OrderList', true);
                                }
                            },
                            failure: function (form, action) {
                                var data = Ext.JSON.decode(action.response.responseText);
                                Ext.MessageBox.show({
                                    title: '提示',
                                    msg: data.msg,
                                    buttons: Ext.MessageBox.OK,
                                    icon: 'x-message-box-warning'
                                });
                            }
                        });

                    }

                },

                '#ExchangeItem #addRow': {
                    //多个添加到商品车
                    click: this.readyAddGood
                },

                'ExchangeOnsaleCart #removeBtn': {
                    click: this.removeGoodCart  //移除购物车中商品
                },


            });
        },
        //准备添加到商品车
        readyAddGood: function (button, rowIndex, colIndex, item, e, selected) {
            console.log(1);
            var root = this,
                selectgoods = [],
                newgoods = null;

            //分情况获取所选项数组
            if (button.getXType() == 'button') {
                selectgoods = button.up('grid').getSelectionModel().getSelection();
            } else {
                selectgoods.push(selected);
            }

            //是否有加入项，有，则返回可加入数组
            newgoods = root.canSelGoodAdd(selectgoods);

            if (!newgoods) return false;

            //条件校检好后真正加入购物车
            root.addToGoodCart(newgoods);
        },

        //过滤所选项，判断是否符合加入条件，符合返回可加入的数组，否则返回false
        canSelGoodAdd: function (sels) {
            var root = this,
                flag = true,
                arr = [],
                addNum = Ext.getCmp('ExchangeItem').down('#addNum').getValue() || 1;


            Ext.each(sels, function (record, index, records) {
                if (root.isGoodAdded(record.get('sku'))) {
                    Ext.Msg.alert('警告', '暂存仓已有选中商品，请先移除已选再添加');
                    return flag = false;
                }

                console.log(record);
                var newgood = Ext.create('Supplier.model.ExchangeWin.GoodCart', {
                    num: addNum,
                    autoId: null,
                    productNo: record.get('productNo'),
                    productId: record.get('id'),
                    itemType: record.get('itemType'),
                    brandName: record.get('brandName'),
                    prodCategoryName: record.get('prodCategoryName'),
                    sku: record.get('sku'),
                    repositoryNum: record.get('repositoryNum'),
                    marketPrice: 0,
                    discountPrice: 0,
                    //repositoryNum: addNum*record.get('prodPrice'),
                    name: record.get('name')
                });


                arr.push(newgood);
            });
            if (!flag || arr.length === 0) {
                return false;
            }
            return arr;
        },
        //判断购物车是否已有要添加的商品
        isGoodAdded: function (goodId) {
            var flag = false,
                goodCartItems = Ext.getCmp('ExchangeOnsaleCart').getStore().data.items;
            Ext.each(goodCartItems, function (record, index, root) {
                if (record.get('sku') == goodId) {
                    return flag = true;
                }
            });
            return flag;
        },
        //过滤完后，真正加入购物车
        addToGoodCart: function (goods) {
            Ext.getCmp('ExchangeOnsaleCart').getStore().add(goods);
        },
        //移除购物车中商品
        removeGoodCart: function (button) {
            var goodCart = button.up('window').down("#ExchangeOnsaleCart"),
                records = goodCart.getSelectionModel().getSelection();
            if (records.length > 0) {
                goodCart.getStore().remove(records);
            }
        }
    }
);