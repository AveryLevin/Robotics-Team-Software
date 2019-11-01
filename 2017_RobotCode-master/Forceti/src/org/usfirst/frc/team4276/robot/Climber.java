package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {

	VictorSP climber;

	final static double LIMIT_SWITCH_DELAY = 1.0; // seconds
	final static double CLIMBER_POWER_FAST = 1.0; // -1.0 to 1.0
	final static double CLIMBER_POWER_SLOW = .25;
	final static double RELEASE_POWER = -.40;
	boolean climbSlow=false;
	boolean climbFast=false;
	boolean climbRev = false;

	public Climber(int pwm5) {

		climber = new VictorSP(pwm5);
		
	}

	void driveInReverse() {

		if ((Robot.XBoxController.getRawAxis(XBox.RStickY) > 0.5)) {
			
				climber.set(RELEASE_POWER);
				climbFast = false;
				climbSlow = false;
				climbRev = true;
		}

		else {
			climber.set(0.0);
			climbFast = false;
			climbSlow = false;
			climbRev = false;
		}
		// driveInReverse();
		SmartDashboard.putBoolean("Climb Fast:", climbFast);
		SmartDashboard.putBoolean("Climb Slow:", climbSlow);
		SmartDashboard.putBoolean("Climb Reverse:", climbRev);
	}

	void performMainProcessing() {

		if (Robot.XBoxController.getRawAxis(XBox.RStickY) < -0.5) {

			if (Robot.XBoxController.getRawButton(XBox.RStick) == true) {
				climber.set(CLIMBER_POWER_FAST);
				climbFast = true;
				climbSlow = false;
				climbRev = false;
			}
			else
			{
				climber.set(CLIMBER_POWER_SLOW);
				climbFast = false;
				climbSlow = true;
				climbRev = false;
			}
			LEDi2cInterface.climbing = true;
		}

		else {
			climber.set(0.0);
			climbFast = false;
			climbSlow = false;
			climbRev = false;
			LEDi2cInterface.climbing = false;
		}
		// driveInReverse();
		SmartDashboard.putBoolean("Climb Fast:", climbFast);
		SmartDashboard.putBoolean("Climb Slow:", climbSlow);
		SmartDashboard.putBoolean("Climb Reverse:", climbRev);
	}
}
