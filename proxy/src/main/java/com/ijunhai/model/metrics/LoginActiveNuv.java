package com.ijunhai.model.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ijunhai.dao.DaoType;
import com.ijunhai.util.PropertiesUtils;

import static com.ijunhai.constant.ProxyConstants.*;

public class LoginActiveNuv implements Metric {
    public static String NAME = "active_nuv";

    @JsonIgnore
    public String getFuction(DaoType daoType) {
        switch (daoType) {
            case MYSQL:
                return " sum(new_account) as active_nuv_m ";
            case KYLIN:
                return "count(distinct \"USER_ID\") as active_nuv";
            case GP:
                return "sum(active_nuv) as active_nuv";
            default:
                return null;
        }
    }

    @JsonIgnore
    public String getConditions(DaoType daoType) {
        return " reg_date = server_date_day ";
    }

    @JsonIgnore
    public String getTableName(DaoType daoType) {
        switch (daoType) {
            case MYSQL:
                return MYSQL_TABLENAME;
            case KYLIN:
                return PropertiesUtils.get(KYLIN_LOGIN_TABLENAME);
            case WEEK_GP:
                return PropertiesUtils.get(GP_WEEK_TABLENAME);
            case MONTH_GP:
                return PropertiesUtils.get(GP_MONTH_TABLENAME);
            default:
                return null;
        }
    }

    @JsonIgnore
    public String getName() {
        return NAME;
    }
}
