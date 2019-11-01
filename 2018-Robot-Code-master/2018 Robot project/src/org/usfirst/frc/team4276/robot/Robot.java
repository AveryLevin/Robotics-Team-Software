package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4276.autonomous.PositionFinder;
import org.usfirst.frc.team4276.autonomous.AutoMain;
import org.usfirst.frc.team4276.autonomous.RoutineSelector;
import org.usfirst.frc.team4276.systems.ArmPivoter;
import org.usfirst.frc.team4276.systems.Climber;
import org.usfirst.frc.team4276.systems.DriveTrain;
import org.usfirst.frc.team4276.systems.Elevator;
import org.usfirst.frc.team4276.systems.Manipulator;
import org.usfirst.frc.team4276.systems.Cameras;
import org.usfirst.frc.team4276.utilities.RoboRioPorts;

public class Robot extends SampleRobot {

	public static Timer systemTimer;
	public static Joystick logitechJoystickL;
	public static Joystick logitechJoystickR;
	public static Joystick xboxController;

	public static Cameras robotCameraSystem;
	public static DriveTrain driveTrain;
	public static Elevator elevator;
	public static Manipulator manipulator;
	public static ArmPivoter armPivoter;
	public static Climber climber;

	public static PositionFinder positionFinder;
	public static RoutineSelector routineSelector;
	public AutoMain autoMain;

	public Robot() {
		// Master Timer
		systemTimer = new Timer();

		// Controllers
		logitechJoystickL = new Joystick(0);
		logitechJoystickR = new Joystick(1);
		xboxController = new Joystick(2);

		// Mechanisms
		robotCameraSystem = new Cameras();
		driveTrain = new DriveTrain(RoboRioPorts.DRIVE_DOUBLE_SOLENOID_FWD, RoboRioPorts.DRIVE_DOUBLE_SOLENOID_REV,
				RoboRioPorts.CAN_DRIVE_L1, RoboRioPorts.CAN_DRIVE_R1, RoboRioPorts.CAN_DRIVE_L2,
				RoboRioPorts.CAN_DRIVE_R2, RoboRioPorts.CAN_DRIVE_L3, RoboRioPorts.CAN_DRIVE_R3);
		elevator = new Elevator(RoboRioPorts.CAN_RAIL_DRIVER_R1, RoboRioPorts.CAN_RAIL_DRIVER_R2,
				RoboRioPorts.CAN_RAIL_DRIVER_L1, RoboRioPorts.CAN_RAIL_DRIVER_L2);
		armPivoter = new ArmPivoter(RoboRioPorts.CAN_ARM_PIVOT);
		manipulator = new Manipulator(RoboRioPorts.CAN_INTAKE_L, RoboRioPorts.CAN_INTAKE_R,
				RoboRioPorts.INTAKE_LIM_SWITCH);
		climber = new Climber(RoboRioPorts.CLIMBER_PISTON_FWD, RoboRioPorts.CLIMBER_PISTON_REV);

		// Autonomous
		routineSelector = new RoutineSelector();
		positionFinder = new PositionFinder(RoboRioPorts.DIO_DRIVE_LEFT_A, RoboRioPorts.DIO_DRIVE_LEFT_B,
				RoboRioPorts.DIO_DRIVE_RIGHT_A, RoboRioPorts.DIO_DRIVE_RIGHT_B);
		autoMain = new AutoMain();
	}

	public void robotInit() {
		positionFinder.calibrateImu();

		// Start threads
		systemTimer.start();
		armPivoter.start();
		elevator.start();
		positionFinder.start();
		routineSelector.start();
	}

	public void autonomous() {
		armPivoter.initializeThread();
		elevator.initializeThread();
		while (isAutonomous() && isEnabled()) {
			// double desiredCoordinate[] = { 10, 0 };
			// driveTrain.rotateToHeading(90);
			// driveTrain.rotateToCoordinate(desiredCoordinate);
			// if (driveTrain.driveToCoordinate(desiredCoordinate));
			autoMain.loop();
			Timer.delay(0.05);
		}
	}

	public void operatorControl() {
		armPivoter.initializeThread();
		elevator.initializeThread();
		// positionFinder.kill();
		routineSelector.kill();
		driveTrain.resetDrive();
		manipulator.stop();
		while (isOperatorControl() && isEnabled()) {
			driveTrain.performMainProcessing();
			// robotArmPivoter.performMainProcessing();
			climber.performMainProcessing();
			manipulator.performMainProcessing();
			Timer.delay(0.05);
		}
	}

	public void test() {
	}
}
