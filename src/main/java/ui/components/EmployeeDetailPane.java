/*
 * @author Gavril
 * File: EmployeeDetailPane.java
 * This is the right side of the UI when you select someone, I really like the idea so im keeping it
 */

package com.motorph.ui.components;

import com.motorph.models.Employee;

import javax.swing.*;
import java.awt.*;

public class EmployeeDetailPane extends JEditorPane {
    private final String[] fullColumns;

    public EmployeeDetailPane(String[] fullColumns) {
        this.fullColumns = fullColumns;
        setContentType("text/html");
        setEditable(false);
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    public void showEmployee(Employee emp) {
        String[] data = emp.toArray();
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif; padding:10px;'>");
        html.append("<h2 style='margin-top:0;'>(◕‿◕) Employee Profile</h2><table cellpadding='4'>");
        for (int i = 0; i < fullColumns.length && i < data.length; i++) {
            html.append("<tr>")
                .append("<td style='font-weight:bold; color:#333;'>").append(fullColumns[i]).append("</td>")
                .append("<td>").append(data[i]).append("</td>")
                .append("</tr>");
        }
        html.append("</table></body></html>");
        setText(html.toString());
        setCaretPosition(0);
    }
}
