package org.usfirst.frc.team4276.robot;

public class SoftwareTimer {

	static double expirationTime;

	static double robotTime;

	void setTimer(double timerValue) {
		robotTime = Robot.systemTimer.get();
		expirationTime = robotTime + timerValue;
		/*
		 * I replaced the Robot.systemTimer.get() in that equation with the
		 * double robotTime
		 * 
		 * @Brian
		 */
	}

	boolean isExpired() {
		return (robotTime > expirationTime);
		// if robotTime is greater than expirationTime, then this boolean is
		// true
	}
}
