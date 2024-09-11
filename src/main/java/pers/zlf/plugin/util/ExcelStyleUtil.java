package pers.zlf.plugin.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import pers.zlf.plugin.constant.Common;

/**
 * excel样式工具类
 *
 * @author zhanglinfeng
 * @date create in 2023/6/5 10:20
 */
public class ExcelStyleUtil {

    public static CellStyle titleCellStyle(Workbook wb) {
        return defaultCellStyle(wb, (short) 14);
    }

    public static CellStyle headerCellStyle(Workbook wb) {
        return defaultCellStyle(wb, (short) 12);
    }

    public static CellStyle contentCellStyle(Workbook wb) {
        return defaultCellStyle(wb, (short) 11);
    }

    public static CellStyle defaultCellStyle(Workbook wb, short size) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        Font basicFont = wb.createFont();
        basicFont.setFontName(Common.FONT_WRYH);
        basicFont.setBold(true);
        basicFont.setFontHeightInPoints(size);
        cellStyle.setFont(basicFont);
        return cellStyle;
    }

    public static void createCell(Row row, int cellNum, String cellValue, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
    }

    public static void addMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
        sheet.addMergedRegion(cra);
    }
}
