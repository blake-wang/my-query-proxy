package com.ijunhai.dao;

import com.ijunhai.util.PropertiesUtils;
import java.sql.*;
import java.util.Properties;

import static com.ijunhai.dao.DaoConstants.KYLIN_PASSWORD;
import static com.ijunhai.dao.DaoConstants.KYLIN_URL;
import static com.ijunhai.dao.DaoConstants.KYLIN_USERNAME;

public class KylinDao {

    private static class LazyHolder {
        private static final KylinDao INSTANCE = new KylinDao();
    }

    private Properties info = new Properties();
    private String kylinUrl;
    private Driver driverManager;

    public KylinDao() {
        try {
            driverManager = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
            info.put("user", PropertiesUtils.get(KYLIN_USERNAME));
            info.put("password", PropertiesUtils.get(KYLIN_PASSWORD));
            kylinUrl = PropertiesUtils.get(KYLIN_URL);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static KylinDao getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void free(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public ResultSet execuQuery(String sql) throws SQLException {
        Connection connection = null;
        ResultSet resultSet;
        try {
            connection = driverManager.connect(kylinUrl, info);
            //TODO resultSet这两个参数是什么意思，要好好研究一下
            Statement state = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = state.executeQuery(sql);
        } finally {
            free(connection);
        }

        return resultSet;
    }

}
