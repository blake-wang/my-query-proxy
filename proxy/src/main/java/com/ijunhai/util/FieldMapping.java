package com.ijunhai.util;

import java.util.HashMap;
import java.util.Map;

import static com.ijunhai.constant.ProxyConstants.*;

public class FieldMapping {
    private static Map<String, String> loginMap;
    private static Map<String, String> orderMapA;
    private static Map<String, String> orderMapB;
    private static Map<String, String> mysqlMap;
    private static Map<String, String> timeMap;
    private static Map<String, String> gpMap;

    private final static String AGENTORDER = PropertiesUtils.get(AGENT_ORDER_TABLENAME);
    private final static String HAIWAIORDER = PropertiesUtils.get(HAIWAI_ORDER_TABLENAME);
    private final static String DALANORDER = PropertiesUtils.get(DALAN_ORDER_TABLENAME);

    private final static String AGENTLOGIN = PropertiesUtils.get(AGENT_LOGIN_TABLENAME);
    private final static String HAIWAILOGIN = PropertiesUtils.get(HAIWAI_LOGIN_TABLENAME);
    private final static String DALANLOGIN = PropertiesUtils.get(DALAN_LOGIN_TABLENAME);

    static {
        loginMap = new HashMap<>();
        loginMap.put("CHANNELID", "\"AGENT-CHANNEL_ID\"");
        loginMap.put("GAMECHANNELID", "\"AGENT-GAME_CHANNEL_ID\"");
        loginMap.put("GAMEID", "\"GAME-GAME_ID\"");
        loginMap.put("OSTYPE", "\"DEVICE-OS_TYPE\"");
        loginMap.put("COMPANYID", "\"GAME-COMPANY_ID\"");

        orderMapA = new HashMap<>();
        orderMapA.put("CHANNELID", "\"AGENT-CHANNEL_ID\"");
        orderMapA.put("GAMECHANNELID", "\"AGENT-GAME_CHANNEL_ID\"");
        orderMapA.put("GAMEID", "\"GAME-GAME_ID\"");
        orderMapA.put("OSTYPE", "\"DEVICE-OS_TYPE\"");
        orderMapA.put("COMPANYID", "\"GAME-COMPANY_ID\"");

        orderMapB = new HashMap<>();
        orderMapB.put("CHANNELID", "channel_id");
        orderMapB.put("GAMECHANNELID", "game_channel_id");
        orderMapB.put("GAMEID", "game_id");
        orderMapB.put("OSTYPE", "sub_pf");
        orderMapB.put("COMPANYID", "company_id");

        gpMap = new HashMap<>();
        gpMap.put("CHANNELID", "channel_id");
        gpMap.put("GAMECHANNELID", "game_channel_id");
        gpMap.put("GAMEID", "game_id");
        gpMap.put("OSTYPE", "os_type");
        gpMap.put("COMPANYID", "company_id");

        mysqlMap = new HashMap<>();
        mysqlMap.put("GAMECHANNELID", "a.game_channel_id");
        mysqlMap.put("GAMEID", "a.game_id");
        mysqlMap.put("CHANNELID", "b.channel_id");
        mysqlMap.put("OSTYPE", "b.pf");
        mysqlMap.put("COMPANYID", "company_id");

        timeMap = new HashMap<>();
        timeMap.put("DAY", "SERVER_DATE_DAY");
        timeMap.put("HOUR", "SERVER_DATE_HOUR");
        timeMap.put("MINUTE", "SERVER_DATE_MINUTE");
        timeMap.put("day", "date");
        timeMap.put("hour", "hour");
        timeMap.put("minute", "minute");
    }

    public static String TIME_COLUMN = "server_date_day";


    public static String getTimeColumn(String time) {
        return timeMap.get(time);
    }

    public static String getMysql(String key) {
        return mysqlMap.get(key);
    }

    public static String getKylin(String key, String tableName) {
        String field = "";
        if (tableName.equals(DALANLOGIN) || tableName.equals(HAIWAILOGIN) || tableName.equals(AGENTLOGIN)) {
            field = loginMap.get(key);
        } else if (tableName.equals(DALANORDER) || tableName.equals(HAIWAIORDER)) {
            field = orderMapA.get(key);
        } else if (tableName.equals(AGENTORDER)) {
            field = orderMapB.get(key);
        }
        return field;
    }

    public static String getGP(String key) {
        return gpMap.get(key);
    }
}
