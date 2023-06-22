package main;

import main.models.Student;
import main.models.Task;
import main.models.Variant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class StudentEstimation extends JFrame {

    private JPanel estimationPanel;
    private JList list1;
    private JButton showTasksButton;
    private JTable table1;
    private JPanel condition;
    private JLabel maxGrade;
    private JLabel fio;
    private JLabel var;
    private JLabel description;
    private JScrollPane scrollPane;
    private JPanel tablePanel;
    private DefaultListModel<Task> listModel = new DefaultListModel<>();

    public StudentEstimation(Variant variant, Student student) {

        setContentPane(estimationPanel);
        setTitle("Задания варианта");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        fio.setText(student.getFio());
        var.setText(student.getVar() + " вариант");

        for (Task task : variant.getTasks()) {
            listModel.addElement(task);
        }
        tablePanel.setVisible(true);
        list1.setModel(listModel);

        showTasksButton.addActionListener(e -> {
            Task task = (Task) list1.getSelectedValue();
            description.setText(task.getCondition());
            maxGrade.setText(task.getMaxGrade() + " баллов");
            String[] columns = task.getData().get(0).toArray(new String[0]);
            DefaultTableModel tableModel = new DefaultTableModel(null, columns);
            for (ArrayList<String> row : task.getData()) {
                Object[] objRow = row.toArray();
                tableModel.addRow(objRow);
            }
            table1.setModel(tableModel);
        });
    }
}
