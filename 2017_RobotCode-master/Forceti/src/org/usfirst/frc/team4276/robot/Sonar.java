package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sonar {

	Ultrasonic sonar;
	private double range;

	public Sonar(int dioTrigger, int dioEcho) {
		sonar = new Ultrasonic(dioTrigger, dioEcho);// dio
		sonar.setAutomaticMode(true);
	}

	public double getRangeFeet() {
		range = sonar.getRangeInches() / 12.0; // reads the range on the
												// ultrasonic sensor in feet
		return range;
	}
}
