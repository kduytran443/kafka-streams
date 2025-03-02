package com.learnkafkastreams.launcher;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FixExcelApp {

    public static void main(String[] args) {
        try {
            // Đọc file Excel từ thư mục resources
            File file = new File(FixExcelApp.class.getClassLoader().getResource("testcase_fix_hello.xlsx").getFile());
            FileInputStream fis = new FileInputStream(file);

            // Khởi tạo Workbook từ file Excel
            Workbook workbook = new XSSFWorkbook(fis);

            // Lấy sheet đầu tiên trong workbook
            Sheet sheet = workbook.getSheetAt(0);

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Set<String> duplicatedFormulas = new LinkedHashSet<>();
            // Duyệt qua các dòng và đọc giá trị trong cột A (cột 0)
            for (Row row : sheet) {
                if (row.getRowNum() < 12) {
                    continue;
                }
                // Lấy giá trị của ô trong cột A (chỉ số 0)
                Cell cell = row.getCell(0); // Cột A
                if (cell != null) {
                    // Kiểm tra nếu ô chứa công thức
                    if (cell.getCellType() == CellType.FORMULA) {
                        String originalFormula = cell.getCellFormula();  // Lấy công thức gốc
                        if (true || duplicatedFormulas.contains(originalFormula)) {
//                            System.out.println("Công thức gốc: " + originalFormula + " - Line " + cell.getRowIndex());
                            // Nếu công thức có MAX() trong đó, chúng ta có thể sửa lại
                            if (originalFormula.contains("MAX")) {
                                // Sửa công thức bằng cách gọi hàm updateFormula
                                String updatedFormula = updateFormula(originalFormula, row.getRowNum(), sheet);
//                                System.out.println("Công thức đổi: " + updatedFormula + " - Line " + cell.getRowIndex());
                                cell.setCellFormula(updatedFormula);  // Cập nhật công thức mới

                                if (duplicatedFormulas.contains(originalFormula)) {
                                    System.out.println("Duplicated change from " + originalFormula + " to " + updatedFormula);
                                } else {
                                    duplicatedFormulas.add(originalFormula);
                                }

                                // Tính toán lại giá trị công thức
                                formulaEvaluator.evaluateInCell(cell);  // Tính toán lại ô với công thức mới
                                double formulaResult = cell.getNumericCellValue();  // Lấy giá trị tính toán lại
                            }
                        } else {
//                            duplicatedFormulas.add(originalFormula);
                        }
                    } else {
                        // Nếu ô không phải là công thức, kiểm tra giá trị thông thường
                        if (cell.getCellType() == CellType.NUMERIC) {
                            double formulaResult = cell.getNumericCellValue();  // Lấy giá trị số học
//                            System.out.println("Giá trị số học trong ô: " + formulaResult);
                        }
                    }
                }
            }

            System.out.println("File path: " + file.getAbsolutePath());
            if (file.canWrite()) {
                System.out.println("Có quyền ghi vào file.");
            } else {
                System.out.println("Không có quyền ghi vào file.");
            }

            // Lưu lại workbook sau khi thay đổi
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);  // Lưu file Excel
            } catch (IOException e) {
                System.out.println("Có lỗi khi lưu file: " + e.getMessage());
                e.printStackTrace();
            }

            // Đóng workbook sau khi sử dụng
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hàm sửa lại công thức để thay đổi ô tham chiếu (ví dụ: A8 -> A9)
     * @param rowIndex Chỉ số dòng hiện tại
     * @param sheet Sheet đang làm việc
     * @return Công thức đã được sửa
     */
    private static String updateFormula(String originalFormula, int rowIndex, Sheet sheet) {
        // Tìm công thức MAX() phía trên vị trí ô trùng
        String column = "A";  // Chỉ có 1 cột A trong trường hợp này, bạn có thể tùy chỉnh cho các cột khác

        // Duyệt các dòng phía trên để tìm MAX() hợp lý
        while (rowIndex > 1) {  // Dòng 1 là tiêu đề, nên bắt đầu từ dòng 2 trở đi
            rowIndex--;  // Giảm chỉ số dòng để kiểm tra ô phía trên

            // Lấy ô ở dòng trên và kiểm tra công thức
            Row row = sheet.getRow(rowIndex);  // Dòng 1 sẽ là row 0
            if (row != null) {
                Cell cell = row.getCell(0);  // Cột A
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    String updatedReference = column + (rowIndex + 1);  // Tham chiếu ô mới
                    return "MAX(" + updatedReference + ")+1";  // Sửa công thức
                }
            }
        }

        // Nếu không tìm thấy MAX(), trả về công thức gốc (nếu có)
        return originalFormula;  // Bạn có thể chọn công thức mặc định khác, hoặc thông báo lỗi
    }
}
