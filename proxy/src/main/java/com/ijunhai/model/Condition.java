package com.ijunhai.model;

import java.util.List;

public class Condition {
    private String start;

    private String end;

    private List<String> channelId;

    private List<String> gameChannelId;

    private List<String> osType;

    private List<String> gameId;

    private List<String> companyId;

    public List<String> getGameChannelId() {
        return gameChannelId;
    }

    public List<String> getOsType() {
        return osType;
    }

    public List<String> getGameId() {
        return gameId;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public List<String> getChannelId() {
        return channelId;
    }

    public List<String> getCompanyId() {
        return companyId;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setChannelId(List<String> channelId) {
        this.channelId = channelId;
    }

    public void setGameChannelId(List<String> gameChannelId) {
        this.gameChannelId = gameChannelId;
    }

    public void setOsType(List<String> osType) {
        this.osType = osType;
    }

    public void setGameId(List<String> gameId) {
        this.gameId = gameId;
    }

    public void setCompanyId(List<String> companyId) {
        this.companyId = companyId;
    }
}
