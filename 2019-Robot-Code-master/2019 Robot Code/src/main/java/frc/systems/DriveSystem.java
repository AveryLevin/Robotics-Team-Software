/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.systems;

import frc.robot.Robot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Notifier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;

import frc.utilities.Constants;
import frc.utilities.SoftwareTimer;
import frc.utilities.Toggler;
import frc.utilities.LogJoystick;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import jaci.pathfinder.followers.EncoderFollower;

public class DriveSystem {

    private EncoderFollower m_left_follower;
    private EncoderFollower m_right_follower;

    private Encoder m_left_encoder;
    private Encoder m_right_encoder;

    private boolean hasCANNetwork = false;

    DoubleSolenoid gearShifter;
    public final int HI_SHIFTER = 4;
    public final int LO_SHIFTER = 3;
    SoftwareTimer shiftTimer;
    SoftwareTimer driveTimer;
    private boolean shiftInit = true;
    private boolean isShifting = false;
    private boolean isDeploying = false;
    private Gear currentGear = Gear.HI;
    private boolean brakeModeisEngaged = true;
    private final DriveMode DEFAULT_MODE = DriveMode.TANK;
    private DriveMode currentMode = DEFAULT_MODE;
    private String currentMode_s = "Arcade";

    VictorSP flDrive, mlDrive, blDrive, frDrive, mrDrive, brDrive;
    VictorSPX flDriveX, mlDriveX, blDriveX, frDriveX, mrDriveX, brDriveX;
    private double leftPower = 0;
    private double rightPower = 0;
    private double hatchDeployHiGear = -0.25;
    private double hatchDeployLoGear = -0.5;
    private double climbDelayTime = 1.5;// seconds
    private double driveFDelay = 0.8;
    private double retractDelayTime = 2;// seconds
    private double pullupDelayTime = 1;// seconds
    private double contDelayTime = 1.5;// seconds
    private double climbPower = .1;// seconds
    private int timerNum = 0;
    double desired_heading = 0;
    int count = 0;
    private Toggler modeToggler;
    public boolean methodInit = true;
    private Notifier m_follower_notifier;

    double deadband = 0.05;

    public DriveSystem(boolean isCAN, int FLport, int MLport, int BLport, int FRport, int MRport, int BRport,
            int shifterHi, int shifterLo, int m_right_encoderPortA, int m_right_encoderPortB, int m_left_encoderPortA,
            int m_left_encoderPortB) {

        modeToggler = new Toggler(LogJoystick.B1);
        modeToggler.setMechanismState(true); // sets to bracke mode

        shiftTimer = new SoftwareTimer();
        driveTimer = new SoftwareTimer();
        m_right_encoder = new Encoder(m_right_encoderPortA, m_right_encoderPortB);
        m_left_encoder = new Encoder(m_left_encoderPortA, m_left_encoderPortB);
        if (isCAN) {
            hasCANNetwork = true;

            flDriveX = new VictorSPX(FLport);
            mlDriveX = new VictorSPX(MLport);
            blDriveX = new VictorSPX(BLport);
            frDriveX = new VictorSPX(FRport);
            mrDriveX = new VictorSPX(MRport);
            brDriveX = new VictorSPX(BRport);
        } else {
            hasCANNetwork = false;

            flDrive = new VictorSP(FLport);
            mlDrive = new VictorSP(MLport);
            blDrive = new VictorSP(BLport);
            frDrive = new VictorSP(FRport);
            mrDrive = new VictorSP(MRport);
            brDrive = new VictorSP(BRport);
        }

        gearShifter = new DoubleSolenoid(shifterHi, shifterLo);

    }

    /**
     * 
     * @param rightPow right motor power
     * @param leftPow  left motor power
     */

