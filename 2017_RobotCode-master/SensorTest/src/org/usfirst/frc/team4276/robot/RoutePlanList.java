package org.usfirst.frc.team4276.robot;

import java.util.ArrayList;

//Field coordinates:
//  origin (0,0) is at the center of the field
//  North (0.0 degrees) is toward the end of the field with the blue alliance boiler
//  Drivers for the blue alliance will face south (180 deg), with positive Y coordinates that decrease to zero as position approaches mid field
//  Drivers for the red alliance face north (0.0 deg), and have negative Y coordinates that increase to zero as position approaches mid field
//
//  Field coordinates are polar relative to the nearest boiler.
//
//  RobotPositionPolar are (isBlueBoiler, radius, hdgBoiler, yawRobot),
//  where hdgBoiler 0.0 is field frame north, and yawRobot is relative to hdgBoiler
//
//         Blue Drivers       
//  ************************* Blue Boiler
//  *        (0, 12)        *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *    ^  North (0.0 degrees)
//  *                       *   /^\
//  *                       *    |
//  *                       *    +
//  *                       *   +++
//  *(-12, 0) (0,0)  (12, 0)*    +
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *                       *
//  *       (0, -12)        *
//  ************************* Red Boiler
//        Red Drivers

@SuppressWarnings("serial")
public class RoutePlanList extends ArrayList<RoutePlan> {

	// TODO: Determine the exact coordinates for position constants, which are
	// defined only approximately below

	public static final double blueBoiler_X = 13.5;
	public static final double blueBoiler_Y = 27.166667;
	public static final double redBoiler_X = 13.5;
	public static final double redBoiler_Y = -27.166667;

	// Approximately one foot front and center of Boiler
	public static final RobotPositionPolar posBlueBoiler = new RobotPositionPolar(true, 1.5, 45.0, 0.0);
	public static final RobotPositionPolar posRedBoiler = new RobotPositionPolar(false, 1.5, 135.0, 0.0);

	// Feeder
	public static final RobotPositionPolar posBlueFeeder = new RobotPositionPolar(false, 18.5, 90.0, 90.0);
	public static final RobotPositionPolar posRedFeeder = new RobotPositionPolar(true, 18.5, -90.0, -90.0);

	// Center lift and center rope
	public static final RobotPositionPolar posBlueLift_C = new RobotPositionPolar(true, 18.0, 30.0, 60.0);
	public static final RobotPositionPolar posRedLift_C = new RobotPositionPolar(false, 18.0, 120.0, -120.0);

	// Second lift (on the side toward the boiler - can't see the boiler from
	// the other lift)
	public static final RobotPositionPolar posBlueLift_2 = new RobotPositionPolar(true, 16.0, 45.0, -60.0);
	public static final RobotPositionPolar posRedLift_2 = new RobotPositionPolar(false, 16.0, 135.0, 105.0);

	// Second rope (on the side toward the boiler - can't see the boiler from
	// the other rope)
	public static final RobotPositionPolar posBlueRope_2 = new RobotPositionPolar(true, 22.0, 30.0, -90.0);
	public static final RobotPositionPolar posRedRope_2 = new RobotPositionPolar(false, 22.0, -30.0, 90.0);

	// Hopper next to the boiler (can't see vision from hoppers on the other
	// side of the field)
	public static final RobotPositionPolar posBlueHopper = new RobotPositionPolar(true, 12.0, 0.0, 0.0);
	public static final RobotPositionPolar posRedHopper = new RobotPositionPolar(false, 12.0, 180.0, 180.0);

	// Waypoint between Lift_C and Hopper so can move from one to the other
	// without hitting the airship
	public static final RobotPositionPolar posBlueWaypoint = new RobotPositionPolar(true, 8.0, 135.0, 135.0);
	public static final RobotPositionPolar posRedWaypoint = new RobotPositionPolar(false, 8.0, -45.0, 45.0);

	public static final double hdgStartToBlueLift_C = 180.0;
	public static final double hdgStartToRedLift_C = 0.0;
	public static final double feetStartToLift_C = 6.5;

	public RoutePlanList() {

		// This is an example of an autonomous task that does not rely on the
		// vision system
		RoutePlan bluePlaceGear = new RoutePlan("Blue Place Gear ");
		bluePlaceGear.add(new RouteTask(RouteTask.Operation.DRIVE, hdgStartToBlueLift_C, feetStartToLift_C));
		bluePlaceGear.add(new RouteTask(RouteTask.Operation.DRIVE, hdgStartToBlueLift_C, -1.0));
		bluePlaceGear.add(new RouteTask(RouteTask.Operation.STOP));
		add(bluePlaceGear);

		// Same autonomous task using the vision system
		RoutePlan bluePlaceGearUsingVision = new RoutePlan("Blue Place Gear Using Vision");
		bluePlaceGearUsingVision.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueLift_C));
		bluePlaceGearUsingVision.add(new RouteTask(RouteTask.Operation.PLACE_GEAR, posBlueLift_C));
		bluePlaceGearUsingVision.add(new RouteTask(RouteTask.Operation.STOP));
		add(bluePlaceGearUsingVision);

		// Vision based navigation makes much more complex sequences possible
		// because positions are maeasured as you go instead of starting from a
		// known place and accumulating errors.
		RoutePlan bluePlaceGearGetHopperAndShoot = new RoutePlan("Blue Place Gear Get Hopper And Shoot");
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueLift_C));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.PLACE_GEAR, posBlueLift_C));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueLift_C));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueWaypoint));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueHopper));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.WAIT, 1000));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.COLLECT_FUEL));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.DRIVE_VISION_ARC, posBlueBoiler));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.COLLECT_FUEL_STOP));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.STRAFE_ALIGN_BOILER, posBlueBoiler));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.SHOOT_BOILER, posBlueBoiler));
		bluePlaceGearGetHopperAndShoot.add(new RouteTask(RouteTask.Operation.STOP));
		add(bluePlaceGearGetHopperAndShoot);

	}
}
