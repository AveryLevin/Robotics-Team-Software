package org.usfirst.frc.team4276.robot;

public class SoftwareTimer {

	private double expirationTime;

	void setTimer(double timerValue) {
		expirationTime = Robot.systemTimer.get() + timerValue;
	}

	boolean isExpired() {
		return (Robot.systemTimer.get() > expirationTime);
		// if robotTime is greater than expirationTime, then this boolean is
		// true
	}
}
