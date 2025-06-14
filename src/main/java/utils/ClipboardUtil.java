package com.motorph.utils;

import java.awt.*;

public class ClipboardUtil {
    public static void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new java.awt.datatransfer.StringSelection(text), null);
    }
}
