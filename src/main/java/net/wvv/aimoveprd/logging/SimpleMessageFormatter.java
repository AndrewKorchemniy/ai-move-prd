package net.wvv.aimoveprd.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleMessageFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + System.lineSeparator();
    }
}