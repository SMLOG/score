package com.example.score;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.jdbc.core.JdbcTemplate;

import com.csvreader.CsvReader;

public class HttpUtil {

	public static void main(String[] args) throws IOException, EncryptedDocumentException, InvalidFormatException {

	}

	public static void genReportExcel(String stockCode, JdbcTemplate jdbc, OutputStream outputStream2) throws Exception {
		String excelTemplatePath = "../electron-app/excel/gz.tpl.new.xlsx";
		// excelTemplatePath="/Users/alexwang/Downloads/祁连山sh600720.xlsx";

		String name = (String) jdbc.queryForList("select name from hq where code='"+stockCode.toLowerCase()+"'").get(0).get("name");
		System.out.println(new File(excelTemplatePath).getAbsolutePath());
		final FileInputStream inputStream2 = new FileInputStream(new File(excelTemplatePath));
		final Workbook workbook = WorkbookFactory.create(inputStream2);
		DataFormat format = workbook.createDataFormat();

		Map<String, String> tabMap = new LinkedMap<String, String>();
		tabMap.put("资产负债表", "zcfzb");
		tabMap.put("利润表", "lrb");
		tabMap.put("现金流量表", "xjllb");

		CellStyle yiStyle = workbook.createCellStyle();
		yiStyle.setDataFormat(format.getFormat("0\".\"00,,亿"));
		CellStyle wStyle = workbook.createCellStyle();
		wStyle.setDataFormat(format.getFormat("0\".\"00,,万"));

		CellStyle textStyle = workbook.createCellStyle();
		textStyle.setDataFormat(format.getFormat("@"));

		for (String tab : tabMap.keySet()) {

			final Sheet sheet = workbook.getSheet(tab);
			int rowId = 0;

			String fileUrl = "http://quotes.money.163.com/service/" + tabMap.get(tab) + "_"
					+ stockCode.toLowerCase().replaceAll("(sh)|(sz)", "") + ".html";
			Connection connection = Jsoup.connect(fileUrl);
			Connection.Response response = connection.method(Connection.Method.GET).ignoreContentType(true)
					.timeout(3 * 1000).execute();
			// BufferedInputStream bufferedInputStream = response.bodyStream();
			// System.out.println( response.body());

			CsvReader csvReader = new CsvReader(response.bodyStream(), ',', Charset.forName("gb2312"));
			csvReader.readHeaders();
			String[] headers = csvReader.getHeaders();
			//
			Row newRow = sheet.createRow(rowId++);
			int ci = 0;

			for (int i = 0; i < headers.length; i++) {

				Cell cell = newRow.createCell(ci++);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(textStyle);
				if (i == 0) {
					if (!headers[1].contains("12-31")) {
						cell = newRow.createCell(ci++);
						cell.setCellValue(headers[1].split("-")[0] + "-12-31");
						cell.setCellStyle(textStyle);
					}
				}
			}

			while (csvReader.readRecord()) {
				newRow = sheet.createRow(rowId++);
				String colName = csvReader.get(0);
				int scale = colName.indexOf("(万元)") > -1 ? 10000 : 1;
				colName = colName.replace("(万元)", "");
				ci = 0;
				Cell cell = newRow.createCell(ci++);
				cell.setCellValue(colName);
				if (!headers[1].contains("12-31")) {
					cell = newRow.createCell(ci++);

					if (!tabMap.get(tab).equals("zcfzb")) {
						double ttm = 0;
						double d1value = isDouble(csvReader.get(1)) ? scale * Double.parseDouble(csvReader.get(1)) : 0;
						double d4value = isDouble(csvReader.get(4)) ? scale * Double.parseDouble(csvReader.get(4)) : 0;
						double d5value = isDouble(csvReader.get(5)) ? scale * Double.parseDouble(csvReader.get(5)) : 0;
						ttm = d1value + d4value - d5value;
						cell.setCellValue(ttm);
						if (Math.abs(ttm) > 100000000)
							cell.setCellStyle(yiStyle);
						else if (Math.abs(ttm) > 10000)
							cell.setCellStyle(wStyle);
					} else {
						String val = csvReader.get(1);
						if (isDouble(val)) {
							double dvalue = scale * Double.parseDouble(val);

							cell.setCellValue(dvalue);
							if (Math.abs(dvalue) > 100000000)
								cell.setCellStyle(yiStyle);
							else if (Math.abs(dvalue) > 10000)
								cell.setCellStyle(wStyle);

						} else
							cell.setCellValue(val);

					}
				}

				for (int i = 1; i < headers.length; i++) {
					cell = newRow.createCell(ci++);
					String val = csvReader.get(i);

					System.out.print(val);
					System.out.print("\t");

					if (isDouble(val)) {
						double dvalue = scale * Double.parseDouble(val);

						cell.setCellValue(dvalue);
						if (Math.abs(dvalue) > 100000000)
							cell.setCellStyle(yiStyle);
						else if (Math.abs(dvalue) > 10000)
							cell.setCellStyle(wStyle);

					} else
						cell.setCellValue(val);

				}

				System.out.println("\n");

			}
			for (int i = 0; i <= sheet.getRow(0).getLastCellNum(); i++) {
				sheet.autoSizeColumn((short) (i));
			}

		}
		workbook.setForceFormulaRecalculation(true);

		Map<String, String> varsMap = new HashMap<String, String>();
		varsMap.put("_code", stockCode);
		varsMap.put("_name", name);
		ExcelUtil.extractValueFromExcelToDB(jdbc, stockCode, varsMap, workbook);

		inputStream2.close();

		String outputFile = "gz.xlsx".replace(".xlsx", "." + stockCode + name + ".xlsx");
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		workbook.write(outputStream);
		if(outputStream2!=null)workbook.write(outputStream2);
		outputStream.close();
		workbook.close();

		System.out.println(new File(outputFile).getAbsolutePath());
		System.out.println("");
		System.out.println("Report generated: " + outputFile);
	}

	private static boolean isDouble(String val) {
		try {
			Double.parseDouble(val);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
