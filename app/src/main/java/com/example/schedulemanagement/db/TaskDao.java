package com.example.schedulemanagement.db;

import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;
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


    //查询task表
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
                task.setUserId(rs.getInt("userId"));
                task.setTitle(rs.getString("title"));
                task.setContent(rs.getString("content"));
                task.setDate(DateUtils.date2String(rs.getDate("date")));
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
    private List<Task> findDoneTasks() {
        String sql = "select * from [task_3117004905_袁健策] where [state] = 1";
        return search(sql);
    }

    //查询未完成的任务表
    private List<Task> findUnDoneTasks() {
        String sql = "select * from [task_3117004905_袁健策] where [state] = 0";
        return search(sql);
    }

    //查询任务
    public Event findEvent() {
        Event event = new Event();
        event.setDone(findDoneTasks());
        event.setUndone(findUnDoneTasks());
        return event;
    }

    //添加
    public int insert(Task task) {
        String sql = "insert into task_3117004905_袁健策(userId,title,content,date,priority,state) values(?,?,?,?,?,?)";
        return executeUpdate(sql, new Object[]{
                        task.getUserId(),
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
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
        String sql = "update task_3117004905_袁健策 set [title]=?,[content]=?,[date]=?,[priority]=?,[state]=? where [userId] = ?";
        return executeUpdate(sql, new Object[]{
                        task.getTitle(),
                        task.getContent(),
                        task.getDate(),
                        task.getPriority(),
                        task.isState(),
                        task.getUserId(),
                }
        );
    }

}
