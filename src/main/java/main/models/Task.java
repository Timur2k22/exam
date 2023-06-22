package main.models;

import java.util.ArrayList;

public class Task {

    int taskNumber;
    String condition;
    int maxGrade;
    ArrayList<ArrayList<String>> Data;
    TaskReport report;

    public TaskReport getReport() {
        return report;
    }

    public void setReport(TaskReport report) {
        this.report = report;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    public ArrayList<ArrayList<String>> getData() {
        return Data;
    }

    public void setData(ArrayList<ArrayList<String>> data) {
        Data = data;
    }


    @Override
    public String toString() {
        if (report != null){
            return "������� " + (taskNumber+1) + ", �������, ������: " +report.getGrade();
        } else return "������� " + (taskNumber+1);
    }
}
