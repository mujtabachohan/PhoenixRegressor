package com.salesforce;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * HTML result generator main class
 * @author mchohan
 *
 */
public class ResultGenerator {
	private static List<PerformanceResult> performanceResultsList = new ArrayList<PerformanceResult>();
	
	public static void publish(TestSettings ts) throws JsonSyntaxException, IOException, ParseException {
        File folder = new File(ts.RESULT);
		File[] listOfFiles = folder.listFiles(); 
		
		for (File resultFile: listOfFiles) {
			if (resultFile.isFile()) 
				loadResults(resultFile);
		}

		WebWriter.start();
		generateResultHTMLByCategory(ts);
		WebWriter.stop(ts.PUBLISH_PATH);
		WebWriter.updateIndex(ts.PUBLISH_PATH);
	}

	private static void loadResults(File resultFile) throws JsonSyntaxException, IOException {
		PerformanceResult performanceResult = new Gson().fromJson(
				FileUtils.readFileToString(new File(resultFile.toString())),
				PerformanceResult.class);
		performanceResult.phoenix_version = resultFile.getName().replace(".result", "");
		performanceResultsList.add(performanceResult);
	}

	private static void generateResultHTMLByCategory(TestSettings ts) throws JsonSyntaxException, IOException {
        File folder = new File(ts.TEST);
		File[] listOfFiles = folder.listFiles(); 
		Timing[] results = new Timing[performanceResultsList.size()];
		String[] versions = new String[performanceResultsList.size()];
		List<String> queries = new ArrayList<String>();
		List<String> files = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();

		for (File testFile: listOfFiles) {
			if (testFile.isFile()) {
				PerformanceTest performanceTest = new Gson().fromJson(FileUtils.readFileToString(testFile),PerformanceTest.class);
				for (PerformanceTest.Query query : performanceTest.QUERIES) {
					if (!categories.contains(query.Category))
						categories.add(query.Category);
				}
			}
		}
		categories.add("Load Data");
		
		Collections.sort(categories);
		
		for (File testFile: listOfFiles) {
			if (testFile.isFile())
				files.add(testFile.toString());
		}
		Collections.sort(files);

		
		long maxValue = 0;
		if (performanceResultsList.size() == 1) {
			maxValue = 500*100;
		}
		
		WebWriter.writeCategoryDetails(5, "Test Suite: " + ts.TEST.toUpperCase() + "<br/><br/> Queries by Category");
		for (String category : categories) {
			WebWriter.writeCategoryDetails(4,category);
			
			for (String file: files) {
				PerformanceTest performanceTest = new Gson().fromJson(FileUtils.readFileToString(new File(file)),PerformanceTest.class);
			
				queries.clear();
				for (PerformanceTest.Query query : performanceTest.QUERIES) {
					if (query.Category.equals(category))
						queries.add(query.Sql.replace("$TABLE", performanceTest.TABLE));
				}
				if (category.equals("Load Data"))
					queries.add(performanceTest.getLoadString());
	
				WebWriter.startGraph();
				for (String query : queries) {
					System.out.print(query + " :: ");
					for (int i = 0; i < performanceResultsList.size(); i++) {
						PerformanceResult perfResult = performanceResultsList.get(i);
						Timing val = perfResult.testResults.get(performanceTest.TABLE).get(query);
						System.out.print(perfResult.phoenix_version + ":" + val.duration + ", ");
						versions[i] = perfResult.phoenix_version;
						results[i] = val;
					}
	
					System.out.println("");
					WebWriter.addGraph(query.replace(performanceTest.TABLE, "<a href='#"+performanceTest.TABLE+"'>" + performanceTest.TABLE+"</a>"), versions, results, maxValue);
				}
				WebWriter.stopGraph();
			}
		}
		
		WebWriter.writeCategoryDetails(5, "<br><br>Queries by Table");
		for (String file: files) {
			PerformanceTest performanceTest = new Gson().fromJson(FileUtils.readFileToString(new File(file)),PerformanceTest.class);
			WebWriter.writeTestDetails(performanceTest);
			
			queries.clear();
			for (PerformanceTest.Query query : performanceTest.QUERIES) {
				queries.add(query.Sql.replace("$TABLE", performanceTest.TABLE));
			}
			queries.add(performanceTest.getLoadString());

			WebWriter.startGraph();
			for (String query : queries) {
				System.out.print(query + " :: ");
				for (int i = 0; i < performanceResultsList.size(); i++) {
					PerformanceResult perfResult = performanceResultsList.get(i);
					Timing val = perfResult.testResults.get(performanceTest.TABLE).get(query);
					System.out.print(perfResult.phoenix_version + ":" + val	+ ", ");
					versions[i] = perfResult.phoenix_version;
					results[i] = val;
				}

				System.out.println("");
				WebWriter.addGraph(query, versions, results, maxValue);
			}
			WebWriter.stopGraph();
		}
	}

}