    public void assignMotorPower(double rightPow, double leftPow) {
        if (hasCANNetwork) {
            SmartDashboard.putBoolean("Drive check", true);
            flDriveX.set(ControlMode.PercentOutput, leftPow);
            mlDriveX.set(ControlMode.PercentOutput, leftPow);
            blDriveX.set(ControlMode.PercentOutput, leftPow);
            frDriveX.set(ControlMode.PercentOutput, rightPow);
            mrDriveX.set(ControlMode.PercentOutput, rightPow);
            brDriveX.set(ControlMode.PercentOutput, rightPow);

        } else {
            flDrive.set(leftPow);
            mlDrive.set(leftPow);
            blDrive.set(leftPow);
            frDrive.set(rightPow);
            mrDrive.set(rightPow);
            brDrive.set(rightPow);
        }
        rightPower = rightPow;
        leftPower = leftPow;
    }

    /**
     * manual operator controlled drive
     */

    public void operatorDrive() {

        changeMode();
        checkForGearShift();

        if (Robot.rightJoystick.getRawButton(1)) {
            currentMode = DriveMode.AUTO;

        } else if (Robot.rightJoystick.getRawButton(2)) {
            currentMode = DriveMode.CLIMB;
        } else {
            currentMode = DEFAULT_MODE;
        }

        isDeploying = false;

        if (currentMode == DriveMode.AUTO) {
            currentMode_s = "Auto";
        } else if (currentMode == DriveMode.ARCADE) {
            currentMode_s = "Arcade";
        } else {
            currentMode_s = "Tank";
        }

        double leftY = 0;
        double rightY = 0;

        switch (currentMode) {

        case AUTO:
            rotateCam(4, Robot.visionTargetInfo.visionPixelX);

            // driveFwd(4, .25);

            break;

        case CLIMB:

            climb();

            break;

        case ARCADE:
            resetAuto();
            double linear = 0;
            double turn = 0;

            if (Math.abs(Robot.rightJoystick.getY()) > deadband) {
                linear = -Robot.rightJoystick.getY();
            }
            if (Math.abs(Robot.leftJoystick.getX()) > deadband) {
                turn = Math.pow(Robot.leftJoystick.getX(), 3);
            }

            leftY = -linear - turn;
            rightY = linear - turn;
            if (!isShifting) {
                assignMotorPower(rightY, leftY);
            } else {

                assignMotorPower(0, 0);
            }

            break;

        case TANK:

            resetAuto();
            if (Math.abs(Robot.rightJoystick.getY()) > deadband) {
                rightY = -Math.pow(Robot.rightJoystick.getY(), 3 / 2);
            }
            if (Math.abs(Robot.leftJoystick.getY()) > deadband) {
                leftY = Math.pow(Robot.leftJoystick.getY(), 3 / 2);
            }
            if (!isShifting) {
                assignMotorPower(rightY, leftY);
            } else {

                assignMotorPower(0, 0);
            }
            break;

        default:
            break;
        }

        updateTelemetry();
    }

    /**
     * Checks for joystick input to shift gears. Manages to logic and timing to not
     * power drive motors while shifting
     */
    public void checkForGearShift() {
        boolean shiftHi = Robot.leftJoystick.getRawButton(HI_SHIFTER);
        boolean shiftLo = Robot.leftJoystick.getRawButton(LO_SHIFTER);

        if (shiftHi) {
            currentGear = Gear.HI;
            if (shiftInit) {
                shiftTimer.setTimer(Constants.SHIFT_TIME);
                shiftInit = false;
            }
            if (shiftTimer.isExpired()) {
                isShifting = false;
                shiftInit = true;
            } else {
                isShifting = true;
            }
            gearShifter.set(Constants.HI_GEAR_VALUE);
        } else if (shiftLo) {
            currentGear = Gear.LO;
            if (shiftInit) {
                shiftTimer.setTimer(Constants.SHIFT_TIME);
                shiftInit = false;
            }
            if (shiftTimer.isExpired()) {
                isShifting = false;
                shiftInit = true;
            } else {
                isShifting = true;
            }
            gearShifter.set(Constants.LO_GEAR_VALUE);
        } else {
            isShifting = false;
        }

    }

