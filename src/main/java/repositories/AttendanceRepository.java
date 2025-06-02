/*
 * @author Gav
 * File: AttendanceRepository.java
 * Notes: File Management for Attendance.csv
 * IMPORTANT: CALL HERE INSTEAD OF DIRETCLY TO FILE
 */

package com.motorph.repositories;

import com.motorph.models.AttendanceRecord;
import com.motorph.utils.DataPathHelper;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class AttendanceRepository {

    public static List<AttendanceRecord> getAll() {
        List<AttendanceRecord> records = new ArrayList<>();
        File attendanceFile = DataPathHelper.getAttendanceFile();

        try (
            FileReader fr = new FileReader(attendanceFile);
            CSVReader csvReader = new CSVReader(fr)
        ) {
            String[] row;
            boolean firstLine = true;
            while ((row = csvReader.readNext()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (row.length >= 6) {
                    records.add(new AttendanceRecord(row));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
}
