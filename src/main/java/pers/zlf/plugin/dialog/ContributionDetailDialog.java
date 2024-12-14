package pers.zlf.plugin.dialog;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.FileType;
import pers.zlf.plugin.constant.Message;
import pers.zlf.plugin.pojo.ContributionDetail;
import pers.zlf.plugin.util.ExcelUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author zhanglinfeng
 * @date create in 2024/12/12 21:36
 */
public class ContributionDetailDialog {
    private final String FILE_NAME = "文件名";
    private final String TOTAL_LINE_COUNT = "总行数";
    private final String TOTAL = "合计";
    private final String CODE_COUNT = "代码";
    private final String COMMENT_COUNT = "注释";
    private final String EMPTY_LINE_COUNT = "空行";
    private final String KEYWORD_COUNT = "关键字";

    /** 统计数据 */
    private final Map<String, Map<String, ContributionDetail>> totalContributionDetailMap;
    /** git账号 */
    private final Map<String, String> gitMap = new HashMap<>();
    /** 表头 */
    private final List<String> headerList;
    /** ui组件 */
    private JPanel contentPanel;
    private JBTable contributionDetailTable;
    private JButton exportButton;

    public ContributionDetailDialog(Map<String, Map<String, ContributionDetail>> totalContributionDetailMap) {
        this.totalContributionDetailMap = totalContributionDetailMap;
        totalContributionDetailMap.values().forEach(t -> t.values().forEach(v -> gitMap.put(v.getEmail(), v.getEmailAndUser())));
        headerList = getHeaderList(gitMap);
        init();
        initButtonListener();
    }

