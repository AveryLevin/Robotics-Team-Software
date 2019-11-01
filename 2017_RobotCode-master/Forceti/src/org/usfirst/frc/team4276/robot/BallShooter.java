package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;

import java.util.Arrays;

import edu.wpi.first.wpilibj.DigitalInput;

public class BallShooter {

	final int sampleAmounts = 18;
	private double[] rateSamples = new double[sampleAmounts];

	final double FEEDER_DELAY_TIME = 1.0; // seconds
	final double FEEDER_POWER = 1.0; // -1 to 1

	// final double AGITATOR_SPEED = 1.0;
	double FLYWHEEL_SPEED = 2600; // rpm
	double GAIN_PROPORTIONAL = 0.44e-3;
	double GAIN_INTEGRAL = 1.2e-3;
	double GAIN_DERIVATIVE = 350.0e-9;
	double FEED_FORWARD_K = 0.0;

	VictorSP shooterWheel;
	VictorSP feedingWheel;
	VictorSP agitator;
	static Encoder shooterEncoder;
	double assignedPower = 0.0;

	static double errorProportional;
	static double errorProportionalPrevious;
	static double errorIntegral;
	static double errorDerivative;
	static double timeNow;
	static double timePrevious;
	static double timeStep;
	static boolean initializePID = true;
	static boolean initializeShooter = true;
	boolean feederUp = false;
	boolean feederDown = false;
	boolean initializeEstimator = true;

	double ff_power = 0.0;
	DigitalInput feedforward;
	// Toggler shooterToggler;
	SoftwareTimer feederStartDelayTimer;

	public BallShooter(int pwm9, int pwm4, int dio8, int dio9) {

		shooterWheel = new VictorSP(pwm9);
		feedingWheel = new VictorSP(pwm4);
		// agitator = new VictorSP(pwm10);
		shooterEncoder = new Encoder(dio8, dio9); // encoder
		shooterEncoder.setDistancePerPulse(60.0 / 1024.0); // encoder RPM
		// shooterToggler = new Toggler(XBox.RTrigger);
		feederStartDelayTimer = new SoftwareTimer();

	}

	/*
	 * public BallShooter(int pwm9, int pwm4, int dio19, int dio20, int pwm10
	 * ,int dioFF) { testJoy = new Joystick(1); shooterWheel = new
	 * VictorSP(pwm9); feedingWheel = new VictorSP(pwm4); agitator = new
	 * VictorSP(pwm10); shooterEncoder = new Encoder(dio19,dio20); // encoder
	 * feedforward = new DigitalInput(dioFF);
	 * 
	 * shooterEncoder.setDistancePerPulse(60.0/1024.0); // encoder RPM
	 * //shooterToggler = new Toggler(XBox.RTrigger); feederStartDelayTimer =
	 * new SoftwareTimer(); rateSamples[0] = 0; rateSamples[1] = 0;
	 * rateSamples[2] = 0; rateSamples[3] = 0; rateSamples[4] = 0; }
	 */

	void updateGainsFromDriverInput() {

		if (Robot.testJoy.getRawButton(7))
			GAIN_PROPORTIONAL = GAIN_PROPORTIONAL - 1e-5;
		else if (Robot.testJoy.getRawButton(8))
			GAIN_PROPORTIONAL = GAIN_PROPORTIONAL + 1e-5;
		if (Robot.testJoy.getRawButton(9))
			GAIN_INTEGRAL = GAIN_INTEGRAL - 1e-5;
		else if (Robot.testJoy.getRawButton(10))
			GAIN_INTEGRAL = GAIN_INTEGRAL + 1e-5;
		if (Robot.testJoy.getRawButton(11))
			GAIN_DERIVATIVE = GAIN_DERIVATIVE - 1e-10;
		else if (Robot.testJoy.getRawButton(12))
			GAIN_DERIVATIVE = GAIN_DERIVATIVE + 1e-10;

		if (Robot.logitechJoystick.getRawButton(6))
			FLYWHEEL_SPEED = FLYWHEEL_SPEED + 1;
		else if (Robot.logitechJoystick.getRawButton(5))
			FLYWHEEL_SPEED = FLYWHEEL_SPEED - 1;

		SmartDashboard.putNumber("Kp*1000", GAIN_PROPORTIONAL * 1000);
		SmartDashboard.putNumber("Kd*1e9", GAIN_DERIVATIVE * 1e9);
		SmartDashboard.putNumber("Ki*1000", GAIN_INTEGRAL * 1000);
		SmartDashboard.putNumber("CommandedSpeed", FLYWHEEL_SPEED);
		SmartDashboard.putNumber("Sample Amounts", sampleAmounts);
	}

