package com.motorph.ui.components;

import com.motorph.models.PayrollRecord;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class PayrollTableModel extends AbstractTableModel {
    private final String[] columns = {"Employee ID", "Employee Name", "Period", "Gross Pay", "Deductions", "Net Pay"};
    private List<PayrollRecord> payrolls;

    public PayrollTableModel(List<PayrollRecord> payrolls) {
        this.payrolls = payrolls;
    }

    public void setPayrolls(List<PayrollRecord> payrolls) {
        this.payrolls = payrolls;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return payrolls == null ? 0 : payrolls.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        PayrollRecord p = payrolls.get(row);
        switch (col) {
            case 0: return p.employeeId;
            case 1: return p.employeeName;
            case 2: return p.period;
            case 3: return p.grossPay;
            case 4: return p.deductions;
            case 5: return p.netPay;
            default: return "";
        }
    }

    public PayrollRecord getPayrollRecord(int row) {
        return payrolls.get(row);
    }
}
