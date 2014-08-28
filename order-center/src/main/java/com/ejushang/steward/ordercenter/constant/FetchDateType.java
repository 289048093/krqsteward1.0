package com.ejushang.steward.ordercenter.constant;

import com.ejushang.steward.common.domain.util.ViewEnum;

/**
 * User: Baron.Zhang
 * Date: 2014/5/28
 * Time: 16:47
 */
public enum FetchDateType implements ViewEnum{
    FETCH_BY_CREATED("创建日期"),
    FETCH_BY_MODIFIED("更新日期")
    ;

    private String name;
    private String value;

    FetchDateType(String value){
        this.value = value;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
