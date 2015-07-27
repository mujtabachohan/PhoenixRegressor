package com.salesforce;

/**
 * Json representation of Performance Test
 * @author mchohan
 *
 */
class PerformanceTest {
	public String TABLE;
	public String DESCRIPTION;
	public String CREATE_DDL;
	public int ROW_COUNT;
	public Field[] FIELDS;
	public Query[] QUERIES;
	
	PerformanceTest() {
	}
	
	public class Field {
		public FieldType fieldType;
		public int length;
		public int max_value;
		public Distribution distribution;
		public String[] values;
		
		@Override 
		public String toString() {
			String vals = "";
			
			if (values != null) {
				vals = "Values: ";
				for (String value : values) {
					vals += value + "|";
				}
				
				if (vals.length() > 1) {
					vals = vals.substring(0, vals.length() -1);
				}
			}
			
			return   fieldType + " :: " + 
					(length != 0 ? " Length: " + length : "")+
					(max_value != 0 ? " Max. Value: " + max_value : "")+
					(distribution != null ? " Values: " + distribution : "") + " "+
					vals;
		}
		
	}

	public String getLoadString() {
		return "LOAD DATA " + TABLE + " [" + ROW_COUNT + " ROWS]";
	}
	
	public class Query {
		public String Sql;
		public String Category;
	}
	
	public enum Distribution {
		RANDOM, SEQUENTIAL
	}
	
	public enum FieldType {
		INTEGER, STRING, DATE, DECIMAL
	}
}
