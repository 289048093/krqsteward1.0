/*!
 * Ext JS Library 4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

Ext.define('EBDesktop.App', {
    extend: 'Ext.ux.desktop.App',

//    requires: [
//        'Ext.window.MessageBox',
//        'Ext.ux.desktop.ShortcutModel',
//        'EBDesktop.Order',
//        'EBDesktop.originalOrder',
//        'EBDesktop.Shipment',
//        'EBDesktop.Stock',
//        'EBDesktop.Shop',
//        'EBDesktop.Config',
//        'EBDesktop.LogisticsQuery',
//        'EBDesktop.OrderFetch',
//        'EBDesktop.Product',
//        'EBDesktop.Contract',
//        'EBDesktop.Log',
//        'EBDesktop.StorageFlowLog',
//        'EBDesktop.Financial',
//        'EBDesktop.Payment',
//        'EBDesktop.Refund',
//        'EBDesktop.logistics.Win',
//        'EBDesktop.warehouse.Win',
//        'EBDesktop.gift.Win',
//        'EBDesktop.account.Win',
//        'EBDesktop.role.Win',
//        'EBDesktop.module.Win',
//        'EBDesktop.brand.Win',
//        'EBDesktop.supplier.Win',
//        'EBDesktop.platform.Win',
//        'EBDesktop.productCategory.Win',
//        'EBDesktop.mealset.Win',
//        'EBDesktop.Warn'
//    ],
      requires:requireJsArray,

    init: function () {
        // custom logic before getXYZ methods get called...

        this.callParent();

        // now ready...
    },

    getModules: function () {


        var getModulesNewArray = [];
        Ext.each(getModulesArray,function(ArrayItems,i){
           getModulesNewArray.push(Ext.create(ArrayItems));
        });

        return getModulesNewArray;
    },

    getDesktopConfig: function () {
        var me = this, ret = me.callParent();

        return Ext.apply(ret, {
            //cls: 'ux-desktop-black',

            contextMenuItems: [
                { text: 'Change Settings', scope: me }
            ],
            shortcuts: Ext.create('Ext.data.Store', {
                model: 'Ext.ux.desktop.ShortcutModel',
                data: moduleArray

            }),

            //wallpaper: 'assets/images/wallpapers/default.jpg',
            wallpaperStretch: false
        });

    },

    // config for the start menu
    getStartConfig: function () {
        var me = this, ret = me.callParent();

        return Ext.apply(ret, {
            title: GV.username,
            iconCls: 'user',
            height: 200,
            toolConfig: {
                width: 150,
                items: [
                    {
                        text: '修改密码',
                        iconCls: 'settings',
                        scope: me,
                        handler: function () {

                            var form = new Ext.form.FormPanel({
                                buttonAlign: 'center',
                                width: 220,
                                id: 'updatePass',
                                frame: false,
                                border: 0,
                                allowBlank: false,
                                style: 'padding: 15px 5px 0px 5px;',
                                defaultType: 'textfield',
                                fieldDefaults: {
                                    labelAlign: 'right',
                                    labelWidth: 70
                                },
                                items: [
                                    {
                                        name: 'employeeId',
                                        hidden: true,
                                        value: GV.employeeId
                                    },
                                    {
                                        fieldLabel: '原密码',
                                        inputType: 'password',
                                        emptyText: '原密码',
                                        allowBlank: false,

                                        name: 'oldPassword'
                                    },
                                    {
                                        fieldLabel: '新密码',
                                        emptyText: '6-32位密码',
                                        blankText: '新密码不能为空',
                                        allowBlank: false,
                                        inputType: 'password',
                                        minLength: 6,
                                        maxLength: 32,
                                        name: 'newPassword'
                                    },
                                    {
                                        fieldLabel: '确认新密码',
                                        emptyText: '6-32位密码',
                                        inputType: 'password',
                                        blankText: '新密码不能为空',
                                        allowBlank: false,
                                        minLength: 6,
                                        maxLength: 32,
                                        name: 'reNewPassword'
                                    }
                                ]
                            });

                            var win = new Ext.Window({
                                title: '修改密码',
                                width: 300,
                                height: 200,
                                animateTarget: Ext.getBody(),
                                colseAction: 'hide',
                                constrainHeader: true,
                                layout: 'fit',
                                items: form,
                                listeners: {
                                    'render': function (input) {
                                        var map = new Ext.util.KeyMap({
                                            target: 'updatePass',    //target可以是组建的id  加单引号
                                            binding: [
                                                {                       //绑定键盘事件
                                                    key: Ext.EventObject.ENTER,
                                                    fn: function () {
                                                        Ext.getCmp('addBtn').getEl().dom.click();
                                                    }
                                                }
                                            ]
                                        });
                                    },
                                },
                                buttons: [
                                    {
                                        text: '保存',
                                        itemId: 'addBtn',
                                        id: 'addBtn',
                                        handler: function (btn) {
                                            var updateForm = Ext.getCmp('updatePass').getForm();
                                            if (updateForm.isValid()) {
                                                var data = updateForm.getValues();
                                                if (data.newPassword === data.reNewPassword) {

                                                    Ext.Ajax.request({
                                                        params: {
                                                            employeeId: data.employeeId,
                                                            oldPassword: data.oldPassword,
                                                            newPassword: data.newPassword
                                                        },
                                                        url: "/employee/updatePassword",
                                                        success: function (response, options) {
                                                            var data = Ext.JSON.decode(response.responseText);
                                                            if (data.success) {
                                                                Ext.Msg.alert('提示', '密码更新成功', function () {
                                                                    location.href = '/logout.action';
                                                                })
                                                            } else {

                                                                Espide.Common.showGridSelErr(data.msg);
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


                                                } else {
                                                    Espide.Common.showGridSelErr('新密码与确认密码必须一致！');
                                                }
                                            }

                                        }
                                    }
                                ]


                            });

                            win.show();
                        }
                    },
                    '-',
                    {
                        text: '退出系统',
                        iconCls: 'logout',
                        scope: me,
                        handler: function () {
                            location.href = '/logout.action';
                        }
                    }
                ]
            }
        });
    },

    onLogout: function () {
        Ext.Msg.confirm('退出', '你确定要退出系统吗?');
    },

    onSettings: function () {
        var dlg = new EBDesktop.Settings({
            desktop: this.desktop
        });
        dlg.show();
    }
});
