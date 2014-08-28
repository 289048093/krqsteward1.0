package com.ejushang.steward.ordercenter.util;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/8/18
 * Time: 16:21
 */
public class SfAddressUtil {

    /**
     * 省份集合
     */
    public static List<String> provinceList = new ArrayList<String>(){
        {
            add("河北");
            add("山西");
            add("辽宁");
            add("吉林");
            add("黑龙江");
            add("江苏");
            add("浙江");
            add("安徽");
            add("福建");
            add("江西");
            add("山东");
            add("河南");
            add("湖北");
            add("湖南");
            add("广东");
            add("海南");
            add("重庆");
            add("四川");
            add("贵州");
            add("云南");
            add("陕西");
            add("甘肃");
            add("青海");
        }
    };

    /**
     * 直辖市集合
     */
    public static List<String> mdugcList = new ArrayList<String>(){
        {
            add("北京");
            add("天津");
            add("上海");
            add("重庆");
        }
    };

    /**
     * 自治区集合
     */
    public static List<String> autoRegionList = new ArrayList<String>(){
        {
            add("新疆");
            add("内蒙古");
            add("宁夏");
            add("广西");
            add("西藏");
        }
    };

}
