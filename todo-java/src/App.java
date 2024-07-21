import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    static Connection conn;
    static DefaultTableModel tableModel;
    static Statement stat;
    static ResultSet rows;
    static JTextField textField;

    public static void main(String[] args) throws Exception {
        // System.out.println("Hello, World!");
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/my-todo";
        String username = "root";
        String password = "root";
        conn = DriverManager.getConnection(url, username, password);
        System.out.println("Database Connected!!");

        // Insert Data into the Database

        JFrame frame = new JFrame("My Todo App");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        textField = new JTextField(20);
        JButton addButton = new JButton("Add");

        JPanel panel = new JPanel();
        panel.add(textField);
        panel.add(addButton);

        tableModel = new DefaultTableModel(new Object[] { "Id", "Task", "Completed" }, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scorll = new JScrollPane(table);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selectedIndex = table.getSelectedRow();
                // System.out.println(index);
                System.out.println(table.getValueAt(selectedIndex, 1));
                String[] option = { "yes", "no" };
                int choice = JOptionPane.showOptionDialog(null, "Do you want to delete the task", "Delete task",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, option, option[0]);

                if (choice == 0) {
                    System.out.println("Do Somthinng");
                    deleteData(selectedIndex);
                } else {
                    System.out.println("Do nothing");
                    updateData(selectedIndex);
                }
            }
        });

        loadData();

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addData();
            }
        });

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scorll, BorderLayout.SOUTH);
        frame.setVisible(true);

        // tableModel.addRow(new Object[] { 1, "Test", "Yes" });
        // tableModel.addRow(new Object[] { 1, "Test", "Yes" });
        // tableModel.addRow(new Object[] { 1, "Test", "Yes" });
        // tableModel.addRow(new Object[] { 1, "Test", "Yes" });
    }

    public static void loadData() {
        String query = "Select * from todos";

        try {
            stat = conn.createStatement();
            rows = stat.executeQuery(query);

            tableModel.setRowCount(0);// This code so that the table doesn't rerender all the elements again

            while (rows.next()) {
                tableModel.addRow(new Object[] { rows.getInt("id"), rows.getString("task"), rows.getInt("completed") });
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    private static void addData() {
        System.out.println("I clicked the button");
        try {
            String input = textField.getText();
            if (input.equals("")) {
                return;
            }
            System.out.println(textField.getText());
            PreparedStatement statement = conn.prepareStatement("Insert into todos (task) values (?)");
            statement.setString(1, input);
            statement.executeUpdate();
            textField.setText("");
            loadData();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void deleteData(int rowIndex) {
        System.out.println("Delete CLicked!!");
        int taskID = (int) tableModel.getValueAt(rowIndex, 0);
        String deleteQuery = "Delete from todos where id = ?";
        PreparedStatement delpreparedStatement;

        tableModel.setRowCount(0);
        try {
            delpreparedStatement = conn.prepareStatement(deleteQuery);
            delpreparedStatement.setInt(1, taskID);

            delpreparedStatement.executeUpdate();
            loadData();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void updateData(int rowIndex) {

        // System.out.println("Enter the new task:");
        // String newfirstname = scanner.next();

        // System.out.println("Enter the new customer last name:");
        // String newlastname = scanner.next();

        // System.out.println("Enter the new customer email:");
        // String newemail = scanner.next();

        // System.out.println("Enter the new customer phone:");
        // String newphone = scanner.next();

        // String updateQuery = "UPDATE customers SET FirstName = ?, LastName = ?, Email
        // = ?, Phone = ? WHERE CustomerID = ?";
        // PreparedStatement newpreparedStatement = conn.prepareStatement(updateQuery);
        // newpreparedStatement.setString(1, newfirstname);
        // newpreparedStatement.setString(2, newlastname);
        // newpreparedStatement.setString(3, newemail);
        // newpreparedStatement.setString(4, newphone);
        // newpreparedStatement.setInt(5, searchcustomerID);

        // newpreparedStatement.executeUpdate();

        String input = textField.getText();
        if (input.equals("")) {
            return;
        }
        System.out.println(textField.getText());
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement("Update into todos (task) values (?) where id=?");
            statement.setString(1, input);
            statement.setInt(2, rowIndex);
            statement.executeUpdate();
            textField.setText("");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
