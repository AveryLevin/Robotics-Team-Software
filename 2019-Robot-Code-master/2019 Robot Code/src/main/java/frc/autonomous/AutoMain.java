/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.autonomous.DashboardInterface.Position;
import frc.robot.Robot;
import frc.systems.DriveSystem.Gear;
import frc.utilities.SoftwareTimer;

/**
 * This is where the main auto sequences will be run
 */
public class AutoMain {

    SoftwareTimer phaseTimer;

    // current state definitions
    private static final int INIT = 0;
    private static final int DRIVE_OFF_PLATFORM = 1;
    private static final int DRIVE_PATH = 2;
    private static final int DEPLOY_HATCH = 3;
    private static final int DEPLOY_CARGO = 4;
    private static final int EXIT = 5;

    private boolean[] stateEnabled = new boolean[EXIT + 1];

    static int currentState = INIT; // always runs init first
    String currentStateName;

    // command return status
    private boolean reachedDestination = false;
    private boolean performStateEntry = true;

    // Path name to run
    private String drivePath;

    // constants

    public AutoMain() {
        phaseTimer = new SoftwareTimer();
    }

    public void defineStateEnabledStatus() {
        stateEnabled[DRIVE_OFF_PLATFORM] = Robot.mSDBInterface.getDRIVE_OFF_PLATFORM();
        stateEnabled[DRIVE_PATH] = Robot.mSDBInterface.getDRIVE_PATH();
        stateEnabled[DEPLOY_HATCH] = Robot.mSDBInterface.getDEPLOY_HATCH();
        stateEnabled[DEPLOY_CARGO] = Robot.mSDBInterface.getDEPLOY_CARGO();
    }

    public void planRoute() {
        DashboardInterface.Position start = Robot.mSDBInterface.getStartPosition();
        DashboardInterface.Position target = Robot.mSDBInterface.getStartPosition();

        switch (start) {
        case LEFT:
            switch (target) {
            case LEFT:
                drivePath = "leftStartToSide";
                break;

            case RIGHT:
                drivePath = "leftStartToFrontRight";
                break;

            case CENTER:
                drivePath = "leftStartToFrontLeft";
                break;

            default:
                stateEnabled[DRIVE_PATH] = false;
                break;
            }
            break;

        case RIGHT:
            switch (target) {
            case LEFT:
                drivePath = "rightStartToFrontLeft";
                break;

            case RIGHT:
                drivePath = "rightStartToSide";
                break;

            case CENTER:
                drivePath = "rightStartToFrontRight";
                break;

            default:
                stateEnabled[DRIVE_PATH] = false;
                break;
            }
            break;

        case CENTER:
            switch (target) {
            case LEFT:
                drivePath = "MidToFrontLeft";
                break;

            case RIGHT:
                drivePath = "MidToFrontRight";
                break;

            case CENTER:
                drivePath = "MidToFrontLeft";
                break;

            default:
                stateEnabled[DRIVE_PATH] = false;
                break;
            }
            break;

        default:
            stateEnabled[DRIVE_PATH] = false;
            break;
        }
    }

    public void init() {

    }

    public void loop() {
        switch (currentState) {
        case INIT:
            currentStateName = "Init";
            // State entry
            if (performStateEntry) {

                planRoute();
                defineStateEnabledStatus();

                Robot.mDriveSystem.shiftGear(Gear.HI);
                phaseTimer.setTimer(0.2);
                performStateEntry = false;

            }

            // State processing

            // State exit
            if (phaseTimer.isExpired()) {

                performStateExit();
            }
            break;

        case DRIVE_OFF_PLATFORM:
            currentStateName = "Drive off Platform";
            // State entry
            if (performStateEntry) {

                phaseTimer.setTimer(0.5);
                performStateEntry = false;

            }

            // State processing

            // State exit
            if (phaseTimer.isExpired()) {

                performStateExit();
            }
            break;

        case DRIVE_PATH:
            currentStateName = "Drive Path";
            // State entry
            if (performStateEntry) {

                phaseTimer.setTimer(5);
                performStateEntry = false;

            }

            // State processing

            // State exit
            if (phaseTimer.isExpired()) {

                performStateExit();
            }
            break;

        case DEPLOY_HATCH:
            currentStateName = "Deploy Hatch";
            // State entry
            if (performStateEntry) {

                phaseTimer.setTimer(.5);
                performStateEntry = false;

            }

            // State processing

            // State exit
            if (phaseTimer.isExpired()) {

                performStateExit();
            }
            break;

        case DEPLOY_CARGO:
            currentStateName = "Deploy Cargo";
            // State entry
            if (performStateEntry) {

                phaseTimer.setTimer(2);
                performStateEntry = false;

            }

            // State processing

            // State exit
            if (phaseTimer.isExpired()) {

                performStateExit();
            }
            break;
        case EXIT:

            currentStateName = "Autonomous Done";
            break;
        }
    }

    private void performStateExit() {
        if (phaseTimer.isExpired()) {
            SmartDashboard.putString("Exit Status", currentStateName + "time expired");
        } else {
            SmartDashboard.putString("Exit Status", currentStateName + "target reached");
        }
        reachedDestination = false;
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
