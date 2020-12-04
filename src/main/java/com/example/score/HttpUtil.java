package com.example.score;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.EncryptedDocumentException;
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

import com.csvreader.CsvReader;

public class HttpUtil {

	public static void main(String[] args) throws IOException, EncryptedDocumentException, InvalidFormatException {
		

	        
			String excelTemplatePath="../electron-app/excel/gz.tpl.new.xlsx";
			//excelTemplatePath="/Users/alexwang/Downloads/祁连山sh600720.xlsx";
			
			String stockCode = "600519";
			System.out.println(new File(excelTemplatePath).getAbsolutePath());
			final FileInputStream inputStream2 = new FileInputStream(new File(excelTemplatePath));
			final Workbook workbook = WorkbookFactory.create(inputStream2);
			DataFormat format = workbook.createDataFormat();
			

			Map<String,String>tabMap = new LinkedMap<String,String>();
			tabMap.put("资产负债表", "zcfzb");
			tabMap.put("利润表", "lrb");
			tabMap.put("现金流量表", "xjllb");
			
			CellStyle yiStyle = workbook.createCellStyle();
			yiStyle.setDataFormat(format.getFormat("0\".\"00,,亿")); 
			CellStyle wStyle = workbook.createCellStyle();
			wStyle.setDataFormat(format.getFormat("0\".\"00,,万"));
			
			for (String tab : tabMap.keySet()) {
				
				final Sheet sheet = workbook.getSheet(tab);
				int rowId=0;
				

				String fileUrl="http://quotes.money.163.com/service/"+tabMap.get(tab)+"_"+stockCode+".html";
		        Connection connection = Jsoup.connect(fileUrl);
		        Connection.Response response = connection.method(Connection.Method.GET).ignoreContentType(true).timeout(3*1000).execute();
		        //BufferedInputStream bufferedInputStream = response.bodyStream();
		       //System.out.println( response.body());
		       
		        CsvReader csvReader = new CsvReader( response.bodyStream(), ',', Charset.forName("gb2312"));
		        csvReader.readHeaders();
		        String[] headers = csvReader.getHeaders();
		       // 
				Row newRow = sheet.createRow(rowId++);

		        for(int i=0;i<headers.length;i++) {
		        	Cell cell = newRow.createCell(i);
					cell.setCellValue( headers[i]);
		        }
		        

		        while (csvReader.readRecord()) {
					 newRow = sheet.createRow(rowId++);

		        	for(int i=0;i<headers.length;i++) {
			        	Cell cell = newRow.createCell(i);
			        	String val = csvReader.get(i);
			        	String colName = csvReader.get(0);
			        	
			        	if(i==0) val = colName.replace("(万元)", "");

			        	
			        	if(i>0&&colName.indexOf("(万元)")>-1 && isDouble(val)) {
			        		double dvalue = 10000*Double.parseDouble(val);
			        		
							cell.setCellValue(dvalue);
							if(Math.abs(dvalue)>100000000)cell.setCellStyle(yiStyle);
							else if(Math.abs(dvalue)>10000)cell.setCellStyle(wStyle);	
							
			        	}else cell.setCellValue(val);
		        	}


		        }
		        for(int i=0;i<headers.length;i++) {
					sheet.autoSizeColumn((short) (i));
		        }

			}
			workbook.setForceFormulaRecalculation(true);
			
			inputStream2.close();

			String outputFile = "gz.xlsx".replace(".xlsx", ".bak.xlsx");
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			workbook.write(outputStream);
			outputStream.close();
			workbook.close();

			System.out.println(new File(outputFile).getAbsolutePath());
			System.out.println("");
			System.out.println("Report generated: " + outputFile);

	}

	private static boolean isDouble(String val) {
		try {
			Double.parseDouble(val);
		}catch (Exception e) {
			return false;
		}
		return true;
	}

}
