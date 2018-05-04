package com.ijunhai.constant;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

//final修饰一个类，这个类不能被继承
public final class ProxyConstants {
    //私有化构造函数，其他类不能创建该类的实例
    private ProxyConstants() {
    }

    public final static ObjectMapper JSON_MAPPER = new ObjectMapper();
    public final static String PROXY_PORT = "proxy.port";

    public final static String PROXY_THREADS = "proxy.threads";

    public final static String KYLIN_URL = "kylin.url";
    public final static String KYLIN_USERNAME = "kylin.username";
    public final static String KYLIN_PASSWORD = "kylin.password";

    public final static String KYLIN_LOGIN_TABLENAME = "agent.login.tablename";
    public final static String KYLIN_ORDER_TABLENAME = "agent.order.tablename";

    public final static String GP_ORDER_TABLENAME = "gp.order.tablename";
    public final static String GP_LOGIN_TABLENAME = "gp.login.tablename";
    public final static String GP_RETENTION_NEW_TABLENAME = "gp.retention.new.tablename";
    public final static String GP_RETENTION_NEW_PAY_TABLENAME = "gp.retention.new.pay.tablename";
    public final static String GP_RETENTION_FIRST_PAY_TABLENAME = "gp.retention.first.pay.tablename";
    public final static String GP_TOTAL_TABLENAME = "gp.total.tablename";

    public final static String GP_WEEK_TABLENAME = "gp.week.tablename";
    public final static String GP_MONTH_TABLENAME = "gp.month.tablename";

    public final static String AGENT_LOGIN_TABLENAME = "agent.login.tablename";
    public final static String AGENT_ORDER_TABLENAME = "agent.order.tablename";

    public final static String HAIWAI_LOGIN_TABLENAME = "haiwai.login.tablename";
    public final static String HAIWAI_ORDER_TABLENAME = "haiwai.order.tablename";

    public final static String DALAN_LOGIN_TABLENAME = "dalan.login.tablename";
    public final static String DALAN_ORDER_TABLENAME = "dalan.order.tablename";

    public final static String MYSQL_FILTER_CONDITION = "select distinct game_channel_id from agent_game_channel where game_id<128 and game_channel_name like '%IOS独代%';";
    public final static String MYSQL_TABLENAME = "  (import_data a join agent_game_channel b on a.game_channel_id = b.game_channel_id) join agent_game c on a.game_id = c.game_id  ";

    public final static List<String> longMetric = Arrays.asList("first_pay_retention_nuv", "first_pay_retention_uv", "retention_uv", "yet_pay_nuv", "nu_yet_pay_amount");
    public final static String KYLIN_POOL_SIZE = "kylin.pool.size";
    public final static String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public final static String MYSQL_URL = "mysql.url";
    public final static String MYSQL_USER = "mysql.user";
    public final static String MYSQL_PASSWORD = "mysql.password";


}
