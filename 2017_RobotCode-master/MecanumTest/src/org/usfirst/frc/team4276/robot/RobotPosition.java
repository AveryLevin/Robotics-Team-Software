package org.usfirst.frc.team4276.robot;

public class RobotPosition {
	public double posX;
	public double posY;
	public double hdg;

	RobotPosition() {
		posX = 0.0;
		posY = 0.0;
		hdg = 0.0;
	}

	RobotPosition(double Xpos, double Ypos, double heading) {
		posX = Xpos;
		posY = Ypos;
		hdg = heading;
	}

	public String displayText() {
		return "(" + posX + ", " + posY + "), HDG: " + hdg;
	}

}
