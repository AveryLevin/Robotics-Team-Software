package frc.systems;

import edu.wpi.first.wpilibj.VictorSP;

import com.ctre.phoenix.CANifier.LEDChannel;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.systems.LEDControl.LightMode;
import frc.utilities.Xbox;

public class BallLift {

	Value lowPosition = Value.kForward;
	Value highPosition = Value.kReverse;
	double backPowerShoot = .5;
	double backPowerSpit = .4;
	double frontPower = -.7;
	double collectPower = -.7;
	boolean goingHi = false;
	boolean goingLo = false;
	boolean goingOut = false;
	boolean isCollecting = false;

	VictorSPX backroller, frontroller;
	DigitalInput intakeSwitch;
	DoubleSolenoid diverter;

	public BallLift(int backPort, int frontPort, int divertA, int B, int limSwitch) {
		intakeSwitch = new DigitalInput(limSwitch);
		backroller = new VictorSPX(backPort);
		frontroller = new VictorSPX(frontPort);
		diverter = new DoubleSolenoid(divertA, B);
		diverter.set(highPosition);
	}

	public void performMainProcessing() {

		if (Robot.xboxJoystick.getRawButton(Xbox.LB)) {
			collect(collectPower);
			//Robot.mLED.setMode(LightMode.INTAKE);
		} else if (Robot.xboxJoystick.getRawButton(Xbox.A)) {
			reverse();
			//Robot.mLED.setMode(LightMode.ENABLE);
		} else if (Robot.xboxJoystick.getRawButton(Xbox.B)) {
			lowScore();
			//Robot.mLED.setMode(LightMode.SHOOT);
		} else if (Robot.xboxJoystick.getRawButton(Xbox.Y)) {
			highScore();
			//Robot.mLED.setMode(LightMode.SHOOT);
		} else {
			stop();
			//Robot.mLED.setMode(LightMode.ENABLE);
		}
		updateTelemetry();
	}

	public void lowScore() {

		diverter.set(lowPosition);
		frontroller.set(ControlMode.PercentOutput, frontPower);
		backroller.set(ControlMode.PercentOutput, backPowerSpit);

		goingHi = false;
		goingLo = true;
		goingOut = false;
		isCollecting = false;

	}

	public void highScore() {

		diverter.set(highPosition);
		frontroller.set(ControlMode.PercentOutput, frontPower);
		backroller.set(ControlMode.PercentOutput, backPowerShoot);

		goingHi = true;
		goingLo = false;
		goingOut = false;
		isCollecting = false;
	}

	public void reverse() {

		frontroller.set(ControlMode.PercentOutput, -frontPower);
		backroller.set(ControlMode.PercentOutput, -backPowerShoot);

		goingHi = false;
		goingLo = false;
		goingOut = true;
		isCollecting = false;

	}

	public void stop() {

		diverter.set(highPosition);
		frontroller.set(ControlMode.PercentOutput, 0);
		backroller.set(ControlMode.PercentOutput, 0);
		Robot.xboxJoystick.setRumble(RumbleType.kLeftRumble, 0.0);

		goingHi = false;
		goingLo = false;
		goingOut = false;
		isCollecting = false;
	}

	public void exStop() {

		goingHi = false;
		goingLo = false;
		goingOut = false;
		isCollecting = false;
	}

	private void collect(double power) {
		isCollecting = true;
		// diverter.set(highPosition);
		/*
		 * if (intakeSwitch.get()) { frontroller.set(ControlMode.PercentOutput, 0);
		 * Robot.xboxJoystick.setRumble(RumbleType.kLeftRumble, 0.5); } else {
		 */
		frontroller.set(ControlMode.PercentOutput, power);
		backroller.set(ControlMode.PercentOutput, power);
		Robot.xboxJoystick.setRumble(RumbleType.kLeftRumble, 0.0);
		// }

	}

	public void collect() {
		isCollecting = true;
	}

	public void updateTelemetry() {
		SmartDashboard.putBoolean("High Goal:", goingHi);
		SmartDashboard.putBoolean("Low Goal:", goingLo);
		SmartDashboard.putBoolean("Outake:", goingOut);
		SmartDashboard.putBoolean("Has Ball", intakeSwitch.get());

		SmartDashboard.putBoolean("Collecting:", isCollecting);
	}

}