    /**
     * current gear status
     */

    public enum Gear {
        HI, LO
    }

    public enum DriveMode {
        TANK, ARCADE, AUTO, CLIMB
    }

    /**
     * 
     * @param shiftTo desired gear
     */
    public void shiftGear(Gear shiftTo) {
        boolean shiftHi = false;
        boolean shiftLo = false;

        currentGear = shiftTo;

        if (shiftTo == Gear.HI) {
            shiftHi = true;
        } else {
            shiftLo = true;
        }

        if (shiftHi) {
            if (shiftInit) {
                shiftTimer.setTimer(Constants.SHIFT_TIME);
                shiftInit = false;
            }
            if (shiftTimer.isExpired()) {
                isShifting = false;
                shiftInit = true;
            } else {
                isShifting = true;
            }
            gearShifter.set(Constants.HI_GEAR_VALUE);
        } else if (shiftLo) {
            if (shiftInit) {
                shiftTimer.setTimer(Constants.SHIFT_TIME);
                shiftInit = false;
            }
            if (shiftTimer.isExpired()) {
                isShifting = false;
                shiftInit = true;
            } else {
                isShifting = true;
            }
            gearShifter.set(Constants.LO_GEAR_VALUE);
        } else {
            isShifting = false;
        }

    }

    public void changeMode() {
        modeToggler.updateMechanismStateLJoy();
        brakeModeisEngaged = modeToggler.getMechanismState();
        if (brakeModeisEngaged) {
            flDriveX.setNeutralMode(NeutralMode.Brake);
            mlDriveX.setNeutralMode(NeutralMode.Brake);
            blDriveX.setNeutralMode(NeutralMode.Brake);
            frDriveX.setNeutralMode(NeutralMode.Brake);
            mrDriveX.setNeutralMode(NeutralMode.Brake);
            brDriveX.setNeutralMode(NeutralMode.Brake);
        } else {
            flDriveX.setNeutralMode(NeutralMode.Coast);
            mlDriveX.setNeutralMode(NeutralMode.Coast);
            blDriveX.setNeutralMode(NeutralMode.Coast);
            frDriveX.setNeutralMode(NeutralMode.Coast);
            mrDriveX.setNeutralMode(NeutralMode.Coast);
            brDriveX.setNeutralMode(NeutralMode.Coast);
        }

    }

    /**
     * 
     * @param targetTime      distance to travel
     * @param powerMultiplier from 0 to 1
     * @return status of action (complete)
     */
    public boolean driveFwd(double targetTime, double power) {
        double followHeading = 0;
        if (methodInit) {
            driveTimer.setTimer(targetTime);
            followHeading = Robot.mImu.getYaw();
            methodInit = false;
        }
        // establish gains
        double P_turn = Constants.regDrivePIDs[Constants.P];

        double heading_difference = followHeading - Robot.mImu.getYaw();

        double turn = P_turn * heading_difference;

        assignMotorPower(power, -power);

        return false;
    }

