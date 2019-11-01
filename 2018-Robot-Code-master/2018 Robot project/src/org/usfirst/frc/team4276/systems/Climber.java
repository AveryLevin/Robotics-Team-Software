package org.usfirst.frc.team4276.systems;

import org.usfirst.frc.team4276.robot.Robot;
import org.usfirst.frc.team4276.utilities.Xbox;
import org.usfirst.frc.team4276.utilities.Toggler;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber {
	DoubleSolenoid lockingPin;
	Toggler lockingToggler;
	DoubleSolenoid.Value ENGAGED = Value.kForward;
	DoubleSolenoid.Value DISENGAGED = Value.kReverse;
	boolean climberIsLocked = false;
	final double LT_THRESHOLD = 0.5;

	public Climber(int PnuematicPort1, int PnuematicPort2) {
		lockingPin = new DoubleSolenoid(PnuematicPort1, PnuematicPort2);
		lockingToggler = new Toggler(Xbox.LT);
	}

	public void performMainProcessing() {
		lockingToggler.updateMechanismState(LT_THRESHOLD);
		boolean togglerState = lockingToggler.getMechanismState();
		if (togglerState) {
			// when climber is on climbInProgress = true (bookkept)
			lockingPin.set(ENGAGED);
			climberIsLocked = true;

		} else {
			// when climber is off climbInProgress = false (bookkept)
			lockingPin.set(DISENGAGED);
			climberIsLocked = false;
		}
		updateTelemetry();
	}

	public void updateTelemetry() {
		SmartDashboard.putBoolean("Climber Locked", climberIsLocked);
	}
}