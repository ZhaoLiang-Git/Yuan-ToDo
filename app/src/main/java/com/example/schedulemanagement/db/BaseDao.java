package com.example.schedulemanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.schedulemanagement.app.Constants.DB_NAME;
import static com.example.schedulemanagement.app.Constants.HOST_IP;
import static com.example.schedulemanagement.app.Constants.WIFI_IP;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/20
 *     desc   : 数据库连接
 * </pre>
 */

public class BaseDao {

    protected Connection conn = null;

    /***
     *
     * @return 打开连接
     */
    protected Connection getConnection() {
        conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + WIFI_IP + ":1433/" + DB_NAME + ";useunicode=true;characterEncoding=UTF-8", "sa", "3117004905");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    /****
     *关闭数据库连接
     */
    protected void closeAll(Connection conn, PreparedStatement ps, ResultSet rs) {
        if (rs != null)
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    /***
     *
     * 增删改方法
     * 参数为 SQL语句 和 对象数组
     * @ isReturnPrimaryKey 是否返回主键
     * @return 返回受影响行数
     */
    public int executeUpdate(String sql, Object[] ob) {
        conn = getConnection();
        PreparedStatement ps = null;
        try {
            ps = prepareStatement(conn, sql, ob);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            closeAll(conn, ps, null);
        }
    }

    //需要返回主键
    public int executeUpdateNeedReturnPK(String sql, Object[] ob) {
        conn = getConnection();
        PreparedStatement ps = null;
        ResultSet resultSet;
        try {
            int index = 1;
            ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            if (ps != null && ob != null) {
                for (Object o : ob) {
                    ps.setObject(index, o);
                    index++;
                }
            }
            ps.executeUpdate();
            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            closeAll(conn, ps, null);
        }
        return 0;
    }

    /***
     * 查询方法
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql, Object[] ob) {
        PreparedStatement ps = null;
        try {
            int index = 1;
            ps = conn.prepareStatement(sql);
            if (ps != null && ob != null) {
                for (int i = 0; i < ob.length; i++) {
                    ps.setObject(index, ob[i]);
                    index++;
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return ps;
    }

}
