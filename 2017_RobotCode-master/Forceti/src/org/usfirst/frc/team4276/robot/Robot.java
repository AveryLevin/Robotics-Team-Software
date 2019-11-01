
package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static org.usfirst.frc.team4276.robot.RoboRioPorts.*;

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
	
	static Timer systemTimer;
	static Joystick XBoxController;
	static Joystick logitechJoystick;
	static Joystick autoSelector;
	static Joystick testJoy;
	
	Sonar sonar;
	//mecanumNavigation robotLocation;
	mecanumDrive driveSystem;
	Climber climbingSystem;
	gearCollection gearMechanism;
	ArmPID gearArmControl;
	BallShooter Shooter;
	autoShooter smartShoot;
	BallCollector ballCollectingMechanism;
	AutoCases autonomous;
	//autoModeSelector autoSelect;
	AutoSelector autoSelectorThread;
	UsbCamera cam;
	LEDi2cInterface LEDs;
	
	
	static double yawOffsetToFieldFrame = 0.0;
	static double xyFieldFrameSpeed = 0.0;
	static double xyFieldFrameHeading = 0.0;

	public Robot() {
		

		XBoxController = new Joystick(3);
		logitechJoystick = new Joystick(0);
		//autoSelector = new Joystick(1);
		testJoy = new Joystick(1);
		
		imu = new ADIS16448_IMU();
		
		cam=CameraServer.getInstance().startAutomaticCapture(0);
		cam.setResolution(320, 240);
		cam.setFPS(30);
		cam.setExposureManual(30);

		systemTimer = new Timer();
		systemTimer.start();
		
		sonar = new Sonar(20,21);

		gearArmControl = new ArmPID();
		//robotLocation = new mecanumNavigation(DIO_DRIVE_FL_A, DIO_DRIVE_FL_B, DIO_DRIVE_BL_A, DIO_DRIVE_BL_B);
		Shooter = new BallShooter(PWM_SHOOTER_FLYWHEEL, PWM_SHOOTER_FEEDER, DIO_SHOOTER_FLYWHEEL_A, DIO_SHOOTER_FLYWHEEL_B);
		driveSystem = new mecanumDrive(PWM_DRIVE_FR, PWM_DRIVE_FL, PWM_DRIVE_BR, PWM_DRIVE_BL,sonar);
		climbingSystem = new Climber(PWM_CLIMBER);
		gearMechanism = new gearCollection(PWM_GEAR_ANGLE, PWM_GEAR_INTAKE, DIO_GEAR_LIMIT, DIO_GEAR_ANGLE_A, DIO_GEAR_ANGLE_B);
															
		LEDs = new LEDi2cInterface(0,1,2,3);
		
		ballCollectingMechanism = new BallCollector(PWM_BALL_INTAKE);

		smartShoot = new autoShooter(Shooter);
		autonomous = new AutoCases(smartShoot,driveSystem,gearMechanism,ballCollectingMechanism);
		//autoSelect = new autoModeSelector();
		autoSelectorThread = new AutoSelector();
		
		                         //autoSelect.start();
		//robotLocation.start();
		gearArmControl.start();
		autoSelectorThread.start();
		LEDs.start();
		

	}

	public void robotInit() {
		SmartDashboard.putString("auto", "no");

		LEDi2cInterface.enabled=false;
		//LEDs.testI2C();

		alignRobotAndField();
	}
	
	public void autonomous() {
		SmartDashboard.putString("auto", "yes");
		LEDi2cInterface.enabled=true;
		autonomous.autoModes();
	}
	
	private synchronized void alignRobotAndField() {
		yawOffsetToFieldFrame = 0.0 - imu.getAngleZ();
	}


	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {

		while (isOperatorControl() && isEnabled()) {
			driveSystem.Operatordrive();
			climbingSystem.performMainProcessing();
			gearMechanism.performMainProcessing();
			Shooter.performMainProcessing();
			//ballCollectingMechanism.performMainProcessing();
			ballCollectingMechanism.pushButtonBallIntake();
			Timer.delay(.005);
			LEDi2cInterface.enabled=true;
			//LEDs.testI2C();
		}
	}

	/**
	 * Runs during test mode
	 */
	public void test() {
		
		while (isEnabled()) {
			driveSystem.Operatordrive();
			climbingSystem.driveInReverse();
			gearMechanism.performMainProcessing();
			Shooter.performMainProcessing();
			ballCollectingMechanism.performMainProcessing();
			Timer.delay(.005);
			LEDi2cInterface.enabled=true;
		}

	}
}
