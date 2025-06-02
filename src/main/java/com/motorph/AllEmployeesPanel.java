package com.motorph;

/*
* author @Gav pogi
inupdate ko na yung UI based sa request, wag niyo na galawan ty
*/
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;

public class AllEmployeesPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JEditorPane detailPane;
    private JTextField searchField;

// moved the column from the JTable to the Full View 
    private final String[] fullColumns = {
        "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate"
    };

    public AllEmployeesPanel() {
        setLayout(new BorderLayout());
// Nakita ko lang to sa tutorial na paano gawin uneditable ang field para hindi nakakainis
        model = new DefaultTableModel(new Object[]{"Employee #", "Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                } else {
                    comp.setBackground(new Color(184, 207, 229));
                }
                return comp;
            }
        };

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        loadEmployeeData();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String[] fullData = getFullRowData(selectedRow);
                    showEmployeeDetails(fullData);
                }
            }
        });

        detailPane = new JEditorPane();
        detailPane.setContentType("text/html");
        detailPane.setEditable(false);
        detailPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        detailPane.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        topPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e -> openEmployeeDialog(null));
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                openEmployeeDialog(getFullRowData(selectedRow));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        });

        deleteBtn.addActionListener(e -> deleteSelectedRow());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
		
		// split-view (left = list) (right = full view)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), new JScrollPane(detailPane));
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(440);
        splitPane.setDividerSize(6);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
	 // paano natin icoconvert to csv?
    private void loadEmployeeData() {
        model.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("employee_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fullRow = line.split("\t");
                if (fullRow.length >= 11) {
                    String empId = fullRow[0];
                    String name = fullRow[2] + " " + fullRow[1];
                    String status = fullRow[10];
                    model.addRow(new Object[]{empId, name, status});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filter() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
        table.setRowSorter(sorter);
    }

    private String[] getFullRowData(int selectedRow) {
        String empId = (String) table.getValueAt(selectedRow, 0);
        try (BufferedReader br = new BufferedReader(new FileReader("employee_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fullRow = line.split("\t");
                if (fullRow[0].equals(empId)) {
                    return fullRow;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[fullColumns.length];
    }
	//Right panel when selecting employee
	//lagay niyo lang tong kaomoji
    private void showEmployeeDetails(String[] data) {
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif; padding:10px;'>");
        html.append("<h2 style='margin-top:0;'>(◕‿◕) Employee Profile</h2><table cellpadding='4'>");
        for (int i = 0; i < fullColumns.length && i < data.length; i++) {
            html.append("<tr>")
                .append("<td style='font-weight:bold; color:#333;'>").append(fullColumns[i]).append("</td>")
                .append("<td>").append(data[i]).append("</td>")
                .append("</tr>");
        }
        html.append("</table></body></html>");
        detailPane.setText(html.toString());
        detailPane.setCaretPosition(0);
    }

    private void openEmployeeDialog(String[] data) {
        JTextField[] fields = new JTextField[fullColumns.length];
        JPanel panel = new JPanel(new GridLayout(fullColumns.length, 2, 4, 4));

        for (int i = 0; i < fullColumns.length; i++) {
            panel.add(new JLabel(fullColumns[i]));
            fields[i] = new JTextField(data != null && i < data.length ? data[i] : "");
            panel.add(fields[i]);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, data == null ? "Add Employee" : "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] newData = new String[fullColumns.length];
            for (int i = 0; i < fullColumns.length; i++) {
                newData[i] = fields[i].getText();
            }

            saveOrUpdateEmployee(newData, data != null ? data[0] : null);
        }
    }

    private void saveOrUpdateEmployee(String[] newData, String oldEmpId) {
        File file = new File("employee_data.txt");
        Vector<String> updatedLines = new Vector<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean updated = false;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts[0].equals(oldEmpId)) {
                    updatedLines.add(String.join("\t", newData));
                    updated = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (!updated) {
                updatedLines.add(String.join("\t", newData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadEmployeeData();
    }
 /// Hindi ko alam paano ayusin tong section na mag eerror kung kulang sa ng input sa field
    private void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        String empId = (String) table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Employee #" + empId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        File file = new File("employee_data.txt");
        Vector<String> updatedLines = new Vector<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (!parts[0].equals(empId)) {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadEmployeeData();
    }
}