    public boolean climb() {

        double stage1pow = .25;
        double stage2pow = .3;

        // 1. bring arm down
        Robot.mArmPivot.commandSetpoint(-90);

        if (methodInit) {
            // start timer for jack extend
            driveTimer.setTimer(climbDelayTime);

            methodInit = false;

            timerNum = 0;
            

            SmartDashboard.putNumber("init", 1);
        }

        if (driveTimer.isExpired()) {

            SmartDashboard.putNumber("sequence:", timerNum);
            switch (timerNum) {

            case 0:
                // 1. extend jack and drive
                assignMotorPower(stage1pow, -stage1pow);// creep forward while bring arm down
                timerNum++;

                // wait for piston to extend
                driveTimer.setTimer(driveFDelay);
                break; // timer for arm down expired

            case 1:
                // 2. extend jack and drive
                Robot.mClimbingJack.setJack(true);
                assignMotorPower(stage1pow, -stage1pow);// creep forward while lifting back end
                timerNum++;

                // wait for piston to extend
                driveTimer.setTimer(pullupDelayTime);
                break; // timer for arm down expired

            case 2:
                // 3. drive foward
                assignMotorPower(stage2pow, -stage2pow);// drive forward once back end up
                timerNum++;

                // wait to drive forward
                driveTimer.setTimer(retractDelayTime);
                break;// timer for piston extension expired

            case 3:
                // 4. retract jack
                Robot.mClimbingJack.setJack(false);
                assignMotorPower(stage1pow, -stage1pow);// creep forward while lifting piston
                timerNum++;

                // continue to drive forward
                driveTimer.setTimer(contDelayTime);
                break; // timer for piston retract expired

            case 4:
                // 5. stop all
                assignMotorPower(0, 0); // full stop
                return true;
            // break;

            default:
                assignMotorPower(0, 0); // full stop
                return false;
            // break;
            }
        } // some timer has expired
        return false;
    }

    public void resetAuto() {
        methodInit = true;
        timerNum = 1;
    }

    public boolean rotate(double targetTime, double desired_heading) {

        if (methodInit) {
            driveTimer.setTimer(targetTime);
            methodInit = false;
        }
        double P_turn = Constants.regDrivePIDs[Constants.P];
        double heading_difference = desired_heading - Robot.mImu.getYaw();
        double turn = P_turn * heading_difference;

        assignMotorPower(-turn, -turn);
        SmartDashboard.putNumber("Turn", turn);
        SmartDashboard.putNumber("diff", heading_difference);

        if (driveTimer.isExpired()) {
            assignMotorPower(0, 0);
            methodInit = true;
            return true;
        }
        return false;
    }

    public boolean rotateCam(double targetTime, double targetPixel) {

        double leftbias = 1.01;
        double rightbias = 1.0;
        double degppixel = (0.170615413);// tentative
        double centerPixel = 250;

        if (methodInit) {
            if (Robot.visionTargetInfo.isCargoBayDetected != 0) {
                desired_heading = Robot.mImu.getYaw() + ((targetPixel - centerPixel) * degppixel);
                driveTimer.setTimer(targetTime);
                methodInit = false;
            } // if found vision target then calculate heading and prevent init repeat
            else {
                desired_heading = Robot.mImu.getYaw();
            } // if target not found hold current heading

            SmartDashboard.putBoolean("reached init", true);
            SmartDashboard.putNumber("target", targetPixel);
            // log how many times init runs
            // it repeats if doesnt find target
            count++;
            SmartDashboard.putNumber("count", count);
        }
        double P_turn = Constants.regDrivePIDs[Constants.P]; // full power at ~30 deg offset
        double heading_difference = desired_heading - Robot.mImu.getYaw();
        double turn = P_turn * heading_difference;
        double linear = Robot.rightJoystick.getY();
        double manTurn = 0;
        if (Math.abs(Robot.rightJoystick.getTwist()) > 0.4) {
            manTurn = Robot.rightJoystick.getTwist() / 3;
        }
        SmartDashboard.putNumber("Turn", turn);
        SmartDashboard.putNumber("diff", heading_difference);
        SmartDashboard.putNumber("goal", desired_heading);
        SmartDashboard.putNumber("Manual turn", manTurn);

        assignMotorPower(-linear * rightbias - turn - manTurn, linear * leftbias - turn - manTurn);

        return false;
    }

