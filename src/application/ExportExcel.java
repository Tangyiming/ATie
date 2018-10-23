package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.ObservableList;

public class ExportExcel {

	@SuppressWarnings("deprecation")
	public void Export(File file, String string, ObservableList<TableModel> lt) throws IOException {
		String path = file.getName();
		if (path.endsWith("xls")) {
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("sheet1");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("用例名");
			cell.setCellStyle(style);
			cell = row.createCell((short) 1);
			cell.setCellValue("AT指令");
			cell.setCellStyle(style);
			cell = row.createCell((short) 2);
			cell.setCellValue("预期");
			cell.setCellStyle(style);
			cell = row.createCell((short) 3);
			cell.setCellValue("返回");
			cell = row.createCell((short) 4);
			cell.setCellValue("测试结果");
			cell.setCellStyle(style);
			for (int i = 0; i < lt.size(); i++) {
				row = sheet.createRow((int) i + 1);
				TableModel tm = (TableModel) lt.get(i);
				row.createCell((short) 0).setCellValue(tm.getCasename());
				row.createCell((short) 1).setCellValue(tm.getATCommand());
				row.createCell((short) 2).setCellValue(tm.getExpection());
				row.createCell((short) 3).setCellValue(tm.getResponse());
				row.createCell((short) 4).setCellValue(tm.getResult());
			}
			FileOutputStream fout = new FileOutputStream(file);
			wb.write(fout);
			fout.close();
			wb.close();
		} else if (path.endsWith("xlsx")) {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("sheet1");
			XSSFRow row = sheet.createRow((int) 0);
			XSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			XSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("用例名");
			cell.setCellStyle(style);
			cell = row.createCell((short) 1);
			cell.setCellValue("AT指令");
			cell.setCellStyle(style);
			cell = row.createCell((short) 2);
			cell.setCellValue("预期");
			cell.setCellStyle(style);
			cell = row.createCell((short) 3);
			cell.setCellValue("返回");
			cell = row.createCell((short) 4);
			cell.setCellValue("测试结果");
			cell.setCellStyle(style);
			for (int i = 0; i < lt.size(); i++) {
				row = sheet.createRow((int) i + 1);
				TableModel tm = (TableModel) lt.get(i);
				row.createCell((short) 0).setCellValue(tm.getCasename());
				row.createCell((short) 1).setCellValue(tm.getATCommand());
				row.createCell((short) 2).setCellValue(tm.getExpection());
				row.createCell((short) 3).setCellValue(tm.getResponse());
				row.createCell((short) 4).setCellValue(tm.getResult());
			}
			FileOutputStream fout = new FileOutputStream(file);
			wb.write(fout);
			fout.close();
			wb.close();
		}
	}
}
