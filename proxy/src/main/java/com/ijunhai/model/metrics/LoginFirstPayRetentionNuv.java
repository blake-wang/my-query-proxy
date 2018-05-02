package com.ijunhai.model.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ijunhai.dao.DaoType;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.util.List;

public class LoginFirstPayRetentionNuv implements Metric {
    public static String NAME = "first_pay_retention_nuv";


    @JsonProperty
    private String regDate;
    @JsonIgnore
    private LocalDate regLocalDate;
    @JsonProperty
    private List<Integer> values;
    @JsonProperty
    private DateTime serverDate;

    public LoginFirstPayRetentionNuv() {
    }

    @JsonCreator
    public LoginFirstPayRetentionNuv(
            @JsonProperty("regDate") String regDate,
            @JsonProperty("values") List<Integer> values
    ) {
        this.regDate = regDate;
        this.values = values;
        this.regLocalDate = LocalDate.parse(regDate);
    }

    @JsonCreator
    public LoginFirstPayRetentionNuv(
            @JsonProperty("values") List<Integer> values
    ) {
        this.values = values;
    }

    public LoginFirstPayRetentionNuv(
            @JsonProperty("regDate") String regDate,
            @JsonProperty("serverDate") DateTime serverDate) {
        this.regDate = regDate;
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
