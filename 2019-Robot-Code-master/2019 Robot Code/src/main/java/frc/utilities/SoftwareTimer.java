package frc.utilities;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

public class SoftwareTimer {

	private double expirationTime = 0;

	public void setTimer(double timerValue) {
		expirationTime = Robot.systemTimer.getFPGATimestamp() + timerValue;
		SmartDashboard.putNumber("TIME", Robot.systemTimer.getFPGATimestamp());
	}

	public boolean isExpired() {
		return (Robot.systemTimer.getFPGATimestamp() > expirationTime);
		// if robotTime exceeds expirationTime, then this returns true
	}
}
