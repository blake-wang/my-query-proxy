package com.ijunhai.model;

import com.ijunhai.model.metrics.Metric;

import java.util.List;


/**
 * {
 * "conditions": {
 * "channelId": ["1231", "1232"],
 * "gameChannelId": [],
 * "gameId": ["124", "342432"],
 * "osType": ["IOS"],
 * "start": "2017-09-02 21:03:01",
 * "end": "2017-09-03 21:03:01"
 * },
 * "metrics": [
 * {"name": "active_uv"},
 * {"name": "active_nuv"},
 * {"name": "active_ouv"},
 * {"name": "retention_uv", "regDate": "2017-09-01", "values": [2, 3, 7]},
 * {"name": "complex_retention_uv", "values": [2, 3, 7]},
 * {"name": "complex_yet_pay_nuv"}
 * ],
 * "granularity": "day",
 * "returnDemensions": ["ChannelId", "GameId"]，
 * "orderByFields":["ChannelId","active_uv"]，
 * "limit":"10"
 * }
 */

public class QueryModel {
    private List<Metric> metrics;
    private Condition conditions;
    private String granularity;
    private List<String> returnDemensions;
    private List<String> orderByFields;
    private String limit;

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public List<String> getOrderByFields() {
        return orderByFields;
    }

    public void setOrderByFields(List<String> orderByFields) {
        this.orderByFields = orderByFields;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public Condition getConditions() {
        return conditions;
    }

    public void setConditions(Condition conditions) {
        this.conditions = conditions;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public List<String> getReturnDemensions() {
        return returnDemensions;
    }

    public void setReturnDemensions(List<String> returnDemensions) {
        this.returnDemensions = returnDemensions;
    }

    @Override
    public String toString() {
        return "QueryModel{" +
                "metrics=" + metrics +
                ", conditions=" + conditions +
                ", granularity='" + granularity + '\'' +
                ", returnDemensions=" + returnDemensions +
                ", orderByFields=" + orderByFields +
                ", limit='" + limit + '\'' +
                '}';
    }
}
