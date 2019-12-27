package com.example.schedulemanagement.db;

import android.util.Log;

import com.example.schedulemanagement.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/20
 *     desc   : 用户相关
 * </pre>
 */

public class UserDao extends BaseDao{
    private static final String TAG = "UserDao";

    public boolean login(String username, String password){
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "select * from user_3117004905_袁健策";
            stmt = prepareStatement(conn,sql,new Object[]{});
            rs = stmt.executeQuery();
            while (rs.next()) {
                String s1 = rs.getString("username");
                String s2 = rs.getString("password");
                if(username.equals(s1) && password.equals(s2)){
                    User.getInstance().setTaskId(rs.getInt("userId"));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeAll(conn,stmt,rs);
        }
        return false;
    }

    public String register(String username,String password){
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            //先判断是否有相同账户
            String querySql = "select [username] from [user_3117004905_袁健策] where [username] =?";
            stmt = prepareStatement(conn,querySql,new Object[]{username});
            rs = stmt.executeQuery();
            if(rs.next()) return  "用户名已被注册，请重新输入";
            //插入数据
            String sql = "insert into user_3117004905_袁健策(username,password) values(?,?)";
            if(executeUpdate(sql,new Object[]{username,password})!=0) return "showSuccess";
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeAll(conn,stmt,rs);
        }
        return "注册失败，请重试";
    }
}