	void feederManualControl() {
		if (Robot.XBoxController.getPOV(XBox.DPad) == XBox.POVup) {
			feedingWheel.set(FEEDER_POWER);
			// agitator.set(AGITATOR_SPEED);
			feederUp = true;
			feederDown = false;
		} else if (Robot.XBoxController.getPOV(XBox.DPad) == XBox.POVdown) {
			feedingWheel.set(-FEEDER_POWER);
			// agitator.set(-AGITATOR_SPEED);
			feederUp = false;
			feederDown = true;
		} else {
			feedingWheel.set(0.0);
			// agitator.set(0.0);
			feederUp = false;
			feederDown = false;
		}
	}

	double computeFlyWheelPower(double currentRate) {
		// double assignedPower;

		if (initializePID == true) {
			timeNow = Robot.systemTimer.get();

			errorProportional = FLYWHEEL_SPEED - currentRate;
			errorIntegral = 0.0;
			assignedPower = 0.0;
			initializePID = false;
		} else {
			errorProportionalPrevious = errorProportional;
			timePrevious = timeNow;

			timeNow = Robot.systemTimer.get();

			/*
			 * if (!feedforward.get()) ff_power = FEED_FORWARD_K; else
			 * ff_power=0;
			 */

			timeStep = timeNow - timePrevious;
			errorProportional = FLYWHEEL_SPEED - currentRate;
			errorIntegral = errorIntegral + errorProportional * timeStep;
			errorDerivative = (errorProportional - errorProportionalPrevious) / timeStep;
			assignedPower = GAIN_PROPORTIONAL * errorProportional + GAIN_INTEGRAL * errorIntegral
					+ GAIN_DERIVATIVE * errorDerivative + ff_power;

			if (assignedPower > 1.0)
				assignedPower = 1.0;
			else if (assignedPower < -1.0)
				assignedPower = -1.0;
		}
		return assignedPower;
	}

	double estimateFlywheelRate() {

		if (initializeEstimator) {
			for (int j = sampleAmounts; j > 0; j--) {
				rateSamples[j - 1] = 0.0;
			}
			initializeEstimator = false;
		}

		int i;
		double medianRate;
		double currentRate = shooterEncoder.getRate();
		double[] sortedRates = new double[sampleAmounts];
		int middleIndex = sortedRates.length / 2;
		for (i = sampleAmounts - 1; i > 0; i--) {
			rateSamples[i] = rateSamples[i - 1];
		}
		rateSamples[0] = currentRate;

		sortedRates = Arrays.copyOf(rateSamples, rateSamples.length);
		Arrays.sort(sortedRates);
		// filteredRate = (rateSamples[0] + rateSamples[1] + rateSamples[2] +
		// rateSamples[3] + rateSamples[4]) / 5;
		if (sortedRates.length % 2 == 1) {
			medianRate = sortedRates[middleIndex];
		} else {
			medianRate = (sortedRates[middleIndex - 1] + sortedRates[middleIndex]) / 2;
		}
		return medianRate;
	}

