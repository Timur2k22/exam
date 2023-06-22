package main;

import main.models.Group;
import main.models.Student;
import main.models.Variant;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Interface extends JFrame {
    private ArrayList<Group> groups;
    private int varNum;
    private File varFolder;
    private JPanel mainPanel;
    private JButton importStudentsButton;
    private JButton varFolderButton;
    private JList list1;
    private JComboBox comboBox1;
    private JButton viewStudents;
    private JPanel jPanelChooseGroup;
    private JButton openVarButton;
    private DefaultListModel<Student> listModel = new DefaultListModel<>();
    private DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
    private final JFileChooser jFileChooser = new JFileChooser();
    private final JFileChooser jFolderChooser = new JFileChooser();

    public Interface() {

        setContentPane(mainPanel);
        setTitle("Создание отчета о работах студентов");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(550, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        importStudentsButton.addActionListener(e -> {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("", "xlsx");
            jFileChooser.setFileFilter(filter);
            jFileChooser.setCurrentDirectory(new File("./src/main/resources"));
            jFileChooser.showDialog(null, "Choose file:");
            File file = jFileChooser.getSelectedFile();
            try {
                groups = ExcelHandler.importStudents(file);
                for (Group group : groups) {
                    cbModel.addElement(group.getName());
                }
                comboBox1.setModel(cbModel);
            } catch (IOException | InvalidFormatException ex) {
                JOptionPane.showMessageDialog(Interface.this, "Ошибка при импорте файла");
            }
        });

        viewStudents.addActionListener(e -> {
            String chosenGroup = (String) comboBox1.getSelectedItem();
            Group group = groups.stream().filter(group1 -> Objects.equals(group1.getName(), chosenGroup)).findFirst().get();
            for (Student student : group.getStudents()) {
                listModel.addElement(student);
            }
            list1.setModel(listModel);
        });


        varFolderButton.addActionListener(e -> {
            jFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jFolderChooser.setCurrentDirectory(new File("./src/main/resources/Statistics"));
            jFolderChooser.showDialog(null, "Choose folder:");
            varFolder = jFolderChooser.getSelectedFile();
        });

        openVarButton.addActionListener(e -> {
            Student student = (Student) list1.getSelectedValue();
            varNum = student.getVar();
            try {
                String path = varFolder.getPath() + "\\" + varNum + ".xlsx";
                Variant variant = ExcelHandler.importVar(path);
                StudentEstimation estimation = new StudentEstimation(variant, student);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Interface.this, "Ошибка при импорте файла");
            }
        });
    }
}
