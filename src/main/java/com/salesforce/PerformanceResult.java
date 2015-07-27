package com.salesforce;

import java.util.HashMap;

class PerformanceResult {
	String phoenix_version;
	HashMap<String, HashMap<String, Timing>> testResults = new HashMap<String, HashMap<String, Timing>>();
}

class Timing {
	String explainPlan;
	String category;
	long duration;
	
	Timing(Long duration, String explainPlan) {
		this.explainPlan = explainPlan;
		this.duration = duration;
	}
}
