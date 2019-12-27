package com.example.schedulemanagement.db;

import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.entity.Type;
import com.example.schedulemanagement.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/27
 *     desc   : 分类,标签等的增删改查操作
 * </pre>
 */

public class TypeDao extends BaseDao {


    //查询category表
    private List<Type> search(String sql, int typeInt,Object... params) {
        List<Type> res = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Type type = new Type();
                if(typeInt == Constants.TYPE_CATEGORY){//分类
                    type.setId(rs.getInt("cId"));
                    type.setName(rs.getString("cName"));
                }else {//标签
                    type.setId(rs.getInt("tagId"));
                    type.setName(rs.getString("tagName"));
                }
                type.setNum(rs.getInt("count"));
                res.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return res;
    }

    //根据类别查找
    public List<Type> query(int type){
        String sql;
        if(type == Constants.TYPE_CATEGORY){
            //求分类号，分类名以及每个分类的任务数量
            sql = " select category_3117004905_袁健策.cId,cName,count(taskId) as count" +
                    " from category_3117004905_袁健策,task_3117004905_袁健策 "+
                    " where category_3117004905_袁健策.cId = task_3117004905_袁健策.cId "+
                    " group by category_3117004905_袁健策.cId,cName ";
        }else {
            //求标签号，标签名以及每个标签的任务数量
            sql = " select tag_3117004905_袁健策.tagId,tagName,count(taskId) " +
                    " from tag_3117004905_袁健策,task_3117004905_袁健策,task_select_tag_3117004905_袁健策 "+
                    " where task_select_tag_3117004905_袁健策.tagId = tag_3117004905_袁健策.tagId "+
                    " group by tag_3117004905_袁健策.tagId,tagName ";
        }
        return search(sql,type);
    }


    //添加分类
    public int insert(String title){
        String sql = "insert into category_3117004905_袁健策(userId,cName) values(?,?)";
        return executeUpdate(sql,new Object[]{User.getInstance().getTaskId(),title});
    }

    //删除分类
    public int delete(int cId){
        String sql = "delete from category_3117004905_袁健策 where cId=?";
        return executeUpdate(sql,new Object[]{cId});
    }

    //修改分类
    public int update(Type type){
        String sql = "update category_3117004905_袁健策 set cName=? where cId=?";
        return executeUpdate(sql,new Object[]{type.getName(),type.getId()});
    }


}
