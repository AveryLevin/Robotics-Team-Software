package org.usfirst.frc.team4276.robot;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class RoutePlan extends ArrayList<RouteTask> {

	public String name;

	RoutePlan(String planName) {
		name = planName;
	}

	public String displayText() {
		String sRet = name + "\n";
		for (int i = 0; i < size(); i++) {
			RouteTask myTask = get(i);
			sRet += myTask.displayText();
		}
		return sRet;
	}
}
