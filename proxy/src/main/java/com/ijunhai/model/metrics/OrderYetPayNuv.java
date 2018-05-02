package com.ijunhai.model.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ijunhai.dao.DaoType;

import java.time.LocalDate;
import java.util.List;

public class OrderYetPayNuv implements Metric {
    public static String NAME = "yet_pay_nuv";

    @JsonProperty
    private String regDate;
    @JsonIgnore
    private LocalDate regLocalDate;
    @JsonProperty
    private String yetDate;
    @JsonIgnore
    private LocalDate yetLocalDate;
    @JsonProperty
    private String i;
    @JsonProperty
    private List<Integer> values;

    public OrderYetPayNuv() {
    }

    @JsonCreator
    public OrderYetPayNuv(
            @JsonProperty("regDate") String regDate,
            @JsonProperty("yetDate") String yetDate,
            @JsonProperty("day") Integer i
    ) {
        this.regDate = regDate;
        this.yetDate = yetDate;
        this.regLocalDate = LocalDate.parse(regDate);
        this.yetLocalDate = LocalDate.parse(yetDate);
        this.i = i == 0 ? "" : i.toString();
    }

    @JsonCreator
    public OrderYetPayNuv(
            @JsonProperty("values") List<Integer> values
    ) {
        this.values = values;
    }


    @Override
    public String getFuction(DaoType queryType) {
        return null;
    }

    @Override
    public String getConditions(DaoType queryType) {
        return null;
    }

    @Override
    public String getTableName(DaoType queryType) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