    private void init() {
        //TODO 合并表头
        int columnCount = headerList.size();
        DefaultTableModel defaultTableModel = new DefaultTableModel(null, headerList.toArray()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Map<String, ContributionDetail> totalMap = new HashMap<>();
        Integer total = 0;
        for (Map.Entry<String, Map<String, ContributionDetail>> fileDetailMapEntry : totalContributionDetailMap.entrySet()) {
            Map<String, ContributionDetail> detailMap = fileDetailMapEntry.getValue();
            String[] rowData = new String[columnCount];
            rowData[0] = fileDetailMapEntry.getKey();
            rowData[1] = String.valueOf(detailMap.values().stream().mapToInt(ContributionDetail::getTotalCount).sum());
            total = total + Integer.parseInt(rowData[1]);
            int columnNum = 2;
            for (String email : gitMap.keySet()) {
                ContributionDetail detail = detailMap.getOrDefault(email, new ContributionDetail());
                rowData[columnNum++] = detail.getCodeCount() + Common.COMMA + detail.getCommentCount() + Common.COMMA + detail.getEmptyLineCount() + Common.COMMA + detail.getKeywordCount();
                ContributionDetail totalDetail = totalMap.getOrDefault(email, new ContributionDetail());
                totalDetail.add(detail);
                totalMap.putIfAbsent(email, totalDetail);
            }
            defaultTableModel.addRow(rowData);
        }
        //合计
        String[] totalRow = new String[columnCount];
        totalRow[0] = TOTAL;
        totalRow[1] = String.valueOf(total);
        int columnNum = 2;
        for (String email : gitMap.keySet()) {
            ContributionDetail detail = totalMap.getOrDefault(email, new ContributionDetail());
            totalRow[columnNum++] = detail.getCodeCount() + Common.COMMA + detail.getCommentCount() + Common.COMMA + detail.getEmptyLineCount() + Common.COMMA + detail.getKeywordCount();
        }
        defaultTableModel.addRow(totalRow);
        contributionDetailTable.setModel(defaultTableModel);
    }

    private void initButtonListener() {
        //导出
        exportButton.addActionListener(e -> {
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
            Optional.ofNullable(virtualFile).map(VirtualFile::getPath).ifPresent(this::export);
        });
    }

    public JPanel getContent() {
        return this.contentPanel;
    }

    private List<String> getHeaderList(Map<String, String> emailMap) {
        List<String> headerList = new LinkedList<>();
        headerList.add(FILE_NAME);
        headerList.add(TOTAL_LINE_COUNT);
        headerList.addAll(emailMap.values());
        return headerList;
    }

    private void export(String path) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(Common.CODE_STATISTICS);
            int rowNum = 0;
            //插入表头
            Row firstHeaderRow = sheet.createRow(rowNum++);
            Row secondHeaderRow = sheet.createRow(rowNum++);
            CellStyle headerCellStyle = ExcelUtil.headerCellStyle(workbook);
            IntStream.range(0, headerList.size()).forEach(i -> {
                if (i < 2) {
                    ExcelUtil.createCell(firstHeaderRow, i, headerList.get(i), headerCellStyle);
                    ExcelUtil.createCell(secondHeaderRow, i, headerList.get(i), headerCellStyle);
                } else {
                    ExcelUtil.createCell(firstHeaderRow, 4 * i - 6, headerList.get(i), headerCellStyle);
                    ExcelUtil.createCell(secondHeaderRow, 4 * i - 6, CODE_COUNT, headerCellStyle);
                    ExcelUtil.createCell(firstHeaderRow, 4 * i - 5, headerList.get(i), headerCellStyle);
                    ExcelUtil.createCell(secondHeaderRow, 4 * i - 5, COMMENT_COUNT, headerCellStyle);
                    ExcelUtil.createCell(firstHeaderRow, 4 * i - 4, headerList.get(i), headerCellStyle);
                    ExcelUtil.createCell(secondHeaderRow, 4 * i - 4, EMPTY_LINE_COUNT, headerCellStyle);
                    ExcelUtil.createCell(firstHeaderRow, 4 * i - 3, headerList.get(i), headerCellStyle);
                    ExcelUtil.createCell(secondHeaderRow, 4 * i - 3, KEYWORD_COUNT, headerCellStyle);
                }
            });
            //合并表头
            ExcelUtil.addMergedRegion(sheet, 0, 1, 0, 0);
            ExcelUtil.addMergedRegion(sheet, 0, 1, 1, 1);
            IntStream.range(0, headerList.size()).forEach(i -> {
                if (i > 1) {
                    ExcelUtil.addMergedRegion(sheet, 0, 0, 4 * i - 6, 4 * i - 3);
                }
            });
            //插入主体数据
            CellStyle commonStyle = ExcelUtil.contentCellStyle(workbook);
            for (Map.Entry<String, Map<String, ContributionDetail>> fileDetailMapEntry : totalContributionDetailMap.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                Map<String, ContributionDetail> detailMap = fileDetailMapEntry.getValue();
                ExcelUtil.createCell(row, 0, fileDetailMapEntry.getKey(), commonStyle);
                ExcelUtil.createCell(row, 1, detailMap.values().stream().mapToInt(ContributionDetail::getTotalCount).sum(), commonStyle);
                int columnNum = 2;
                for (String email : gitMap.keySet()) {
                    ContributionDetail detail = detailMap.getOrDefault(email, new ContributionDetail());
                    ExcelUtil.createCell(row, columnNum++, detail.getCodeCount(), commonStyle);
                    ExcelUtil.createCell(row, columnNum++, detail.getCommentCount(), commonStyle);
                    ExcelUtil.createCell(row, columnNum++, detail.getEmptyLineCount(), commonStyle);
                    ExcelUtil.createCell(row, columnNum++, detail.getKeywordCount(), commonStyle);
                }
                //自适应高度
                row.setHeight((short) -1);
            }
            // 合计
            Row totalRow = sheet.createRow(rowNum);
            ExcelUtil.createCell(totalRow, 0, TOTAL, commonStyle);
            int finalRowNum = rowNum - 1;
            IntStream.range(1, firstHeaderRow.getLastCellNum()).forEach(i -> {
                Cell cell = ExcelUtil.createCell(totalRow, i, 0, commonStyle);
                String cellReference = new CellReference(0, i).formatAsString();
                cellReference = cellReference.substring(0, cellReference.length() - 1);
                cell.setCellFormula("SUM(" + cellReference + "3:" + cellReference + finalRowNum + ")");
            });
            //自适应宽度
            for (int i = 0; i < headerList.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            try (FileOutputStream outputStream = new FileOutputStream(Path.of(path, Common.CODE_STATISTICS + FileType.XLSX_FILE).toString())) {
                workbook.write(outputStream);
            }
            Message.showMessage(Message.EXPORT_SUCCESS);
        } catch (Exception e) {
            Message.showMessage(e.getMessage());
        }
    }
}