    public boolean drivePath(String pathFileName) {

        Trajectory left_trajectory = PathfinderFRC.getTrajectory(pathFileName + ".right");
        Trajectory right_trajectory = PathfinderFRC.getTrajectory(pathFileName + ".left");

        m_left_follower = new EncoderFollower(left_trajectory);
        m_right_follower = new EncoderFollower(right_trajectory);

        m_left_follower.configureEncoder(m_left_encoder.get(), Constants.TickPRev, Constants.wheelDiam);
        // You must tune the PID values on the following line!
        m_left_follower.configurePIDVA(Constants.kP, Constants.kI, Constants.kD, Constants.kV, Constants.kA);

        m_right_follower.configureEncoder(m_right_encoder.get(), Constants.TickPRev, Constants.wheelDiam);
        // You must tune the PID values on the following line!
        m_right_follower.configurePIDVA(Constants.kP, Constants.kI, Constants.kD, Constants.kV, Constants.kA);

        m_follower_notifier = new Notifier(this::followPath);
        m_follower_notifier.startPeriodic(left_trajectory.get(0).dt);

        return false;
    }

    private void followPath() {
        if (m_left_follower.isFinished() || m_right_follower.isFinished()) {
            m_follower_notifier.stop();
        } else {

            double LVT = m_left_follower.getSegment().velocity;
            double RVT = m_right_follower.getSegment().velocity;

            double LVA = m_left_encoder.getRate();
            double RVA = m_right_encoder.getRate();

            double LPT = m_left_follower.getSegment().position;
            double RPT = m_right_follower.getSegment().position;

            double LPA = m_left_encoder.getDistance();
            double RPA = m_right_encoder.getDistance();

            double left_speed = m_left_follower.calculate(m_left_encoder.get());
            double right_speed = m_right_follower.calculate(m_right_encoder.get());
            double heading = Robot.mImu.getYaw();
            double desired_heading = Pathfinder.r2d(m_left_follower.getHeading());
            double heading_difference = Pathfinder.boundHalfDegrees(desired_heading - heading);

            double turn = 0.8 * (-1.0 / 80.0) * heading_difference;

            SmartDashboard.putNumber("Left goal velocity", LVT);
            SmartDashboard.putNumber("Right goal velocity", RVT);

            SmartDashboard.putNumber("Left actual velocity", LVA);
            SmartDashboard.putNumber("Right actual velocity", RVA);

            SmartDashboard.putNumber("Left velocity difference", (LVT - LVA));
            SmartDashboard.putNumber("Right velocity difference", (RVT - RVA));

            SmartDashboard.putNumber("Left goal position", LPT);
            SmartDashboard.putNumber("Right goal position", RPT);

            SmartDashboard.putNumber("Left actual position", LPA);
            SmartDashboard.putNumber("Right actual position", RPA);

            SmartDashboard.putNumber("Left position difference", (LPT - LPA));
            SmartDashboard.putNumber("Right position difference", (RPT - RPA));

            SmartDashboard.putNumber("heading error:", heading_difference);
            SmartDashboard.putNumber("turn power:", turn);

            assignMotorPower(right_speed - turn, left_speed + turn);
        }
    }

    /*
     * () public void reset(){ assignMotorPower(rightPow, leftPow); }
     */
    /**
     * updates smartdashboard
     */
    public void updateTelemetry() {
        SmartDashboard.putNumber("Heading", Robot.mImu.getYaw());
        // encoder outputs
        SmartDashboard.putNumber("Right Encoder", m_right_encoder.getDistance());
        SmartDashboard.putNumber("Left Encoder", m_left_encoder.getDistance());
        // shifting status
        SmartDashboard.putBoolean("Shifting", isShifting);
        SmartDashboard.putString("Drive Mode", currentMode_s);
        // current gear
        SmartDashboard.putBoolean("HI Gear", (currentGear == Gear.HI));
        SmartDashboard.putBoolean("LOW Gear", (currentGear == Gear.LO));
        // power outputs
        SmartDashboard.putNumber("Right Power", rightPower);
        SmartDashboard.putNumber("Left Power", leftPower);

        // Deploy hatch
        SmartDashboard.putBoolean("Hatch Back Up", isDeploying);

        // Check Coast/Brake
        SmartDashboard.putBoolean("Brake Mode", brakeModeisEngaged);
    }

}
