package org.usfirst.frc.team4276.robot;

public class Toggler {

	static int button;
	static boolean state = false;

	public Toggler(int joystickButton) {
		button = joystickButton;
	}

	static boolean currentButtonStatus = false;
	static boolean previousButtonStatus;

	void updateMechanismState() {
		previousButtonStatus = currentButtonStatus;
		currentButtonStatus = Robot.XBoxController.getRawButton(button);

		if (currentButtonStatus) {
			if (previousButtonStatus == false) {
				if (state == true)
					state = false;
				else if (state == false)
					state = true;
			}
		}
	}

	boolean getMechanismState(){
		return state;
	}
}
