package com.salesforce;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.phoenix.util.CSVCommonsLoader;

public class DataGenerator {

	protected static final String TABLE_ALIAS = "$TABLE";
	protected static final String CSV_FOLDER = "csv";
	
	public static void createTable(String tableName, String createTableDDL) {
		createTableDDL = createTableDDL.replace(TABLE_ALIAS, tableName);
		String[] createTableDDLSplit = createTableDDL.split(";");
		
		for (String ddl: createTableDDLSplit) {
			executeStatement(ddl);
		}
	}
	
	public static void executeStatement(String ddl) {
		try {
			System.out.println("EXECUTING DDL: " + ddl);
			Connection conn = PhoenixConnection.getConnection();
			PreparedStatement statement = conn.prepareStatement(ddl);
			statement.execute();
			conn.commit();
			statement.close();
			conn.close();
		} 
		catch (SQLException ex) {
			System.out.println(ex.toString());
		}
	}
	
	public static void generateData(String tableName, int rowCount, PerformanceTest.Field[] fields) throws IOException {
		String csvFileName = CSV_FOLDER + "/" + tableName + ".csv";
		if (new File(csvFileName).exists()) {
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = GregorianCalendar.getInstance();
		String line = "";
		Random rand = new Random();
		FileOutputStream fostream = new FileOutputStream(csvFileName);
		
		for (int i = 0; i < rowCount; i++) {
			for (PerformanceTest.Field field : fields) {
				if (field.values != null) {
					line += field.values[i % field.values.length];
				} else {
					switch (field.fieldType) {
					case DECIMAL:
						switch (field.distribution) {
						case SEQUENTIAL:
							line += "1." + (i + rand.nextInt(10)) + RandomStringUtils.randomNumeric(field.length);
							break;
						case RANDOM:
							line += rand.nextInt(field.max_value);
							break;
						}
						break;
					case INTEGER:
						switch (field.distribution) {
						case SEQUENTIAL:
							line += i;
							break;
						case RANDOM:
							line += rand.nextInt(field.max_value);
							break;
						}
						break;
					case DATE:
						switch (field.distribution) {
						case SEQUENTIAL:
							now.add(Calendar.SECOND, 1);
							line += sdf.format(now.getTime());
							break;
						case RANDOM:
							//TODO
							break;
						}
						break;
					case STRING:
						switch (field.distribution) {
						case SEQUENTIAL:
							if (field.max_value > 0) {
								line += StringUtils.leftPad((i % field.max_value) + "", field.length, 'x');
							} else {
								line += StringUtils.leftPad(i + "", field.length, 'x');
							}
							break;
						case RANDOM:
							if (field.max_value > 0) {
								line += StringUtils.leftPad(rand.nextInt(field.max_value) + "", field.length, 'x');
							} else {
								line += RandomStringUtils.randomAlphanumeric(field.length);
							}
							break;
						}
						break;
					}
				}
				line += ",";
			}

            if (i % 100000 == 0) {
                System.out.print(".");
                if (i % 1000000 == 0) {
                    if (i != 0) System.out.print((i/1000000) + "M");
                }
            } 

			line = line.substring(0, line.length()-1);			
			//System.out.println(line);
			line += "\n";
			fostream.write(line.getBytes());
			line = "";
		}
		
		fostream.close();
	}
	
	public static void gatherStats(String tableName) throws InterruptedException {
		System.out.println("\nStarting gathering stats for " + tableName + " ...");
		Thread.sleep(5000);
		executeStatement("update statistics " + tableName);
		Thread.sleep(10000);
	}
	
	public static long upsertData(String tableName) throws Exception {
		org.apache.phoenix.util.CSVCommonsLoader csvLoader = new CSVCommonsLoader((org.apache.phoenix.jdbc.PhoenixConnection) PhoenixConnection.getConnection(), tableName, null, true);
		long start = System.currentTimeMillis();
		csvLoader.upsert(CSV_FOLDER + "/" + tableName + ".csv");
		long ret = System.currentTimeMillis() - start;

		tableName = tableName.toUpperCase();
		gatherStats(tableName);
		return ret;
	}
}
