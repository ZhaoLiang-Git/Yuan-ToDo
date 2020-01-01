package com.example.schedulemanagement.db;

import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.entity.Type;
import com.example.schedulemanagement.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/27
 *     desc   : 分类,标签等的增删改查操作
 * </pre>
 */

public class TypeDao extends BaseDao {


    //查询Task表
    private List<Type> searchTask(String sql, int typeInt, Object... params) {
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


    //查询分类表，标签表是否存在
    private boolean search(String sql,Object... params) {
        List<Type> res = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            if(rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return false;
    }


    //查询分类表，得到id和标题键值对的HashMap
    private HashMap<String,Integer> searchType(String sql, int typeInt,Object... params) {
        //键为id
        HashMap<String,Integer> res = new LinkedHashMap<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if(typeInt == Constants.TYPE_CATEGORY){//分类
                    res.put(rs.getString("cName"),rs.getInt("cId"));
                }else {//标签
                    res.put(rs.getString("tagName"),rs.getInt("tagId"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return res;
    }

    //查询任务号的位置
    private List<Integer> searchTypeIndex(String sql, int typeInt,List<Integer> ids,Object... params) {
        List<Integer> resList = new ArrayList<>();
        int res = -1;
        int index = 0;
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();

            while (rs.next()) {
                res++;
                if(typeInt == Constants.TYPE_CATEGORY){//分类
                    if(rs.getInt("cId") == ids.get(index)) {
                        //分类只有一个，找到后直接返回
                        resList.add(res);
                        return resList;
                    }
                }else {//标签
                    if(rs.getInt("tagId") == ids.get(index)) {
                        resList.add(res);
                        index++;
                    }
                }
                if(index == ids.size()) break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return resList;
    }


    //查找分类是否为空
    public boolean isEmptyOfCategory(){
        String sql = "select * from category_3117004905_袁健策 where [userId]=?";
        return !search(sql,User.getInstance().getUserId());
    }



    //根据类别查找
    public List<Type> query(int type){
        String sql;
        if(type == Constants.TYPE_CATEGORY){
            //求分类号，分类名以及每个分类的任务数量
            sql = " select category_3117004905_袁健策.cId,cName,count(taskId) as count" +
                    " from category_3117004905_袁健策 left join task_3117004905_袁健策 "+
                    " on category_3117004905_袁健策.cId = task_3117004905_袁健策.cId"+
                    " where category_3117004905_袁健策.userId = ?"+
                    " group by category_3117004905_袁健策.cId,cName ";
        }else {
            //求标签号，标签名以及每个标签的任务数量
            sql = " select tag_3117004905_袁健策.tagId,tagName,count(taskId) as count " +
                    " from tag_3117004905_袁健策 left join task_select_tag_3117004905_袁健策 "+
                    " on task_select_tag_3117004905_袁健策.tagId = tag_3117004905_袁健策.tagId"+
                    " where tag_3117004905_袁健策.userId = ?"+
                    " group by tag_3117004905_袁健策.tagId,tagName ";
        }
        return searchTask(sql,type,User.getInstance().getUserId());
    }

    //查找分类列表
    public HashMap<String,Integer> queryCategory(){
        String sql = "select * from category_3117004905_袁健策 where [userId] = ?";
        return searchType(sql,Constants.TYPE_CATEGORY,User.getInstance().getUserId());
    }

    //查找标签列表
    public HashMap<String,Integer> queryTag(){
        String sql = "select * from tag_3117004905_袁健策 where [userId] = ?";
        return searchType(sql,Constants.TYPE_TAG,User.getInstance().getUserId());
    }

    //查找分类号的位置
    public int queryCategoryIndex(int id){
        String sql = "select * from category_3117004905_袁健策 where [userId] = ?";
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        return searchTypeIndex(sql,Constants.TYPE_CATEGORY,ids,User.getInstance().getUserId()).get(0);
    }

    //根据任务号找出标签列表
    public List<Integer> queryTagByTaskId(int taskId){
        String sql = "select tagId from task_select_tag_3117004905_袁健策 where [taskId]=?";
        List<Integer> res = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, new Object[]{taskId});
            rs = stmt.executeQuery();
            while (rs.next()) {
                res.add(rs.getInt("tagId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return res.size() ==0?null:res;
    }


    //查找标签集合的位置
    public List<Integer> queryTagIndexList(List<Integer> ids){
        String sql = "select * from tag_3117004905_袁健策 where [userId] = ?";
        return searchTypeIndex(sql,Constants.TYPE_TAG,ids,User.getInstance().getUserId());
    }




    //添加分类
    public int insert(String title,boolean isCategory){
        String sql;
        //如果名称重复，返回-1
        if(isExist(title,isCategory)) return -1;
        //否则直接插入到表中
        if(isCategory) sql = "insert into category_3117004905_袁健策(userId,cName) values(?,?)";
        else sql = "insert into tag_3117004905_袁健策(userId,tagName) values(?,?)";
        return executeUpdate(sql,new Object[]{User.getInstance().getUserId(),title});
    }

    //判断名字是否重复
    private boolean isExist(String title, boolean isCategory){
        String sql;
        if(isCategory) sql = "select * from category_3117004905_袁健策 where [cName]=? and [userId] = ?";
        else sql ="select * from tag_3117004905_袁健策 where [tagName]=? and [userId] = ?";
        return search(sql,title,User.getInstance().getUserId());
    }

    //删除分类
    public int delete(int cId,boolean isCategory){
        String sql;
        if(isCategory) sql = "delete from category_3117004905_袁健策 where cId=? ";
        else sql = "delete from tag_3117004905_袁健策 where tagId=?";
        return executeUpdate(sql,new Object[]{cId});
    }

    //修改分类
    public int update(Type type){
        String sql = "update category_3117004905_袁健策 set cName=? where cId=?";
        return executeUpdate(sql,new Object[]{type.getName(),type.getId()});
    }


}
