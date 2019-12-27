package com.example.schedulemanagement.db;

import android.util.Log;

import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.entity.User;
import com.example.schedulemanagement.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/24
 *     desc   : 任务的增删改查
 * </pre>
 */

public class TaskDao extends BaseDao {
    private static final String TAG = "TaskDao";

    //根据sql语句查询task表
    private List<Task> search(String sql, Object... params) {
        List<Task> res = new ArrayList<>();
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = prepareStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setTaskId(rs.getInt("taskId"));
                task.setcId(rs.getInt("cId"));
                task.setpId(rs.getInt("pId"));
                task.setTimeId(rs.getInt("timeId"));
                task.setStartTime(rs.getString("startTime"));
                task.setUserId(rs.getInt("userId"));
                task.setTitle(rs.getString("title"));
                task.setContent(rs.getString("content"));
                task.setDate(rs.getString("date"));
                task.setPriority(rs.getInt("priority"));
                task.setState(rs.getBoolean("state"));
                res.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAll(conn, stmt, rs);
        }
        return res;
    }

    //查询已经完成的任务表
    private List<Task> findDoneTasks(String sql,Object...params) {
        return search(sql,params);
    }

    //查询未完成的任务表
    private List<Task> findUnDoneTasks(String sql,Object...params) {
        return search(sql,params);
    }

    //查询全部任务
    public Event findAllTask() {
        //已完成按日期，开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 order by date desc,startTime desc";
        //未完成按日期，开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 order by date,startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql));
        event.setUndone(findUnDoneTasks(queryUndoneSql));
        return event;
    }


    //根据日期查找任务
    public Event queryTaskByDate(String date){
        //已完成按开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 and [date] =? order by startTime desc";
        //未完成按开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 and [date]=? order by startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, date));
        event.setUndone(findUnDoneTasks(queryUndoneSql,date));
        return event;
    }

    //根据任务号查找任务
    public Task queryTaskById(int taskId){
        String sql = "select * from [task_3117004905_袁健策] where [taskId]=?";
        if(search(sql,taskId).size()==0) return null;
        return search(sql,taskId).get(0);
    }

    //根据
    public Event queryTaskByCategoryId(int cId){
        //已完成按开始时间倒序排序
        String queryDoneSql = "select * from [task_3117004905_袁健策] where [state] = 1 and [cId] =? order by startTime desc";
        //未完成按开始时间正序排序
        String queryUndoneSql = "select * from [task_3117004905_袁健策] where [state] = 0 and [cId]=? order by startTime";
        Event event = new Event();
        event.setDone(findDoneTasks(queryDoneSql, cId));
        event.setUndone(findUnDoneTasks(queryUndoneSql,cId));
        return event;
    }


    //添加
    public int insert(Task task) {
        String sql = "insert into task_3117004905_袁健策(userId,title,content,date,startTime,priority,state) values(?,?,?,?,?,?,?)";
        return executeUpdate(sql, new Object[]{
                        User.getInstance().getTaskId(),
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
                        task.getStartTime(),
                        task.getPriority(),
                        task.isState(),
                }
        );
    }

    //删除
    public int delete(int taskId) {
        String sql = "delete from [task_3117004905_袁健策] where [taskId] = ?";
        return executeUpdate(sql, new Object[]{taskId});
    }

    //修改
    public int update(Task task) {
        String sql = "update task_3117004905_袁健策 set [title]=?,[content]=?,[date]=?,[startTime]=?,[priority]=?,[state]=? where [taskId] = ?";
        return executeUpdate(sql, new Object[]{
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
                        task.getStartTime(),
                        task.getPriority(),
                        task.isState(),
                        task.getTaskId(),
                }
        );
    }

}
