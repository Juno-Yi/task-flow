package com.junoyi.project.domain.dto;

/**
 * 任务统计信息内部类
 *
 * @author Fan
 */
public class TaskStatistics {

    private int totalTasks;
    private int completedTasks;
    private int progress;

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}