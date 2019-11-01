package org.usfirst.frc.team4276.robot;

public class RobotPositionPolar {
	public boolean isBlueBoiler;
	public double radius;
	public double hdgToBoiler; // 0.0 = Field frame north
	public double yawOffsetRobot; // 0.0 = hdgToBoiler

	// Note that we don't need strictly need a flag to indicate blue/red boiler.
	// hdgToBoiler angle for the blue end is 0.0 to 90.0, and for blue end it is
	// 90.0 to 180.0
	//
	// isBlueBoiler is included to provide a hint for where to start a search
	// for the vision target if you have an idea where you were before

	RobotPositionPolar(boolean isBlue, double radBoiler, double hdgBoiler, double yawRobot) {
		isBlueBoiler = isBlue;
		radius = radBoiler;
		hdgToBoiler = hdgBoiler;
		yawOffsetRobot = yawRobot;
	}

	public RobotPositionPolar() {
		isBlueBoiler = false;
		radius = 0.0;
		hdgToBoiler = 0.0;
		yawOffsetRobot = 0.0;
	}

	RobotPosition positionXY() {
		RobotPosition retVal = new RobotPosition();

		// Polar coordinate angle at boiler origin is opposite from hdgToBoiler
		double polarAngle = hdgToBoiler + 180.0;
		if (polarAngle > 360.0) {
			polarAngle -= 360.0;
		}

		double boiler_X = RoutePlanList.redBoiler_X;
		double boiler_Y = RoutePlanList.redBoiler_Y;
		if (hdgToBoiler < 90.0) {
			boiler_X = RoutePlanList.blueBoiler_X;
			boiler_Y = RoutePlanList.blueBoiler_Y;
		}

		retVal.posX = boiler_X + (radius * Math.cos(polarAngle));
		retVal.posY = boiler_Y + (radius * Math.sin(polarAngle));
		retVal.hdg = hdgToBoiler + yawOffsetRobot;

		return retVal;
	}

	double hdgTo(RobotPositionPolar there) {
		// yawOffsetRobot does not affect this calculation - this is the
		// direction to move the center of the robot to get to another place
		RobotPosition posHere = positionXY();
		RobotPosition posThere = there.positionXY();

		return (90.0 - (180.0 / Math.PI) * Math.atan2(posThere.posY - posHere.posY, posThere.posX - posHere.posX));
	}

	public String displayText() {
		RobotPosition posHere = positionXY();
		return "(" + posHere.posX + ", " + posHere.posY + "), HDG: " + posHere.hdg;
	}

	public String displayTextPolar() {
		return "[ rad: " + radius + ", hdg: " + hdgToBoiler + "], yaw: " + yawOffsetRobot;
	}

	public double distanceTo(RobotPositionPolar endPos) {
		RobotPosition posHere = positionXY();
		RobotPosition posThere = endPos.positionXY();
		return Math.sqrt((posHere.posX - posThere.posX) * (posHere.posX - posThere.posX)
				+ (posHere.posY - posThere.posY) * (posHere.posY - posThere.posY));
	}
}