	void performMainProcessing() {
		// Update PID gains and shooter setpoint (for test phase only)
		updateGainsFromDriverInput();

		double filteredRate = estimateFlywheelRate();

		// shooterToggler.updateMechanismState();
		// if (shooterToggler.getMechanismState()) {
		if (Robot.XBoxController.getRawAxis(XBox.RTrigger) > 0.5) {
			if (initializeShooter) {
				// if initializeShooter is true, then this if statement runs
				feederStartDelayTimer.setTimer(FEEDER_DELAY_TIME);
				initializeShooter = false;
			} else if (feederStartDelayTimer.isExpired()) {
				feedingWheel.set(FEEDER_POWER);
				// agitator.set(AGITATOR_SPEED);
				feederUp = true;
				feederDown = false;
			}
			assignedPower = computeFlyWheelPower(filteredRate);
			shooterWheel.set(assignedPower);
			LEDi2cInterface.shooting = true;
		} else {
			shooterWheel.set(0.0);
			feederManualControl();
			initializePID = true;
			initializeShooter = true;
			feederUp = false;
			feederDown = false;
			LEDi2cInterface.shooting = false;
		}
		double rawSpeed = shooterEncoder.getRate();
		SmartDashboard.putNumber("raw Speed", rawSpeed);

		SmartDashboard.putNumber("Shooter Speed", filteredRate);
		SmartDashboard.putBoolean("Feed Up:", feederUp);
		SmartDashboard.putBoolean("Feed Down:", feederDown);
	}

	void giveFeedback() {
		// SmartDashboard.putNumber("Shooter Speed", filteredRate);
		SmartDashboard.putNumber("Shooter Motor Power", assignedPower);
		SmartDashboard.putNumber("R Trigger", Robot.XBoxController.getRawAxis(XBox.RTrigger));
	}



	void stopFlywheel() {
		double filteredRate = 0.0;
		feedingWheel.set(0.0);
		assignedPower = 0.0;
		shooterWheel.set(assignedPower);
		LEDi2cInterface.shooting = false;

		SmartDashboard.putNumber("Shooter Speed", filteredRate);
		SmartDashboard.putString("AUTO SHOOT ERROR", "function off");
	}
	
	void startFlywheel() {

		/*
		 * double RobotXposition = mecanumNavigation.currentFieldX; double
		 * RobotYposition = mecanumNavigation.currentFieldY; double XRedBoiler =
		 * -27.17;// feet double YRedBoiler = -13.5;// feet double XBlueBoiler =
		 * 27.17;// feet double YBlueBoiler = 13.5;// feet double distanceToGoal
		 * = 0;// default
		 * 
		 * if (RobotXposition > 0) // blue alliance { distanceToGoal = Math
		 * .sqrt(Math.pow(XBlueBoiler - RobotXposition, 2) +
		 * Math.pow(YBlueBoiler - RobotYposition, 2)); } else if (RobotXposition
		 * < 0) // red alliance { distanceToGoal = Math
		 * .sqrt(Math.pow(XRedBoiler - RobotXposition, 2) + Math.pow(YRedBoiler
		 * - RobotYposition, 2)); } // distanceToGoal can be used to calculate
		 * speed of shooter
		 */
		double filteredRate = estimateFlywheelRate();

		//if (initializeShooter) {
			// if initializeShooter is true, then this if statement runs
			//feederStartDelayTimer.setTimer(FEEDER_DELAY_TIME);
			initializeShooter = false;
		//} else if (feederStartDelayTimer.isExpired()) {
			//feedingWheel.set(FEEDER_POWER);
			// agitator.set(AGITATOR_SPEED);
		//}

		assignedPower = computeFlyWheelPower(filteredRate);
		shooterWheel.set(assignedPower);
		LEDi2cInterface.shooting = true;

		SmartDashboard.putNumber("Shooter Speed", filteredRate);
		SmartDashboard.putString("AUTO SHOOT ERROR", "function on");
	}

	void stopFeeder() {

		
		LEDi2cInterface.shooting = false;
		feedingWheel.set(0);

		
		SmartDashboard.putString("Auto Feeder", "Off");
	}
	void startFeeder() {

		
		LEDi2cInterface.shooting = true;
		feedingWheel.set(FEEDER_POWER);

		
		SmartDashboard.putString("Auto Feeder", "On");
	}
}
