package pers.zlf.plugin.action.export;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.DataType;
import com.intellij.database.util.DasUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.util.ExcelUtil;
import pers.zlf.plugin.util.lambda.Empty;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/10 18:17
 */
public abstract class BaseExport {
    private final List<String> headerList = List.of("序号", "主键", "字段", "注释", "类型", "默认值", "不可为null");

    /**
     * 导出文件
     *
     * @param path 文件路径
     */
    public void exportXlsx(String path) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            String fileName = dealWorkbook(workbook);
            try (FileOutputStream outputStream = new FileOutputStream(Path.of(path, fileName + FileType.XLSX_FILE).toString())) {
                workbook.write(outputStream);
            }
            Message.showMessage(Message.EXPORT_SUCCESS);
        } catch (Exception e) {
            Message.showMessage(e.getMessage());
        }
    }


    /**
     * 处理workbook
     *
     * @param workbook excel
     * @return 文件名
     */
    protected abstract String dealWorkbook(XSSFWorkbook workbook);

    /**
     * 创建Sheet
     *
     * @param workbook excel
     * @param dbTable  待处理的表
     */
    protected void createSheet(XSSFWorkbook workbook, DasObject dasTable) {
        String title = dasTable.getName() + Empty.of(dasTable.getComment()).map(t -> Common.LEFT_PARENTHESES + t + Common.RIGHT_PARENTHESES).orElse(Common.BLANK_STRING);
        Sheet sheet = workbook.createSheet(dasTable.getName());
        int rowNum = 0;
        //插入标题数据
        Row titleRow = sheet.createRow(rowNum++);
        ExcelUtil.createCell(titleRow, 0, title, ExcelUtil.titleCellStyle(workbook));
        ExcelUtil.addMergedRegion(sheet, 0, 0, 0, headerList.size() - 1);
        //插入表头数据
        Row headerRow = sheet.createRow(rowNum++);
        CellStyle headerCellStyle = ExcelUtil.headerCellStyle(workbook);
        IntStream.range(0, headerList.size()).forEach(i -> ExcelUtil.createCell(headerRow, i, headerList.get(i), headerCellStyle));
        //插入主体数据
        CellStyle commonStyle = ExcelUtil.contentCellStyle(workbook);
        int serialNumber = 0;
        for (DasColumn column : DasUtil.getColumns(dasTable)) {
            Row row = sheet.createRow(rowNum++);
            ExcelUtil.createCell(row, 0, String.valueOf(serialNumber++), commonStyle);
            ExcelUtil.createCell(row, 1, DasUtil.isPrimary(column) ? Common.HOOK_UP : Common.BLANK_STRING, commonStyle);
            ExcelUtil.createCell(row, 2, column.getName(), commonStyle);
            ExcelUtil.createCell(row, 3, Optional.ofNullable(column.getComment()).orElse(Common.BLANK_STRING), commonStyle);
            DataType dataType = column.getDasType().toDataType();
            String type = dataType.typeName;
            if (dataType.size > 0) {
                type = type + Common.LEFT_PARENTHESES + dataType.size;
                if (dataType.scale > 0) {
                    type = type + Common.COMMA + dataType.scale;
                }
                type = type + Common.RIGHT_PARENTHESES;
            }
            ExcelUtil.createCell(row, 4, type, commonStyle);
            ExcelUtil.createCell(row, 5, Optional.ofNullable(column.getDefault()).orElse(Common.BLANK_STRING), commonStyle);
            ExcelUtil.createCell(row, 6, column.isNotNull() ? Common.HOOK_UP : Common.BLANK_STRING, commonStyle);
            //自适应高度
            row.setHeight((short) -1);
        }
        //自适应宽度
        for (int i = 0; i < headerList.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
