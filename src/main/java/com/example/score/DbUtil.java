package com.example.score;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;

public class DbUtil {
	public static int createTable(JdbcTemplate jt, String tableName, Map<String, Object> obj) {
		StringBuffer sb = new StringBuffer("");
		sb.append("CREATE TABLE `" + tableName + "` (");
		sb.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");

		for (String key : obj.keySet()) {
			if (obj.get(key) instanceof Double)
				sb.append("`" + key + "` DOUBLE(10,2) DEFAULT NULL,");
			else if (obj.get(key) instanceof String) {
				sb.append("`" + key + "` varchar(10) default null,");
			}
		}
		sb.append(" PRIMARY KEY (`id`)");
		sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		try {
			System.out.println(sb.toString());
			jt.update(sb.toString());
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int saveObj(JdbcTemplate jt, String tableName, Map<String, Object> obj) throws Exception {
		int re = 0;
		StringBuilder sb = new StringBuilder();
		if (!getAllTableName(jt, tableName)) {
			createTable(jt, tableName, obj);
		}

		try {

			sb.append(" insert into " + tableName + " (");

			for (String key : obj.keySet()) {
				sb.append(key).append(",");
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(") ");

			sb.append(" values(");

			for (String key : obj.keySet()) {
				if (obj.get(key) instanceof String)
					sb.append("'" + obj.get(key) + "',");
				else
					sb.append(obj.get(key)).append(",");

			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append(")");

			re = jt.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return re;
	}

	@SuppressWarnings("unchecked")
	public static boolean getAllTableName(JdbcTemplate jt, String tableName) throws Exception {
		Connection conn = jt.getDataSource().getConnection();
		ResultSet tabs = null;
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();
			String[] types = { "TABLE" };
			tabs = dbMetaData.getTables(null, null, tableName, types);
			if (tabs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tabs.close();
			conn.close();
		}
		return false;
	}
}