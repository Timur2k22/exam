package main;

import main.models.Group;
import main.models.Student;
import main.models.StudentReport;
import main.models.Task;
import main.models.TaskReport;
import main.models.Variant;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Interface extends JFrame {
    private ArrayList<Group> groups;
    private Group chosenGroup;
    private int varNum;
    private File varFolder;
    private JPanel mainPanel;
    private JButton importStudentsButton;
    private JButton varFolderButton;
    private JList list1;
    private JComboBox comboBox1;
    private JPanel jPanelChooseGroup;
    private JButton openVarButton;
    private JButton createGroupReportButton;
    private JButton noWorkButton;
    private JButton deleteRateButton;
    private JButton personalReportButton;
    private JButton allReportButton;

    private final JFileChooser jFileChooser = new JFileChooser();
    private final JFileChooser jFolderChooser = new JFileChooser();

    public Interface() {

        setContentPane(mainPanel);
        setTitle("Создание отчета о работах студентов");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        importStudentsButton.addActionListener(e -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("", "xlsx");
            jFileChooser.setFileFilter(filter);
            jFileChooser.showDialog(null, "Choose file:");
            File file = jFileChooser.getSelectedFile();
            try {
                DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
                groups = ExcelHandler.importStudents(file);
                for (Group group : groups) {
                    cbModel.addElement(group.getName());
                }
                comboBox1.setModel(cbModel);
                comboBox1.setEnabled(true);
                allReportButton.setEnabled(true);
                JOptionPane.showMessageDialog(Interface.this, "Студенты импортированы");
            } catch (IOException | InvalidFormatException ex) {
                JOptionPane.showMessageDialog(Interface.this, "Ошибка при импорте файла");
            } catch (IllegalArgumentException illegalArgumentException) {
                JOptionPane.showMessageDialog(Interface.this, "Файл не выбран");
            }
        });

        varFolderButton.addActionListener(e -> {
            try {
                jFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFolderChooser.showDialog(null, "Выбрать папку:");
                varFolder = jFolderChooser.getSelectedFile();
                JOptionPane.showMessageDialog(Interface.this, "Выбрана папка " + varFolder.getName());
            } catch (IllegalArgumentException | NullPointerException exception) {
                JOptionPane.showMessageDialog(Interface.this, "Папка не выбрана");
            }
        });

        openVarButton.addActionListener(e -> {
            Student student = (Student) list1.getSelectedValue();
            if (student != null) {
                varNum = student.getVar();
                try {
                    String path = varFolder.getPath() + "\\" + varNum + ".xlsx";
                    Variant variant = ExcelHandler.importVar(path);
                    StudentEstimation estimation = new StudentEstimation(variant, student, this);
                    setEnabled(false);
                    groups.get(comboBox1.getSelectedIndex()).getStudents().get(list1.getSelectedIndex())
                            .setReport(estimation.getReport());
                } catch (IOException | InvalidOperationException | NullPointerException ex) {
                    JOptionPane.showMessageDialog(Interface.this, "Не найден файл варианта");
                }
            } else JOptionPane.showMessageDialog(Interface.this, "Студент не выбран");
            updateStudentsList();
        });

        createGroupReportButton.addActionListener(e -> {
            int minGrade = 0;
            try {
                minGrade = Integer.parseInt(JOptionPane.showInputDialog(Interface.this, "Введите минимальный проходной балл:"));
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(Interface.this, "Необходимо ввести целое число");
            }
            if (chosenGroup.getStudents().stream().filter(student -> student.getReport() == null).findFirst().orElse(null) != null) {
                JOptionPane.showMessageDialog(Interface.this, "Не все студенты оценены");
            } else {
                XSSFWorkbook book = new XSSFWorkbook();
                XSSFSheet sheet = book.createSheet("Отчет по группе " + chosenGroup.getName());
                XSSFRow firstRow = sheet.createRow(0);
                sheet.setColumnWidth(0, 8000);
                firstRow.createCell(0).setCellValue("ФИО студента");
                firstRow.createCell(1).setCellValue("Вариант");
                Student studentWithMaxWorks = chosenGroup.getStudents().stream().filter(student -> student.getReport().getTasks() != null)
                        .max(Comparator.comparing(student -> student.getReport().getTasks().size())).orElse(null);
                int maxWorks = 0;
                if (studentWithMaxWorks != null) {
                    maxWorks = studentWithMaxWorks.getReport().getTasks().size();
                }
                for (int i = 0; i < maxWorks; i++) {
                    firstRow.createCell(i + 2).setCellValue("Задание " + (i + 1));
                }
                firstRow.createCell(firstRow.getLastCellNum()).setCellValue("Оценка");
                firstRow.createCell(firstRow.getLastCellNum()).setCellValue("Итог");
                for (int i = 0; i < chosenGroup.getStudents().size(); i++) {
                    Student student = chosenGroup.getStudents().get(i);
                    XSSFRow row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(student.getFio());
                    row.createCell(1).setCellValue(student.getVar());
                    if (!student.getReport().isNoWork()) {
                        for (int j = 0; j < maxWorks; j++) {
                            if (j < student.getReport().getTasks().size()) {
                                row.createCell(j + 2).setCellValue(student.getReport().getTasks().get(j).getReport().getGrade());
                            } else row.createCell(j + 2).setCellValue("Нет задания");
                        }
                        int finalGrade = student.getReport().getTasks().stream().map(Task::getReport).mapToInt(TaskReport::getGrade).sum();
                        row.createCell(row.getLastCellNum()).setCellValue(finalGrade);
                        if (finalGrade < minGrade) {
                            row.createCell(row.getLastCellNum()).setCellValue("Незачет");
                        } else row.createCell(row.getLastCellNum()).setCellValue("Зачет");
                    } else {
                        for (int j = 0; j < maxWorks; j++) {
                            row.createCell(j + 2).setCellValue(0);
                        }
                        row.createCell(row.getLastCellNum()).setCellValue(0);
                        row.createCell(row.getLastCellNum()).setCellValue("Нет работы");
                    }
                }
                jFolderChooser.showDialog(null, "Выбрать папку сохранения");
                File file = new File(jFolderChooser.getSelectedFile().getName() + chosenGroup.getName() + ".xlsx");
                try {
                    book.write(new FileOutputStream(file));
                    book.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(Interface.this, "Отчет сформирован");
            }
        });

        noWorkButton.addActionListener(e -> {
            StudentReport noWorkReport = new StudentReport();
            noWorkReport.setNoWork(true);
            chosenGroup.getStudents().get(list1.getSelectedIndex()).setReport(noWorkReport);
            updateStudentsList();
        });

        list1.addListSelectionListener(e -> {
            int chosenStudentIndex = list1.getSelectedIndex();
            try {
                Student chosenStudent = chosenGroup.getStudents().get(chosenStudentIndex);
                if (chosenStudent.getReport() != null) {
                    openVarButton.setEnabled(!chosenStudent.getReport().isNoWork());
                    openVarButton.setText("Редактировать оценку");
                    deleteRateButton.setEnabled(true);
                    noWorkButton.setEnabled(false);
                    personalReportButton.setEnabled(true);
                } else {
                    openVarButton.setEnabled(true);
                    openVarButton.setText("Оценить работу");
                    deleteRateButton.setEnabled(false);
                    noWorkButton.setEnabled(true);
                    personalReportButton.setEnabled(false);
                }
            } catch (IndexOutOfBoundsException exception) {
                System.out.println("Нет выбранного значения");
            }
        });

        deleteRateButton.addActionListener(e -> {
            int chosenStudentIndex = list1.getSelectedIndex();
            Student chosenStudent = chosenGroup.getStudents().get(chosenStudentIndex);
            chosenStudent.setReport(null);
            updateStudentsList();
        });

        personalReportButton.addActionListener(e -> {
            int minGrade = 0;
            try {
                minGrade = Integer.parseInt(JOptionPane.showInputDialog(Interface.this, "Введите минимальный проходной балл:"));
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(Interface.this, "Необходимо ввести целое число");
            }
            XSSFWorkbook book = new XSSFWorkbook();
            Student student = (Student) list1.getSelectedValue();
            XSSFSheet sheet = book.createSheet("Отчет по студенту " + student.getFio());
            XSSFRow firstRow = sheet.createRow(0);
            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(2, 10000);
            firstRow.createCell(0).setCellValue(student.getFio());
            firstRow.createCell(1).setCellValue(chosenGroup.getName());
            if (!student.getReport().isNoWork()) {
                XSSFRow secondRow = sheet.createRow(2);
                secondRow.createCell(0).setCellValue("Задание:");
                secondRow.createCell(1).setCellValue("Оценка:");
                secondRow.createCell(2).setCellValue("Комментарий:");
                for (Task task : student.getReport().getTasks()) {
                    XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                    row.createCell(0).setCellValue("Задание " + (task.getTaskNumber() + 1));
                    row.createCell(1).setCellValue(task.getReport().getGrade());
                    row.createCell(2).setCellValue(task.getReport().getComment());
                }
                XSSFRow lastRow = sheet.createRow(sheet.getLastRowNum() + 1);
                lastRow.createCell(0).setCellValue("Итог:");
                int finalGrade = student.getReport().getTasks().stream().map(Task::getReport).mapToInt(TaskReport::getGrade).sum();
                lastRow.createCell(1).setCellValue(finalGrade);
                if (finalGrade >= minGrade) {
                    lastRow.createCell(2).setCellValue("Зачет");
                } else lastRow.createCell(2).setCellValue("Незачет");
            } else {
                XSSFRow lastRow = sheet.createRow(2);
                lastRow.createCell(0).setCellValue("Итог:");
                lastRow.createCell(1).setCellValue("Нет работы");
            }
            jFolderChooser.showDialog(null, "Выбрать папку сохранения");
            File file = new File(jFolderChooser.getSelectedFile().getName() + student.getFio() + ".xlsx");
            try {
                book.write(new FileOutputStream(file));
                book.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(Interface.this, "Отчет сформирован");
        });

        allReportButton.addActionListener(e -> {
            int minGrade;
            try {
                minGrade = Integer.parseInt(JOptionPane.showInputDialog(Interface.this, "Введите минимальный проходной балл:"));
                boolean allStudentsRated = true;
                for (Group group : groups) {
                    if (group.getStudents().stream().filter(student -> student.getReport() == null).findFirst().orElse(null) != null) {
                        allStudentsRated = false;
                    }
                }
                if (!allStudentsRated) {
                    JOptionPane.showMessageDialog(Interface.this, "Не все студенты оценены");
                } else {
                    XSSFWorkbook book = new XSSFWorkbook();
                    for (Group group : groups) {
                        XSSFSheet sheet = book.createSheet("Отчет по группе " + group.getName());
                        XSSFRow firstRow = sheet.createRow(0);
                        sheet.setColumnWidth(0, 8000);
                        firstRow.createCell(0).setCellValue("ФИО студента");
                        firstRow.createCell(1).setCellValue("Вариант");
                        Student studentWithMaxWorks = group.getStudents().stream().filter(student -> student.getReport().getTasks() != null)
                                .max(Comparator.comparing(student -> student.getReport().getTasks().size())).orElse(null);
                        int maxWorks = 0;
                        if (studentWithMaxWorks != null) {
                            maxWorks = studentWithMaxWorks.getReport().getTasks().size();
                        }
                        for (int i = 0; i < maxWorks; i++) {
                            firstRow.createCell(i + 2).setCellValue("Задание " + (i + 1));
                        }
                        firstRow.createCell(firstRow.getLastCellNum()).setCellValue("Оценка");
                        firstRow.createCell(firstRow.getLastCellNum()).setCellValue("Итог");
                        for (int i = 0; i < group.getStudents().size(); i++) {
                            Student student = group.getStudents().get(i);
                            XSSFRow row = sheet.createRow(i + 1);
                            row.createCell(0).setCellValue(student.getFio());
                            row.createCell(1).setCellValue(student.getVar());
                            if (!student.getReport().isNoWork()) {
                                for (int j = 0; j < maxWorks; j++) {
                                    if (j < student.getReport().getTasks().size()) {
                                        row.createCell(j + 2).setCellValue(student.getReport().getTasks().get(j).getReport().getGrade());
                                    } else row.createCell(j + 2).setCellValue("Нет задания");
                                }
                                int finalGrade = student.getReport().getTasks().stream().map(Task::getReport).mapToInt(TaskReport::getGrade).sum();
                                row.createCell(row.getLastCellNum()).setCellValue(finalGrade);
                                if (finalGrade < minGrade) {
                                    row.createCell(row.getLastCellNum()).setCellValue("Незачет");
                                } else row.createCell(row.getLastCellNum()).setCellValue("Зачет");
                            } else {
                                for (int j = 0; j < maxWorks; j++) {
                                    row.createCell(j + 2).setCellValue(0);
                                }
                                row.createCell(row.getLastCellNum()).setCellValue(0);
                                row.createCell(row.getLastCellNum()).setCellValue("Нет работы");
                            }
                        }
                    }
                    jFolderChooser.showDialog(null, "Выбрать папку сохранения");
                    File file = new File(jFolderChooser.getSelectedFile().getName() + "Отчет по потоку" + ".xlsx");
                    try {
                        book.write(new FileOutputStream(file));
                        book.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(Interface.this, "Отчет сформирован");
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(Interface.this, "Необходимо ввести целое число");
            }
        });

        comboBox1.addActionListener(e -> {
            DefaultListModel<Student> listModel = new DefaultListModel<>();
            String groupName = (String) comboBox1.getSelectedItem();
            chosenGroup = groups.stream().filter(group1 -> Objects.equals(group1.getName(), groupName)).findFirst().get();
            for (Student student : chosenGroup.getStudents()) {
                listModel.addElement(student);
            }
            list1.setModel(listModel);
            list1.setSelectedIndex(0);
            createGroupReportButton.setEnabled(true);
        });
    }

    public void updateStudentsList() {
        DefaultListModel<Student> listModel = new DefaultListModel<>();
        for (Student student : chosenGroup.getStudents()) {
            listModel.addElement(student);
        }
        list1.setModel(listModel);
        list1.setSelectedIndex(0);
    }
}