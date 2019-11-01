/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.utilities;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Add your docs here.
 */
public class Constants {

    public final static int F = 0;
    public final static int P = 1;
    public final static int I = 2;
    public final static int D = 3;

    // for drive shifting gearbox

    public final static double SHIFT_TIME = 0.05; // sec

    public final static Value HI_GEAR_VALUE = DoubleSolenoid.Value.kForward;
    public final static Value LO_GEAR_VALUE = DoubleSolenoid.Value.kReverse;

    public final static double[] regDrivePIDs = { 0, 0.03, 0.0, 0.0 }; // F = 0, P = 0.03, I = 0, D = 0

    /**
     * Set to zero to skip waiting for confirmation. Set to nonzero to wait and
     * report to DS if action fails.
     */
    public final static int CAN_TimeoutMs = 30;

    public final static int PID_PRIMARY = 0;

    /**
     * for Pathfinder
     */

    public final static double MAX_ACCELERATION = 24.130538058; // ft/s/s
	public final static double MAX_VELOCITY = 7.2289156627297; // ft/s
	public final static double MAX_JERK = 60.0; // ft/s/s/s
	public final static double WHEEL_BASE = 2.132546; // ft
	public static double kP = 0.0;
	public static double kI = 0.0;
	public static double kD = 0.0;
	public static double kV = 1 / MAX_VELOCITY;
	public static double kA = 0.002;
	public final static double dT = .01;//needs adjusting
	public final static int TickPRev = 1907;
	public final static double wheelDiam = 0.5249344; // ft
}
