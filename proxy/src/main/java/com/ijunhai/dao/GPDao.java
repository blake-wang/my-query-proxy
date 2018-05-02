package com.ijunhai.dao;

import com.ijunhai.util.PropertiesUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ijunhai.dao.DaoConstants.GP_PASSWORD;
import static com.ijunhai.dao.DaoConstants.GP_URL;
import static com.ijunhai.dao.DaoConstants.GP_USER;

public class GPDao {
    private static String dbUrl = PropertiesUtils.get(GP_URL);
    private static String dbUser = PropertiesUtils.get(GP_USER);
    private static String dbPass = PropertiesUtils.get(GP_PASSWORD);

    private static class LazyHolder {
        private static final GPDao INSTANCE = new GPDao();
    }

    private final DataSource dataSource;

    private GPDao() {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setDriverClassName("org.postgresql.Driver");
        poolProps.setUrl(dbUrl);
        poolProps.setUsername(dbUser);
        poolProps.setPassword(dbPass);
        poolProps.setTestOnBorrow(true);
        poolProps.setValidationQuery("select 1");
        dataSource = new DataSource(poolProps);
    }

    public static GPDao getInstance() {
        return LazyHolder.INSTANCE;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = pstmt.executeQuery();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return resultSet;
    }
}
