package com.ijunhai.model.metrics;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ijunhai.dao.DaoType;

import java.util.List;







@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,property = "name")
@JsonSubTypes(value={
        @JsonSubTypes.Type(name="active_uv",value=LoginActiveUv.class),
        @JsonSubTypes.Type(name="active_nuv",value=LoginActiveNuv.class),
        @JsonSubTypes.Type(name="active_ouv",value=LoginActiveOuv.class),
        @JsonSubTypes.Type(name="pay_uv",value=OrderPayUv.class),
        @JsonSubTypes.Type(name="pay_nuv",value=OrderPayNuv.class),
        @JsonSubTypes.Type(name="pay_ouv",value=OrderPayOuv.class),
        @JsonSubTypes.Type(name="pay_amount",value=OrderPayAmount.class),
        @JsonSubTypes.Type(name="nu_pay_amount",value=OrderNuPayAmount.class),
        @JsonSubTypes.Type(name="ou_pay_amount",value=OrderOuPayAmount.class),
        @JsonSubTypes.Type(name="retention_uv",value=LoginRetentionUv.class),
        @JsonSubTypes.Type(name="first_pay_uv",value=OrderFirstPayUv.class),
        @JsonSubTypes.Type(name="first_pay_amount",value=OrderFirstPayAmount.class),
        @JsonSubTypes.Type(name="yet_pay_nuv",value=OrderYetPayNuv.class),
        @JsonSubTypes.Type(name="nu_yet_pay_amount",value=OrderNuYetPayAmount.class),
        @JsonSubTypes.Type(name="first_pay_retention_uv",value=LoginFirstPayRetentionUv.class),
        @JsonSubTypes.Type(name="first_pay_retention_nuv",value=LoginFirstPayRetentionNuv.class),
        @JsonSubTypes.Type(name="complex_nu_yet_pay_amount",value=ComplexNuYetPayAmount.class),
        @JsonSubTypes.Type(name="complex_yet_pay_nuv",value=ComplexYetPayNuv.class),
        @JsonSubTypes.Type(name="complex_retention_uv",value=ComplxRetentionUv.class),
        @JsonSubTypes.Type(name="complex_first_pay_retention_uv",value=ComplxFirstPayRetentionUv.class),
        @JsonSubTypes.Type(name="complex_first_pay_retention_nuv",value=ComplxFirstPayRetentionNuv.class),

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
