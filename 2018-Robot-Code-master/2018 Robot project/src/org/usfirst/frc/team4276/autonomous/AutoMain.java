package org.usfirst.frc.team4276.autonomous;

import org.usfirst.frc.team4276.robot.Robot;
import org.usfirst.frc.team4276.utilities.SoftwareTimer;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoMain {

	SoftwareTimer phaseTimer;
	// constants
	private final double ARM_OUT = 0; // straight out

	// gamedata values
	public int switchValue = 0;
	public int scaleValue = 0;
	boolean startIsCenter = false;
	boolean startIsRight = false;
	boolean startIsLeft = false;

	// AutoSequences error references
	private final int GET_START_POSITION_ERROR = 1;
	private final int ROUTINE_DETERMINE_ERROR = 2;
	private final int ROUTE_PLAN_ERROR = 3;

	// currentState Definitions
	static final int INIT = 0;
	static final int DRIVE_OFF_WALL = 1;
	static final int ROTATE_OFF_WALL = 2;
	static final int DRIVE_TO_SCORE_1 = 3;
	static final int ROTATE_TO_SCORE = 4;
	static final int PREP_TO_SCORE = 5;
	// static final int LOWER_ARM = 6;
	static final int DRIVE_TO_SCORE_2 = 6;
	static final int RELEASE_CUBE = 7;
	static final int BACK_UP = 8;
	static final int DRIVE_ACROSS = 9;
	static final int ROTATE_TO_COLLECT = 10;
	static final int DRIVE_TO_COLLECT = 11;
	static final int SHOOT_CUBE = 12;
	static final int EXIT = 13;

	static int currentState = INIT; // always runs init first
	String currentStateName;

	// routines
	public int setRoutine = 0;

	public final int DEFAULT = 0;
	public final int MID_TO_LEFT_SWITCH = 1;
	public final int MID_TO_RIGHT_SWITCH = 2;
	public final int LEFT_SWITCH = 3;
	public final int RIGHT_SWITCH = 4;
	public final int LEFT_SCALE = 3;
	public final int RIGHT_SCALE = 4;
	public final int LEFT_SCALE_AND_SWITCH = 5;
	public final int RIGHT_SCALE_AND_SWITCH = 6;
	public final int LEFT_SWITCH_RIGHT_SCALE = 7;
	public final int RIGHT_SWITCH_LEFT_SCALE = 8;
	public final int RIGHT_SCALE_LEFT_SWITCH = 9;
	public final int LEFT_SCALE_RIGHT_SWITCH = 10;
	public final int CROSS_RIGHT_SWITCH = 11;
	public final int CROSS_LEFT_SWITCH = 12;
	public final int CROSS_RIGHT_SCALE_SWITCH = 13;
	public final int CROSS_LEFT_SCALE_SWITCH = 14;

	// command return status
	boolean coordinateReached = false;
	boolean headingReached = false;

	// currentState enabled array
	static boolean[] stateEnabled = new boolean[EXIT + 1];

	public AutoMain() {
		phaseTimer = new SoftwareTimer();
	}

	public double[] getStartPosition() {
		double[] startPosition = new double[] { 0, 0 }; // default
		if (RoutineSelector.startingPosition == RoutineSelector.CENTER) {
			startPosition = FieldLocations.CENTER_START_POSITION;
			startIsCenter = true;
			startIsRight = false;
			startIsLeft = false;
		} else if (RoutineSelector.startingPosition == RoutineSelector.LEFT) {
			startPosition = FieldLocations.LEFT_START_POSITION;
			startIsCenter = false;
			startIsRight = false;
			startIsLeft = true;
		} else if (RoutineSelector.startingPosition == RoutineSelector.RIGHT) {
			startPosition = FieldLocations.RIGHT_START_POSITION;
			startIsCenter = false;
			startIsRight = true;
			startIsLeft = false;
		} else {
			SmartDashboard.putNumber("Auto Error", GET_START_POSITION_ERROR);
		}

		return startPosition;
	}

	public void updateGameData() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();

		boolean Lswitch = false;
		boolean Rswitch = false;
		boolean Lscale = false;
		boolean Rscale = false;

		if (gameData.charAt(0) == 'L') {

			switchValue = LEFT_SWITCH;
			Lswitch = true;
		} else if (gameData.charAt(0) == 'R') {

			switchValue = RIGHT_SWITCH;
			Rswitch = true;
		}

		if (gameData.charAt(1) == 'L') {

			scaleValue = LEFT_SCALE;
			Lscale = true;
		} else if (gameData.charAt(1) == 'R') {

			scaleValue = RIGHT_SCALE;
			Rscale = true;
		}

		SmartDashboard.putBoolean("L Switch", Lswitch);
		SmartDashboard.putBoolean("L Switch", Rswitch);
		SmartDashboard.putBoolean("L Switch", Lscale);
		SmartDashboard.putBoolean("L Switch", Rscale);

	}

	public void planRoute() {
		getStartPosition();
		updateGameData();
		if (startIsCenter) {
			// if start in middle

			if (switchValue == LEFT_SWITCH) {
				// if switch to left
				setRoutine = MID_TO_LEFT_SWITCH;
			} else if (switchValue == RIGHT_SWITCH) {
				// if switch to right
				setRoutine = MID_TO_RIGHT_SWITCH;
			}
		} else if (Robot.routineSelector.strategy == Robot.routineSelector.SCORE_SWITCH) {
			// if set to just switch
			if (startIsLeft) {
				// if start on left side
				if (switchValue == LEFT_SWITCH) {
					// if switch on this side
					setRoutine = LEFT_SWITCH;
				} else if (switchValue == RIGHT_SWITCH) {
					// if switch on other side
					setRoutine = DEFAULT;
				} else {
					// if all scoring is on right side
					setRoutine = DEFAULT;
					SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
				}
			} else if (startIsRight) {
				// if start on right side

				if (switchValue == RIGHT_SWITCH) {
					// if switch on this side
					setRoutine = RIGHT_SWITCH;
				} else if (scaleValue == LEFT_SWITCH) {
					// if scale on this side
					setRoutine = DEFAULT;
				} else {
					// if all scoring is on left side
					setRoutine = DEFAULT;
					SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
				}
			} else {
				SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
			}
		} else if (Robot.routineSelector.strategy == Robot.routineSelector.SCORE_BOTH) {
			// if set to score both
			if (startIsLeft) {
				// if start on left side
				if (switchValue == LEFT_SWITCH && scaleValue == LEFT_SCALE) {
					// if switch and scale on this side
					setRoutine = LEFT_SCALE_AND_SWITCH;
				} else if (switchValue == LEFT_SWITCH && scaleValue == RIGHT_SCALE) {
					// if switch on this side scale on other side
					setRoutine = LEFT_SWITCH;
				} else if (switchValue == RIGHT_SWITCH && scaleValue == LEFT_SCALE) {
					// if switch on other side scale on this side
					setRoutine = LEFT_SCALE_RIGHT_SWITCH;
				} else if (switchValue == RIGHT_SWITCH && scaleValue == RIGHT_SCALE) {
					// if switch on other side scale on other side
					setRoutine = CROSS_RIGHT_SCALE_SWITCH;
				} else {
					// if all scoring is on right side
					SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
				}
			} else if (startIsRight) {
				// if start on right side
				if (switchValue == RIGHT_SWITCH && scaleValue == RIGHT_SCALE) {
					// if switch and scale on this side
					setRoutine = RIGHT_SCALE_AND_SWITCH;
				} else if (switchValue == RIGHT_SWITCH && scaleValue == LEFT_SCALE) {
					// if switch on this side scale on other side
					setRoutine = RIGHT_SWITCH;
				} else if (switchValue == LEFT_SWITCH && scaleValue == RIGHT_SCALE) {
					// if switch on other side scale on this side
					setRoutine = RIGHT_SCALE_LEFT_SWITCH;
				} else if (switchValue == LEFT_SWITCH && scaleValue == LEFT_SCALE) {
					// if switch on other side scale on other side
					setRoutine = CROSS_LEFT_SCALE_SWITCH;
				} else {
					// if all scoring is on right side
					SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
				}
			} else if (Robot.routineSelector.strategy == Robot.routineSelector.CROSS_BASE) {
				setRoutine = DEFAULT;

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTE_PLAN_ERROR);
			}
		}
	}

	public void defineStateEnabledStatus() {
		if (setRoutine == DEFAULT) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = true;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = false;
			stateEnabled[ROTATE_TO_SCORE] = false;
			stateEnabled[PREP_TO_SCORE] = false;
			// stateEnabled[LOWER_ARM] = false;
			stateEnabled[DRIVE_TO_SCORE_2] = false;
			stateEnabled[RELEASE_CUBE] = false;
			stateEnabled[BACK_UP] = false;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = false;
			stateEnabled[DRIVE_TO_COLLECT] = false;
			stateEnabled[SHOOT_CUBE] = false;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == MID_TO_LEFT_SWITCH || setRoutine == MID_TO_RIGHT_SWITCH) {
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false; // edited
			stateEnabled[ROTATE_OFF_WALL] = false; // edited
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = false; // edited
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = true;
			stateEnabled[DRIVE_TO_COLLECT] = false;
			stateEnabled[SHOOT_CUBE] = false;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == LEFT_SWITCH || setRoutine == RIGHT_SWITCH) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = true;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = false;
			stateEnabled[DRIVE_TO_COLLECT] = false;
			stateEnabled[SHOOT_CUBE] = false;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == LEFT_SCALE || setRoutine == RIGHT_SCALE) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = false;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = false;
			stateEnabled[DRIVE_TO_COLLECT] = false;
			stateEnabled[SHOOT_CUBE] = false;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == CROSS_LEFT_SWITCH || setRoutine == CROSS_RIGHT_SWITCH) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = true;
			stateEnabled[ROTATE_OFF_WALL] = true;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = true;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = false;
			stateEnabled[DRIVE_TO_COLLECT] = false;
			stateEnabled[SHOOT_CUBE] = false;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == LEFT_SCALE_AND_SWITCH || setRoutine == RIGHT_SCALE_AND_SWITCH) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = false;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = true;
			stateEnabled[DRIVE_TO_COLLECT] = true;
			stateEnabled[SHOOT_CUBE] = true;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == LEFT_SWITCH_RIGHT_SCALE || setRoutine == RIGHT_SWITCH_LEFT_SCALE) {
			// set all false
			// NOT DONE
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = false;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = true;
			stateEnabled[DRIVE_TO_COLLECT] = true;
			stateEnabled[SHOOT_CUBE] = true;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == RIGHT_SCALE_LEFT_SWITCH || setRoutine == LEFT_SCALE_RIGHT_SWITCH) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = false;
			stateEnabled[ROTATE_OFF_WALL] = false;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = false;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = true;
			stateEnabled[ROTATE_TO_COLLECT] = true;
			stateEnabled[DRIVE_TO_COLLECT] = true;
			stateEnabled[SHOOT_CUBE] = true;
			stateEnabled[EXIT] = true;
		} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH || setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
			// set all false
			stateEnabled[INIT] = true;
			stateEnabled[DRIVE_OFF_WALL] = true;
			stateEnabled[ROTATE_OFF_WALL] = true;
			stateEnabled[DRIVE_TO_SCORE_1] = true;
			stateEnabled[ROTATE_TO_SCORE] = true;
			stateEnabled[PREP_TO_SCORE] = true;
			// stateEnabled[LOWER_ARM] = true;
			stateEnabled[DRIVE_TO_SCORE_2] = true;
			stateEnabled[RELEASE_CUBE] = true;
			stateEnabled[BACK_UP] = true;
			stateEnabled[DRIVE_ACROSS] = false;
			stateEnabled[ROTATE_TO_COLLECT] = true;
			stateEnabled[DRIVE_TO_COLLECT] = true;
			stateEnabled[SHOOT_CUBE] = true;
			stateEnabled[EXIT] = true;
		}

	}

	// Main State Variables
	boolean loop;
	boolean performStateEntry = true;

	public void loop() {
		switch (currentState) {
		case INIT:
			currentStateName = "Init";
			// State entry
			if (performStateEntry) {

				PositionFinder.setStartPoint(getStartPosition());
				planRoute();
				defineStateEnabledStatus();

				Robot.driveTrain.setHiGear();
				phaseTimer.setTimer(0.2);
				performStateEntry = false;

			}

			// State processing

			// State exit
			if (phaseTimer.isExpired()) {
				if (setRoutine == DEFAULT) {

				} else {
					Robot.elevator.commandedHeight = Robot.elevator.SETPOINT_SWITCH;
					Robot.armPivoter.commandSetpoint(ARM_OUT);
				}
				performStateExit();
			}

			break;

		case DRIVE_OFF_WALL:
			currentStateName = "Drive Off Wall";
			// State entry

			if (performStateEntry) {
				if (setRoutine == DEFAULT) {

					phaseTimer.setTimer(1.7);
				} else {
					phaseTimer.setTimer(4);
				}
				performStateEntry = false;
			}

			// State processing
			if (setRoutine == DEFAULT) {
				Robot.driveTrain.goForward(true);

			} else if (setRoutine == CROSS_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else if (setRoutine == CROSS_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else {
				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case ROTATE_OFF_WALL:
			currentStateName = "Rotate Off Wall";

			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(1.4);
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == CROSS_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.LEFT_JUNCTION);

			} else if (setRoutine == CROSS_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.RIGHT_JUNCTION);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else {
				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (headingReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case DRIVE_TO_SCORE_1:
			currentStateName = "Drive to Score Prep";

			// State entry
			if (performStateEntry) {
				if (setRoutine == MID_TO_RIGHT_SWITCH || setRoutine == MID_TO_LEFT_SWITCH) {
					phaseTimer.setTimer(2);
				} else {
					phaseTimer.setTimer(4);
				}
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == MID_TO_LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_LEFT_A);

			} else if (setRoutine == MID_TO_RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_RIGHT_A);

			} else if (setRoutine == LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_LEFT_B);

			} else if (setRoutine == RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_RIGHT_B);

			} else if (setRoutine == RIGHT_SCALE) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_RIGHT_B);

			} else if (setRoutine == LEFT_SCALE) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_PREP_LEFT_B);

			} else if (setRoutine == CROSS_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_JUNCTION);

			} else if (setRoutine == CROSS_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_JUNCTION);

			} else if (setRoutine == LEFT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else if (setRoutine == RIGHT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case ROTATE_TO_SCORE:
			currentStateName = "Rotate to score";

			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(2);
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftSwitchScoringZoneB);

			} else if (setRoutine == RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightSwitchScoringZoneB);

			} else if (setRoutine == CROSS_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.LEFT_JUNCTION);

			} else if (setRoutine == CROSS_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.RIGHT_JUNCTION);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftScaleScoringZoneA);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightScaleScoringZoneA);

			} else {
				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case PREP_TO_SCORE:
			currentStateName = "score prep";

			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(.0001);
				performStateEntry = false;
			}
			// State processing
			Robot.armPivoter.commandSetpoint(ARM_OUT);
			if (setRoutine == MID_TO_LEFT_SWITCH || setRoutine == MID_TO_RIGHT_SWITCH || setRoutine == LEFT_SWITCH
					|| setRoutine == RIGHT_SWITCH || setRoutine == CROSS_LEFT_SWITCH
					|| setRoutine == CROSS_RIGHT_SWITCH) {
				// bring elevator to switch scoring height
				Robot.elevator.commandedHeight = Robot.elevator.SETPOINT_SWITCH;

			} else if (setRoutine == LEFT_SCALE || setRoutine == RIGHT_SCALE || setRoutine == LEFT_SCALE_AND_SWITCH
					|| setRoutine == RIGHT_SCALE_AND_SWITCH || setRoutine == RIGHT_SCALE_LEFT_SWITCH
					|| setRoutine == LEFT_SCALE_RIGHT_SWITCH || setRoutine == CROSS_LEFT_SCALE_SWITCH
					|| setRoutine == CROSS_RIGHT_SCALE_SWITCH) {

				// bring elevator to scale scoring height
				Robot.elevator.commandedHeight = Robot.elevator.SETPOINT_SCALE;

			} else {
				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (phaseTimer.isExpired()) {
				performStateExit();
			}
			break;

		/*
		 * case LOWER_ARM: currentStateName = "Raise Elevator";
		 * 
		 * // State entry if (performStateEntry) { phaseTimer.setTimer(.0001);
		 * performStateEntry = false; } // State processing
		 * 
		 * Robot.armPivoter.commandSetpoint(ARM_OUT); // State exit if
		 * (phaseTimer.isExpired()) { performStateExit(); } break;
		 */

		case DRIVE_TO_SCORE_2:
			currentStateName = "Drive to Score Deliver";

			// State entry
			if (performStateEntry) {
				if (setRoutine == MID_TO_RIGHT_SWITCH || setRoutine == MID_TO_LEFT_SWITCH) {
					phaseTimer.setTimer(2);
				} else {
					phaseTimer.setTimer(3);
				}
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == MID_TO_LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftSwitchScoringZoneA);

			} else if (setRoutine == MID_TO_RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightSwitchScoringZoneA);

			} else if (setRoutine == LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftSwitchScoringZoneB);

			} else if (setRoutine == RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightSwitchScoringZoneB);

			} else if (setRoutine == RIGHT_SCALE) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightScaleScoringZoneA);

			} else if (setRoutine == LEFT_SCALE) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftScaleScoringZoneA);

			} else if (setRoutine == CROSS_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_SCORING_ZONE_LEFT_C);

			} else if (setRoutine == CROSS_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.SWITCH_SCORING_ZONE_RIGHT_C);

			} else if (setRoutine == LEFT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftScaleScoringZoneA);

			} else if (setRoutine == RIGHT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightScaleScoringZoneA);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftScaleScoringZoneA);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightScaleScoringZoneA);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case RELEASE_CUBE:
			currentStateName = "Release Cube";
			// State entry
			if (performStateEntry) {

				Robot.manipulator.outake();
				phaseTimer.setTimer(0.25);
				performStateEntry = false;
			}

			// State processing
			if (setRoutine == RIGHT_SCALE_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == LEFT_SCALE_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (headingReached || phaseTimer.isExpired()) {
				Robot.manipulator.stop();
				performStateExit();
			}

			break;

		case BACK_UP:
			currentStateName = "Back Up";
			// State entry
			if (performStateEntry) {
				Robot.elevator.commandToBottom();
				phaseTimer.setTimer(0.6);
				performStateEntry = false;
			}
			// State processing
			Robot.driveTrain.goReverse(true);
			// State exit
			if (phaseTimer.isExpired()) {
				Robot.driveTrain.goReverse(false);
				Robot.driveTrain.resetDrive();
				performStateExit();
			}

			break;

		case DRIVE_ACROSS:
			currentStateName = "Drive Across";
			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(3.5);
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == RIGHT_SCALE_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.LEFT_CROSS_ZONE);

			} else if (setRoutine == LEFT_SCALE_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.driveToCoordinate(FieldLocations.RIGHT_CROSS_ZONE);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}

			break;

		case ROTATE_TO_COLLECT:
			currentStateName = "Rotate to collect";

			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(1.2);
				performStateEntry = false;
			}
			// State processing
			if (setRoutine == LEFT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == RIGHT_SCALE_AND_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else if (setRoutine == RIGHT_SCALE_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == LEFT_SCALE_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else if (setRoutine == MID_TO_LEFT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == MID_TO_RIGHT_SWITCH) {
				headingReached = Robot.driveTrain.rotateToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case DRIVE_TO_COLLECT:
			currentStateName = "Drive to collect";

			// State entry
			if (performStateEntry) {
				phaseTimer.setTimer(1.5);
				Robot.driveTrain.setLoGear();
				Robot.armPivoter.commandSetpoint(0);
				Robot.elevator.commandedHeight = Robot.elevator.SETPOINT_BOTTOM;
				performStateEntry = false;
			}
			// State processing

			if (setRoutine == LEFT_SCALE_AND_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == RIGHT_SCALE_AND_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else if (setRoutine == CROSS_LEFT_SCALE_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == CROSS_RIGHT_SCALE_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else if (setRoutine == RIGHT_SCALE_LEFT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.leftCornerCubeLocation);

			} else if (setRoutine == LEFT_SCALE_RIGHT_SWITCH) {
				coordinateReached = Robot.driveTrain.driveToCoordinate(FieldLocations.rightCornerCubeLocation);

			} else {

				SmartDashboard.putNumber("Auto Error", ROUTINE_DETERMINE_ERROR);
			}
			// State exit
			if (coordinateReached || phaseTimer.isExpired()) {

				Robot.elevator.commandToSwitch();
				Robot.driveTrain.resetDrive();
				performStateExit();
			}
			break;

		case SHOOT_CUBE:
			currentStateName = "Release Cube";
			// State entry
			if (performStateEntry) {
				Robot.elevator.commandToSwitch();
				phaseTimer.setTimer(0.3);
				performStateEntry = false;
			}

			// State processing

			// State exit
			if (phaseTimer.isExpired()) {
				Robot.manipulator.shoot();
				performStateExit();
			}

			break;

		case EXIT:
			Robot.driveTrain.resetDrive();
			Robot.manipulator.stop();
			currentStateName = "Autonomous Done";
			break;

		}
		SmartDashboard.putString("Current State", currentStateName);
	}

	private void performStateExit() {
		if (phaseTimer.isExpired()) {
			SmartDashboard.putString("Exit Status", currentStateName + "time expired");
		} else {
			SmartDashboard.putString("Exit Status", currentStateName + "target reached");
		}
		coordinateReached = false;
		headingReached = false;
		performStateEntry = true;
		advanceState();
	}

	public void advanceState() {
		if (stateEnabled[(currentState + 1)]) {
			currentState = currentState + 1;
		} else {
			currentState = currentState + 1;
			advanceState();
		}
	}
}
