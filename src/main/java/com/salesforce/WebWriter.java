package com.salesforce;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * HTML writer - Sorry, a lot hard coded stuff here :(
 * @author mchohan
 *
 */
public class WebWriter {

	private static StringBuilder sb = new StringBuilder();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	protected static void start() {
		sb.append("<html><title>Phoenix SNAPSHOT Performance</title>" +
				"<head><style type=\"text/css\">table.myTable { border-collapse:collapse; } " +
				"table.myTable tr { padding:5px;} " +
				"table.myTable td, table.myTable th { font-size:9px; } </style>" +
				"</head>" +
				"<body style=\"font-family:verdana;\">"
				);
		sb.append("<font size=5 color='#448899'>Apache Phoenix Performance Result :: " + new Date() + "</font><br>");
	}
	
	private static String[] colors_warning = {"#CC0000","#BB0000","#CC0000", "#BB0000","#CC0000", "#BB0000","#CC0000","#BB0000","#CC0000", "#BB0000","#CC0000", "#BB0000"};
	private static String[] colors_dim = {"#D5E5D5","#ADEDAD","#ADEDAD", "#75E575","#75E575", "#DDEDDD","#D5E5D5","#ADEDAD","#ADEDAD", "#75E575","#75E575", "#DDEDDD"};
	
	protected static void addGraph(String test, String[] versions, Timing[] values, long maxValue) {
		int colorCount = 0;
		boolean warning = false;
		String font = "<font size=1 color='"+(warning ? "red":"black")+"'>";
		String[] colors = warning ? colors_warning : colors_dim;

		sb.append("<tr>");
		sb.append("<td>" + font);
		sb.append("" + test + "");
		
		if (maxValue == 0) {
			for (Timing value : values) {
				maxValue = value.duration > maxValue ? value.duration
						: maxValue;
			}
		}

		DecimalFormat df = new DecimalFormat("#.##");
		
		for (int i=0; i<values.length; i++) {
			
			int barWidth = (int)(((double)values[i].duration/(double)maxValue)*700);
			if (values[i].duration != -3) {
			sb.append("<table width='1000' cellspacing='0px'>");
			sb.append("<tr>");
			if (values[i].duration == -1) {
				sb.append("<td><font size=1 color='gray'>N/A for "+ versions[i].toUpperCase() +"</font></td>");
			} else if (values[i].duration == -2) {
				sb.append("<td><font size=1 color='gray'>SQL Exception - Check logs for "+ versions[i].toUpperCase() +"</font></td>");
			}else if (values[i].duration == -3) {
				sb.append("<td><font size=1 color='gray'>Exception - Check logs for "+ versions[i].toUpperCase() +"</font></td>");
			} else {
				sb.append("<td bgcolor='"+colors[colorCount++]+"' width='"+ barWidth +"' height='10'");
				sb.append("</td>");
				sb.append("<td>" + font + df.format((double)values[i].duration/1000) + "s</font><font size=1 color='gray'> <span title='"+values[i].explainPlan.replace("'", "") +"'>"+ versions[i].toUpperCase() +"</span></font></td>");
				sb.append("</font>");
			}
			sb.append("</tr>");
			sb.append("</table>");
			}
		}
		sb.append("<br></td>");
		sb.append("</tr>");
	}

	protected static void writeCategoryDetails(int size, String category) {
		sb.append("<hr>");
		sb.append("<font size="+size+" color='#333355'><i>" + category + "</i></font>");
		sb.append("<br><br>");
	}
	
	protected static void writeTestDetails(PerformanceTest performanceTest) {
		sb.append("<hr>");
		sb.append("<font size=3 color='#335566'><h4 id='"+performanceTest.TABLE+"'><b>" + performanceTest.TABLE + "</b></h4></font>");
		sb.append("<font size=1>");
		if (performanceTest.DESCRIPTION != null) {
			sb.append("<br><font color=gray><i>" + performanceTest.DESCRIPTION + "</i></font>");
		}
		sb.append("<br><br><b>ROWS</b> " + performanceTest.ROW_COUNT + " [" + performanceTest.ROW_COUNT/1000000 + "M]");
		sb.append("<br><br><b>DDL</b> " + performanceTest.CREATE_DDL.replace("\n", ""));
		
		sb.append("<br><br><b>DATA GENERATOR</b><br>");
		
		for (PerformanceTest.Field field: performanceTest.FIELDS) {
				sb.append(field.toString() + "<br>");
		}
		sb.append("<br>");
		sb.append("</font>");
	}
	
	protected static void stop(String path) throws IOException {
		sb.append("<br><hr>");
		sb.append("<font size=1>Note: Hover over version label to see <i>Phoenix Query Explain Plan</i>.</font>");
		sb.append("</html>");
		FileUtils.writeStringToFile(new File(path + "/phoenix-" + sdf.format(new Date()) + ".htm"), sb.toString());
		FileUtils.writeStringToFile(new File(path + "/latest.htm"), sb.toString());
	}

	public static void startGraph() {
		sb.append("<table class=\"myTable\"");
	}
	
	public static void stopGraph() {
		sb.append("</table>");
	}

	public static void updateIndex(String publishPath) throws IOException, ParseException {
		StringBuilder idx = new StringBuilder();
		idx.append("<html><title>Phoenix Performance Results</title><body style=\"font-family:verdana;\"><h2>Phoenix Performance</h2>Last Updated " + new Date() + "<hr><br>");
		
        File folder = new File(publishPath);
		File[] arrayOfFiles = folder.listFiles(); 
		
		List<String> files = new ArrayList<String>();
		
		for (File file: arrayOfFiles) {
			files.add(file.getName());
		}
		
		Collections.sort(files, Collections.reverseOrder());
		boolean boldFirst = true;
		
		for (String file: files) {
			if (file.startsWith("phoenix")) {
				idx.append("<a href=" + file + ">" + (boldFirst ? "<b>" : "") + sdf.parse(file.replace("phoenix-", "").replace(".htm", "")).toString() + (boldFirst ? "</b>" : "") + "</a><br>");
				boldFirst = false;
			}
		}

		idx.append("</body></html>");
		FileUtils.writeStringToFile(new File(publishPath + "/index.htm"), idx.toString());
	}
}
