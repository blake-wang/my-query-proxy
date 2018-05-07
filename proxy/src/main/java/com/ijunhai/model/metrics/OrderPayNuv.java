package com.ijunhai.model.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ijunhai.dao.DaoType;
import com.ijunhai.util.PropertiesUtils;

import static com.ijunhai.constant.ProxyConstants.*;

public class OrderPayNuv implements Metric {
    public static String NAME = "pay_nuv";

    @JsonIgnore
    public String getFuction(DaoType daoType) {
        switch (daoType) {
            default:
                return null;
            case KYLIN:
                return "count(distinct \"USER_ID\") as \"pay_nuv\" ";
            case GP:
                return "sum(pay_nuv) as \"pay_nuv\" ";
        }
    }

    @JsonIgnore
    public String getConditions(DaoType daoType) {
        StringBuilder sb = new StringBuilder();
        sb.append(" reg_date=server_date_day ");
        return sb.toString();
    }

    @JsonIgnore
    public String getTableName(DaoType daoType) {
        switch (daoType) {
            case MYSQL:
                return MYSQL_TABLENAME;
            case KYLIN:
                return PropertiesUtils.get(KYLIN_ORDER_TABLENAME);
            case GP:
                return PropertiesUtils.get(GP_ORDER_TABLENAME);
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
