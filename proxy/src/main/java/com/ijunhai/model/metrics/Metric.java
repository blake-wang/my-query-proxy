package com.ijunhai.model.metrics;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ijunhai.dao.DaoType;

import java.util.List;

/**
 * 1:新建一个查询的指标，就要到这里来关联指标名称和类class文件，否则会报解析错误
 * Caused by: java.lang.IllegalArgumentException: Class com.ijunhai.model.metrics.LoginActiveUv is not assignable to com.ijunhai.model.metrics.Metric
 * 2:查询指标的字段，必须要来这里添加注解，否则也会报错
 * Exception in thread "main" com.fasterxml.jackson.databind.JsonMappingException: Could not resolve type id 'active_uv' into a subtype of [simple type, class com.ijunhai.model.metrics.Metric]: known type ids = [Metric, active_nuv, active_ouv, complex_first_pay_retention_nuv, complex_first_pay_retention_uv, complex_nu_yet_pay_amount, complex_retention_uv, complex_yet_pay_nuv, first_pay_amount, first_pay_retention_nuv, first_pay_retention_uv, first_pay_uv, nu_pay_amount, nu_yet_pay_amount, ou_pay_amount, pay_amount, pay_nuv, pay_ouv, pay_uv, retention_uv, yet_pay_nuv]
 *  at [Source: {"conditions":{"osType":[],"gameId":[],"start":"2018-04-25","end":"2018-05-01"},"orderByFields":["day desc"],"granularity":"day","limit":"","returnDemensions":[],"metrics":[{"name":"active_nuv"},{"name":"active_uv"},{"name":"active_ouv"},{"name":"pay_uv"},{"name":"pay_amount"},{"name":"pay_nuv"},{"name":"nu_pay_amount"},{"name":"first_pay_uv"},{"name":"first_pay_amount"}]}; line: 1, column: 197] (through reference chain: com.ijunhai.model.QueryModel["metrics"]->java.util.ArrayList[1])
 */


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "name")
@JsonSubTypes(value = {
//        @JsonSubTypes.Type(name = "active_uv", value = LoginActiveUv.class),
        @JsonSubTypes.Type(name = "active_nuv", value = LoginActiveNuv.class),
        @JsonSubTypes.Type(name = "active_ouv", value = LoginActiveOuv.class),
        @JsonSubTypes.Type(name = "pay_uv", value = OrderPayUv.class),
        @JsonSubTypes.Type(name = "pay_nuv", value = OrderPayNuv.class),
        @JsonSubTypes.Type(name = "pay_ouv", value = OrderPayOuv.class),
        @JsonSubTypes.Type(name = "pay_amount", value = OrderPayAmount.class),
        @JsonSubTypes.Type(name = "nu_pay_amount", value = OrderNuPayAmount.class),
        @JsonSubTypes.Type(name = "ou_pay_amount", value = OrderOuPayAmount.class),
        @JsonSubTypes.Type(name = "retention_uv", value = LoginRetentionUv.class),
        @JsonSubTypes.Type(name = "first_pay_uv", value = OrderFirstPayUv.class),
        @JsonSubTypes.Type(name = "first_pay_amount", value = OrderFirstPayAmount.class),
        @JsonSubTypes.Type(name = "yet_pay_nuv", value = OrderYetPayNuv.class),
        @JsonSubTypes.Type(name = "nu_yet_pay_amount", value = OrderNuYetPayAmount.class),
        @JsonSubTypes.Type(name = "first_pay_retention_uv", value = LoginFirstPayRetentionUv.class),
        @JsonSubTypes.Type(name = "first_pay_retention_nuv", value = LoginFirstPayRetentionNuv.class),
        @JsonSubTypes.Type(name = "complex_nu_yet_pay_amount", value = ComplexNuYetPayAmount.class),
        @JsonSubTypes.Type(name = "complex_yet_pay_nuv", value = ComplexYetPayNuv.class),
        @JsonSubTypes.Type(name = "complex_retention_uv", value = ComplxRetentionUv.class),
        @JsonSubTypes.Type(name = "complex_first_pay_retention_uv", value = ComplxFirstPayRetentionUv.class),
        @JsonSubTypes.Type(name = "complex_first_pay_retention_nuv", value = ComplxFirstPayRetentionNuv.class),

})
public interface Metric {
    String getFuction(DaoType queryType);

    String getConditions(DaoType queryType);

    String getTableName(DaoType queryType);

    String getName();

    default String format(String value) {

        return value.split("\\.")[0];
    }

    default List<Integer> getValues() {

        return null;
    }


}
