package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallCollector {

	static final double COLLECTOR_SPEED = 1.0; // -1.0 to 1.0

	VictorSP ballCollector;
	boolean ballCollecting = false;
	Toggler collectorToggler;
	Joystick collectorJoy;

	public BallCollector(int pwm8) {
		ballCollector = new VictorSP(pwm8);
		collectorToggler = new Toggler(XBox.B);
		collectorJoy = new Joystick(3);
	}

	void performMainProcessing() {
		collectorToggler.updateMechanismState();

		if (collectorToggler.getMechanismState()) {
			ballCollector.set(COLLECTOR_SPEED);
			ballCollecting = true;
		} else {
			ballCollector.set(0.0);
			ballCollecting = false;
		}

		 SmartDashboard.putBoolean("Collector", ballCollecting);

	}
	
	void pushButtonBallIntake(){
		if(collectorJoy.getRawButton(XBox.B))
		{
			ballCollector.set(COLLECTOR_SPEED);
		}
		else{
			ballCollector.set(0);
		}
	}

}