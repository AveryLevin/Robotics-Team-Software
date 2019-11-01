package org.usfirst.frc.team4276.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team4276.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class RoutineSelector extends Thread implements Runnable {

	// Selection modes
	private final int COMMIT_MODE = 0;
	private final int EDIT_MODE = 1;
	private String[] selectionModeArray = new String[2];

	// Starting positions
	static final int LEFT = 0;
	static final int CENTER = 1;
	static final int RIGHT = 2;
	static String[] startPositionArray = new String[3];

	// Starting positions
	public final int CROSS_BASE = 0;
	public final int SCORE_SWITCH = 1;
	public final int SCORE_BOTH = 2;
	private String[] strategyArray = new String[3];

	private int selectionMode = COMMIT_MODE;
	static int startingPosition = 0;
	static int strategy = 0;
	static int autoModeToExecute;

	public boolean breakLoop = false;

	public RoutineSelector() {
		selectionModeArray[COMMIT_MODE] = "Commit mode";
		selectionModeArray[EDIT_MODE] = "Edit mode";

		startPositionArray[LEFT] = "left";
		startPositionArray[CENTER] = "center";
		startPositionArray[RIGHT] = "right";

		strategyArray[CROSS_BASE] = "cross base";
		strategyArray[SCORE_SWITCH] = "score switch";
		strategyArray[SCORE_BOTH] = "score both";

		/*
		 * selectionModeChooser = new SendableChooser<Integer>();
		 * selectionModeChooser.addDefault(selectionModeArray[COMMIT_MODE],
		 * COMMIT_MODE);
		 * selectionModeChooser.addObject(selectionModeArray[EDIT_MODE],
		 * EDIT_MODE); SmartDashboard.putData("Commit Selections",
		 * selectionModeChooser);
		 * 
		 * startPosition = new SendableChooser<Integer>();
		 * startPosition.addDefault(startPositionArray[CENTER], CENTER);
		 * startPosition.addObject(startPositionArray[LEFT], LEFT);
		 * startPosition.addObject(startPositionArray[RIGHT], RIGHT);
		 * SmartDashboard.putData("Starting Position", startPosition);
		 */
		// strategyChooser = new SendableChooser<Integer>();
		// strategyChooser.addDefault(startPositionArray[CROSS_BASE],
		// CROSS_BASE);
		// strategyChooser.addObject(startPositionArray[SCORE_SWITCH],
		// SCORE_SWITCH);
		// strategyChooser.addObject(startPositionArray[SCORE_SCALE],
		// SCORE_SCALE);
		// SmartDashboard.putData("Selected Strategy", strategyChooser);

	}

	public void kill() {
		breakLoop = true;
	}

	public void run() {
		try {
			while (true) {
				/*
				 * if (selectionMode == COMMIT_MODE) { selectionMode = (int)
				 * selectionModeChooser.getSelected(); } else { selectionMode =
				 * (int) selectionModeChooser.getSelected(); startingPosition =
				 * (int) startPosition.getSelected(); // strategy = (int) //
				 * strategyChooser.getSelectedautoModeToExecute = //
				 * startingPosition; }
				 */

				if (Robot.logitechJoystickL.getRawButton(7)) {

					startingPosition = LEFT;

				} else if (Robot.logitechJoystickL.getRawButton(9)) {

					startingPosition = CENTER;

				} else if (Robot.logitechJoystickL.getRawButton(11)) {

					startingPosition = RIGHT;

				}

				if (Robot.logitechJoystickL.getRawButton(8)) {

					strategy = SCORE_SWITCH;

				} else if (Robot.logitechJoystickL.getRawButton(10)) {

					strategy = SCORE_BOTH;

				}

				SmartDashboard.putString("Selection mode", selectionModeArray[selectionMode]);
				SmartDashboard.putString("Starting Position", startPositionArray[startingPosition]);
				SmartDashboard.putString("Auto mode", strategyArray[strategy]);

				Timer.delay(0.1);
				SmartDashboard.putBoolean("AutoSelector Error", false);

				if (breakLoop) {
					break;
				}
			}
		} catch (Exception autoSelectorError) {

			SmartDashboard.putBoolean("AutoSelector Error", true);
			SmartDashboard.putString("Auto ERROR", autoSelectorError.getMessage());
		}

	}

}
