package main;

import main.models.Student;
import main.models.StudentReport;
import main.models.Task;
import main.models.TaskReport;
import main.models.Variant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class StudentEstimation extends JFrame {

    private final StudentReport report = new StudentReport();
    private ArrayList<Task> tasks;
    private JPanel estimationPanel;
    private JList list1;
    private JButton showTaskButton;
    private JTable table1;
    private JPanel condition;
    private JLabel maxGrade;
    private JLabel fio;
    private JLabel var;
    private JLabel description;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    private JLabel description2;
    private JButton rateButton;
    private JTextField textField1;
    private JButton endRateButton;

    public StudentEstimation(Variant variant, Student student, Interface intrface) {

        setContentPane(estimationPanel);
        setTitle("Задания варианта");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        if (student.getReport() != null) {
            tasks = student.getReport().getTasks();
        } else tasks = variant.getTasks();
        fio.setText(student.getFio());
        var.setText(student.getVar() + " вариант");

        updateTasksList();

        showTaskButton.addActionListener(e -> {
            textField1.setText("");
            textField1.setEnabled(true);
            rateButton.setEnabled(true);
            Task task = tasks.get(list1.getSelectedIndex());
            if (task.getCondition().length() > 125) {
                description.setText(task.getCondition().substring(0, 125) + "-");
                description2.setText(task.getCondition().substring(125));
            } else {
                description.setText(task.getCondition());
                description2.setText("");
            }
            if (task.getMaxGrade() > 4) {
                maxGrade.setText(task.getMaxGrade() + " баллов");
            } else maxGrade.setText(task.getMaxGrade() + " балла");
            if (tasks.get(list1.getSelectedIndex()).getReport() != null) {
                textField1.setText(tasks.get(list1.getSelectedIndex()).getReport().getGrade() + "");
            }
            String[] columns = task.getData().get(0).toArray(new String[0]);
            DefaultTableModel tableModel = new DefaultTableModel(null, columns);
            for (int i = 1; i < task.getData().size(); i++) {
                ArrayList<String> row = task.getData().get(i);
                Object[] objRow = row.toArray();
                tableModel.addRow(objRow);
            }
            table1.setModel(tableModel);
        });

        rateButton.addActionListener(e -> {
            try {
                if (tasks.get(list1.getSelectedIndex()).getMaxGrade() < Integer.parseInt(textField1.getText())) {
                    JOptionPane.showMessageDialog(StudentEstimation.this, "Оценка превышает максимальный балл");
                } else {
                    TaskReport taskReport = new TaskReport();
                    taskReport.setGrade(Integer.parseInt(textField1.getText()));
                    tasks.get(list1.getSelectedIndex()).setReport(taskReport);
                    updateTasksList();
                }
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(StudentEstimation.this, "Необходимо ввести целое число");
            }
        });

        endRateButton.addActionListener(e -> {
            if (tasks.stream().filter(task -> task.getReport() == null).findFirst().orElse(null) != null) {
                JOptionPane.showMessageDialog(StudentEstimation.this, "Не все задания оценены");
            } else {
                report.setTasks(tasks);
                intrface.setEnabled(true);
                dispose();
            }
        });
        list1.addListSelectionListener(e -> showTaskButton.setEnabled(true));
    }

    public void updateTasksList() {
        DefaultListModel<Task> listModel = new DefaultListModel<>();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
        list1.setModel(listModel);
    }

    public StudentReport getReport() {
        return report;
    }
}