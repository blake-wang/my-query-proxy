package com.ijunhai.dao;

import com.ijunhai.util.PropertiesUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ijunhai.dao.DaoConstants.MYSQL_PASSWORD;
import static com.ijunhai.dao.DaoConstants.MYSQL_URL;
import static com.ijunhai.dao.DaoConstants.MYSQL_USER;

public class MysqlDao {
    private static String dbUrl = PropertiesUtils.get(MYSQL_URL);
    private static String dbUser = PropertiesUtils.get(MYSQL_USER);
    private static String dbPass = PropertiesUtils.get(MYSQL_PASSWORD);

    private static class LazyHolder {
        private static final MysqlDao INSTANCE = new MysqlDao();
    }

    //TODO 这里为什么要用这个DataSource
    private final DataSource dataSource;

    private MysqlDao() {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setDriverClassName("com.mysql.jdbc.Driver");
        poolProps.setUrl(dbUrl);
        poolProps.setUsername(dbUser);
        poolProps.setPassword(dbPass);
        //这个又是为什么？
        poolProps.setTestOnBorrow(true);
        poolProps.setValidationQuery("select 1");
        dataSource = new DataSource(poolProps);
    }

    public static MysqlDao getInstance() {
        return LazyHolder.INSTANCE;
    }

    public ResultSet execQuery(String sql) throws SQLException {
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            //这里为什么用DataSource来获取连接
            conn = dataSource.getConnection();
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
