
package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.SampleRobot;

import java.util.ArrayList;

import org.usfirst.frc.team4276.robot.PIXY.Frame;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.I2C.Port;

/**
 * This is a demo program showing the use of the RobotDrive class. The
 * SampleRobot class is the base of a robot application that will automatically
 * call your Autonomous and OperatorControl methods at the right time as
 * controlled by the switches on the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're
 * inexperienced, don't. Unless you know what you are doing, complex code will
 * be much more difficult under this system. Use IterativeRobot or Command-Based
 * instead if you're new.
 */
public class Robot extends SampleRobot {

	static ADIS16448_IMU imu;

	static double gearPegOffset = 0; // PLACE HOLDER
	static double boilerOffset = 0; // PLACE HOLDER

	AutoCases autonomous;
	mecanumNavigation robotLocation;
	mecanumDrive driveSystem;
	Climber climbingSystem;
	gearCollection gearMechanism;
	ArmPID gearArmControl;
	BallShooter Shooter;
	BallCollector ballCollectingMechanism;

	static Timer systemTimer;
	static Joystick XBoxController;
	static Joystick logitechJoystick;
	static Joystick autoSelector;

	// PIXY camera I2C
	static PIXY pixyI2C;
	static ArrayList<Frame> pixyFrames;

	// Boiler LIDAR (with GRIP camera on turntable #1)
	static LIDAR boilerLidar;
	static LidarSpin turntable1;

	// yawOffsetToFieldFrame is set at start of autonomous when the robot
	// heading is either 0.0 or 180.0
	// Get Field north reference by adding this to imu.getYaw()
	//
	// This loses accuracy over the course of the game, but should be very
	// reliable during autonomous.
	//
	// It is still pretty good in teleop because the errors affect only the
	// polar
	// angle, not the LIDAR range that is continually measured by the vision
	// system. Therefore drive toward an increasingly sloppy range on an arc,
	// but once found you can slide precisely down the arc to the destination.
	private static double _yawOffsetToFieldFrame = 0.0;

	public synchronized static double yawOffsetToFieldFrame() {
		return _yawOffsetToFieldFrame;
	}

	static BoilerTracker boilerTracker;

	// GRIP vision camera
	static GripVisionThread gripVisionThread;

	// TODO: Verify can set this from the driver station.
	// Want to disable scanning in the pit (or anywhere else there is no vision
	// target)
	// But need a way to turn it on from the DS in case forgot to turn it back
	// on before the match starts
	static boolean isBoilerTrackerEnabled = false;

	// Autonomous Route Plans
	// TODO: Use Joystick button controls to select the auto route to be used
	// Hard coded to route '2' for testing
	int autoPlanSelection = 2;
	static RoutePlanList planList;
	static RoutePlan planForThisMatch;

	public Robot() {
		imu = new ADIS16448_IMU();

		try {
			autonomous = new AutoCases();

			robotLocation = new mecanumNavigation(0,1,2,3,4,5,6,7);//dio ports
			driveSystem = new mecanumDrive(0, 1, 2, 3);// pwm ports
			climbingSystem = new Climber(9, 13);// pwm port 9, dio port 13
		gearMechanism = new gearCollection(6,7,14,8,9);//pwm ports 6 and 7, dio ports 14, 8, 9
			Shooter = new BallShooter(4, 5, 15);// pwm ports 4 & 5, dio port 15
			ballCollectingMechanism = new BallCollector(8);// pwm port 8

			robotLocation.start();
			gearArmControl.start();

			XBoxController = new Joystick(3);
			logitechJoystick = new Joystick(0);
			autoSelector = new Joystick(1);
		} catch (Exception e) {
			SmartDashboard.putString("debug", "robot constructor failed");
		}

	}

	public void robotInit() {
		SmartDashboard.putString("debug", "robot constructor 1");
		try {

			pixyI2C = new PIXY("pixyI2C", Port.kMXP, 0x54);

			boilerTracker = new BoilerTracker();
			boilerLidar = new LIDAR("boiler", Port.kMXP, 0x62);
			SmartDashboard.putString("debug", "robot constructor 2");

			int relay1 = 1;
			int dio21 = 21; // 10 + DIO11 on the more board
			int dio22 = 22;
			turntable1 = new LidarSpin(relay1, dio21, dio22);
			// Scan limits -140 to +230 for competition
			// 0.0 is straight ahead robot frame. Want to avoid extended
			// operation
			// with the back of the turntable pointed at the boiler, (small
			// variance
			// in robot heading would cause the scanner to have to switch
			// sides).
			turntable1.setScanLimits(-45, 45); // TMP TMP TMP for prototype
												// testing

			planList = new RoutePlanList();
			planForThisMatch = planList.get(autoPlanSelection);

			SmartDashboard.putString("debug", "robot constructor 3");
			
			gripVisionThread = new GripVisionThread();
			gripVisionThread.start();

			SmartDashboard.putString("debug", "robot constructor 4");
		} catch (Exception e) {
			SmartDashboard.putString("debug", "robot constructor failed");
		}
	}

	private synchronized void alignTurntableRobotAndField() {

		// Assume camera has found and is pointing straight to boiler prior to
		// start of match
		_yawOffsetToFieldFrame = 0.0 - imu.getYaw();
		if (boilerTracker.currentRobotFieldPosition().isBlueBoiler) {
			Robot.turntable1.resetEncoderAtOffsetDegrees(-90.0);
			_yawOffsetToFieldFrame += 180.0;
		} else {
			Robot.turntable1.resetEncoderAtOffsetDegrees(90.0);
		}
		if (_yawOffsetToFieldFrame > 180.0) {
			_yawOffsetToFieldFrame -= 360.0;
		}

	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	public void autonomous() {

		// autonomous.autoModes();

		isBoilerTrackerEnabled = true;
		alignTurntableRobotAndField();

		turntable1.setSpinMode(LidarSpin.SpinMode.FIXED_OFFSET_FROM_YAW);


		/*
		 * boolean isError = false; for (int i = 0; i < planForThisMatch.size();
		 * i++) { RouteTask.ReturnValue retVal = planForThisMatch.get(i).exec();
		 * if (retVal != RouteTask.ReturnValue.SUCCESS) { isError = true;
		 * SmartDashboard.putString("Auto Status", "Auto Failed step " + i);
		 * break; } } if (!isError) { SmartDashboard.putString("Auto Status",
		 * "Auto Complete"); } } else { // TODO: use mecanumNavigation() instead
		 * of vision system }
		 */

		while (isOperatorControl() && isEnabled()) {
			Timer.delay(0.005); // wait 
		}

	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		isBoilerTrackerEnabled = true;

		turntable1.setSpinMode(LidarSpin.SpinMode.SCAN);

		while (isOperatorControl() && isEnabled()) {
			driveSystem.Operatordrive();
			//climbingSystem.performMainProcessing();
			//gearMechanism.performMainProcessing();
			//Shooter.performMainProcessing();
			//ballCollectingMechanism.performMainProcessing();
			Timer.delay(.05);
		}
		isBoilerTrackerEnabled = false;
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
		turntable1.setSpinMode(LidarSpin.SpinMode.IDLE);
		// driveSystem.YTest();
		// driveSystem.XTest();
		// driveSystem.TwistTest();

	}
}
