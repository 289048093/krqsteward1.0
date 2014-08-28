package com.ejushang.steward.ordercenter.constant;

/**
 * User: Baron.Zhang
 * Date: 2014/8/18
 * Time: 11:55
 */
public class SfConstant {

    public static final String SF_REQ_HEAD = "JMSJLSYYXGS,9W7ZwEDdYQklHa3s";
    public static final String SF_LANG = "zh-CN";
    public static final String SF_ORDER_SERVICE = "OrderService";
    public static final String SF_ORDER_SEARCH_SERVICE = "OrderSearchService";
    public static final String PARCEL_QUANTITY = "1";
    // 月结卡号
    public static final String CUST_ID = "7501638868";




    public static enum PAY_METHOD{
        JI_FANG_FU("1"),
        SHOU_FANG_FU("2"),
        DI_SAN_FANG_FU("3")
        ;
        private PAY_METHOD(String value){
            this.value = value;
        }
        private String value;

        public String getValue() {
            return value;
        }
    }


    public static enum SF_EXPRESS_TYPE{
        BIAO_ZHUN_KUAI_DI("1"),
        SHUN_FENG_TE_HUI("2"),
        DIAN_SHANG_TE_HUI("3"),
        DIAN_SHANG_SU_PEI("7")
        ;
        private SF_EXPRESS_TYPE(String value){
            this.value = value;
        }
        private String value;

        public String getValue() {
            return value;
        }
    }


}
