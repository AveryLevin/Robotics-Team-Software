package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallCollector {

	static final double COLLECTOR_SPEED = 1.0; // -1.0 to 1.0

	VictorSP ballCollector;

	Toggler collectorToggler;

	public BallCollector(int pwm7) {
		ballCollector = new VictorSP(pwm7);
		collectorToggler = new Toggler(XBox.RB);
	}

	void performMainProcessing() {
		collectorToggler.updateMechanismState();

		if (collectorToggler.getMechanismState()) {
			ballCollector.set(COLLECTOR_SPEED);
		} else {
			ballCollector.set(0.0);
		}

		// SmartDashboard.putBoolean("Collector", collecting);

	}

}