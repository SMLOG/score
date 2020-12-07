package com.example.score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.score.common.Holder;
import com.example.score.common.Info;
import com.example.score.common.TechMan;
import com.example.score.common.TechTypeEnum;
import com.example.score.data.CacheMan;
import com.example.score.data.Item;

@SpringBootTest
public class ScoreApplicationTests4 {

	@Autowired
	private JdbcTemplate jdbc;

	@Test
	void contextLoads() throws Exception {
		
		List<Map<String, Object>> result = jdbc.queryForList("select * from hq where pe_ttm>0");
     for(Map<String, Object> item:result) {
 		HttpUtil.genReportExcel((String) item.get("code"),(String) item.get("name"), jdbc);

     }
	}



}
