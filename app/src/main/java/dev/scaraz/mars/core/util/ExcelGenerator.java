package dev.scaraz.mars.core.util;

import dev.scaraz.mars.common.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

public class ExcelGenerator implements AutoCloseable {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final XSSFWorkbook workbook;

    public ExcelGenerator() {
        this(new XSSFWorkbook());
    }

    public ExcelGenerator(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void write(OutputStream os) throws IOException {
        workbook.write(os);
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    public SheetGenerator createSheet(String name) {
        return new SheetGenerator(this, name);
    }

    public SheetGenerator createSheet() {
        return new SheetGenerator(this);
    }

    public XSSFCellStyle createCellStyle() {
        return workbook.createCellStyle();
    }

    public XSSFFont createFont() {
        return workbook.createFont();
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public static class SheetGenerator {

        private final ExcelGenerator generator;
        private final XSSFSheet sheet;

        public ExcelGenerator getGenerator() {
            return generator;
        }

        public SheetGenerator(ExcelGenerator generator, String name) {
            Assert.isTrue(StringUtils.isNotBlank(name), "sheet name cannot be null");
            this.generator = generator;
            this.sheet = generator.workbook.createSheet(name);
        }

        public SheetGenerator(ExcelGenerator generator) {
            this.generator = generator;
            this.sheet = generator.workbook.createSheet();
        }

        public RowGenerator createRow(int rowIndex) {
            return new RowGenerator(this, rowIndex);
        }

        public void autoSizeColumn(int colIndex) {
            this.sheet.autoSizeColumn(colIndex);
        }

    }

    public static class RowGenerator {

        private final SheetGenerator sheet;
        private final XSSFRow row;

        public RowGenerator(SheetGenerator generator, int rowIndex) {
            this.sheet = generator;
            this.row = generator.sheet.createRow(rowIndex);
        }

        public int getRowNum() {
            return row.getRowNum();
        }

        public SheetGenerator getSheet() {
            return sheet;
        }


        public XSSFCell createCell(int colIndex, String value) {
            if (value == null) return createEmptyCell(colIndex);
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, String value) {
            if (value == null) return createEmptyCell(colIndex, style);
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellStyle(style);
            cell.setCellValue(value);
            return cell;
        }


        public XSSFCell createCell(int colIndex, Integer value) {
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Integer value) {
            if (value == null) return createEmptyCell(colIndex, style);
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellValue(value);
            cell.setCellStyle(style);
            return cell;
        }


        public XSSFCell createCell(int colIndex, Double value) {
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Double value) {
            if (value == null) return createEmptyCell(colIndex, style);
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellValue(value);
            cell.setCellStyle(style);
            return cell;
        }


        public XSSFCell createCell(int colIndex, Long value) {
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Long value) {
            if (value == null) return createEmptyCell(colIndex, style);
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellValue(value);
            cell.setCellStyle(style);
            return cell;
        }


        public XSSFCell createCell(int colIndex, Enum<?> value) {
            return createCell(colIndex, null, value.name());
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Enum<?> value) {
            if (value == null) return createEmptyCell(colIndex, style);
            return createCell(colIndex, style, value.name());
        }


        public XSSFCell createCell(int colIndex, Instant value) {
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Instant value) {
            if (value == null)
                return createEmptyCell(colIndex, style);
            else
                return createCell(colIndex, style, value.atZone(ZONE_LOCAL).format(DATE_TIME_FORMAT));
        }


        public XSSFCell createCell(int colIndex, boolean value) {
            return createCell(colIndex, null, value ? "Y" : "N");
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, boolean value) {
            return createCell(colIndex, style, value ? "Y" : "N");
        }


        public XSSFCell createCell(int colIndex, Duration value) {
            return createCell(colIndex, null, value);
        }

        public XSSFCell createCell(int colIndex, XSSFCellStyle style, Duration value) {
            if (value == null) return createEmptyCell(colIndex);
            return createCell(colIndex, style, Util.durationDescribe3Segment(value));
        }


        public XSSFCell createEmptyCell(int colIndex) {
            return createEmptyCell(colIndex, null);
        }

        public XSSFCell createEmptyCell(int colIndex, XSSFCellStyle style) {
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellStyle(style);
            cell.setCellValue((String) null);
            return cell;
        }

    }

}
