package pl.kk.services.reporting.service;

import java.util.Arrays;
import java.util.List;

public class HTMLTableBuilder {

    private int columns;
    private StringBuilder table;
    private static final String HTML_START = "<html>";
    private static final String HTML_END = "</html>";
    private static final String TABLE_START_BORDER = "<table border=\"1\">";
    private static final String TABLE_START = "<table>";
    private static final String TABLE_END = "</table>";
    private static final String HEADER_START = "<th>";
    private static final String HEADER_END = "</th>";
    private static final String ROW_START = "<tr>";
    private static final String ROW_END = "</tr>";
    private static final String COLUMN_START = "<td>";
    private static final String COLUMN_END = "</td>";

    private HTMLTableBuilder() {

    }

    public static HTMLTableBuilder getInstance(String title, boolean border, int columns) {
        HTMLTableBuilder builder = new HTMLTableBuilder();
        builder.columns = columns;
        builder.table = new StringBuilder();
        if (title != null) {
            builder.table.append("<b>");
            builder.table.append(title);
            builder.table.append("</b>");
        }
        builder.table.append(HTML_START);
        builder.table.append(border ? TABLE_START_BORDER : TABLE_START);
        builder.table.append(TABLE_END);
        builder.table.append(HTML_END);
        return builder;
    }

    public void addTableHeader(String... values) {
        if (values.length != columns) {
            throw new IllegalStateException("Column length differs from number of provided values");
        } else {
            addTableHeaderDo(Arrays.asList(values));
        }
    }

    public void addTableHeader(List<String> values) {
        if (values.size() != columns) {
            throw new IllegalStateException("Column length differs from number of provided values");
        } else {
            addTableHeaderDo(values);
        }
    }

    public void addRowValues(List<?> values) {
        if (values.size() != columns) {
            throw new IllegalStateException("Column length differs from number of provided values");
        } else {
            addRowValuesDo(values);
        }
    }

    private void addTableHeaderDo(List<?> values) {
        int lastIndex = table.lastIndexOf(TABLE_END);
        if (lastIndex > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(ROW_START);
            for (Object value : values) {
                sb.append(HEADER_START);
                sb.append(value);
                sb.append(HEADER_END);
            }
            sb.append(ROW_END);
            table.insert(lastIndex, sb.toString());
        }
    }

    private void addRowValuesDo(List<?> values) {
        int lastIndex = table.lastIndexOf(ROW_END);
        if (lastIndex > 0) {
            int index = lastIndex + ROW_END.length();
            StringBuilder sb = new StringBuilder();
            sb.append(ROW_START);
            for (Object value : values) {
                sb.append(COLUMN_START);
                sb.append(value);
                sb.append(COLUMN_END);
            }
            sb.append(ROW_END);
            table.insert(index, sb.toString());
        }
    }


    public String build() {
        return table.toString();
    }


}