package org.usfirst.frc.team4276.utilities;

import org.usfirst.frc.team4276.robot.Robot;

public class Toggler {

	private int button;
	private boolean state = false;
	private boolean currentButtonStatus = false;
	private boolean previousButtonStatus;

	public Toggler(int joystickButton) {
		button = joystickButton;
	}

	public void updateMechanismState(double triggerValue) {
		previousButtonStatus = currentButtonStatus;
		if (triggerValue > 0) {
			currentButtonStatus = (Robot.xboxController.getRawAxis(button) >= triggerValue);

		} else {
			currentButtonStatus = (Robot.xboxController.getRawAxis(button) <= triggerValue);
		}
		if (currentButtonStatus == true) {
			if (previousButtonStatus == false) {
				if (state == true) {
					state = false;
				} else {
					state = true;
				}
			}
		}

	}

	public void updateMechanismState(int DpadState) {
		previousButtonStatus = currentButtonStatus;
		currentButtonStatus = (DpadState == Robot.xboxController.getPOV(button));
		if (currentButtonStatus == true) {
			if (previousButtonStatus == false) {
				if (state == true) {
					state = false;
				} else {
					state = true;
				}
			}
		}
	}

	public void updateMechanismState() {
		previousButtonStatus = currentButtonStatus;
		currentButtonStatus = Robot.xboxController.getRawButton(button);
		if (currentButtonStatus == true) {
			if (previousButtonStatus == false) {
				if (state == true) {
					state = false;
				} else {
					state = true;
				}
			}
		}
	}
	
	public void setMechanismState(boolean desiredState) {
		state = desiredState;
	}

	public boolean getMechanismState() {
		return state;
	}
}
