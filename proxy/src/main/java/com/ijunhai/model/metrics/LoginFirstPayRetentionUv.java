package com.ijunhai.model.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ijunhai.dao.DaoType;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.List;

public class LoginFirstPayRetentionUv implements Metric {
    public static String NAME = "first_pay_retention_uv";
    @JsonProperty
    private DateTime serverDate;
    @JsonProperty
    private String firstPayDate;
    @JsonIgnore
    private LocalDate firstPayLocalDate;
    @JsonProperty
    private List<Integer> values;

    public LoginFirstPayRetentionUv() {
    }

    @JsonCreator
    public LoginFirstPayRetentionUv(
            @JsonProperty("firstPayDate") String firstPayDate,
            @JsonProperty("values") List<Integer> values
    ) {
        this.firstPayDate = firstPayDate;
        this.values = values;
        this.firstPayLocalDate = LocalDate.parse(firstPayDate);
    }


    @JsonCreator
    public LoginFirstPayRetentionUv(
            @JsonProperty("values") List<Integer> values
    ) {
        this.values = values;
    }

    public LoginFirstPayRetentionUv(
            @JsonProperty("regDate") String regDate,
            @JsonProperty("serverDate") DateTime serverDate) {
        this.firstPayDate = regDate;
        this.serverDate = serverDate;

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
