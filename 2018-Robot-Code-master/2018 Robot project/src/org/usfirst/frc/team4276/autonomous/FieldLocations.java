package org.usfirst.frc.team4276.autonomous;

public class FieldLocations {

	public static double[][] SCALE_LOCATION = new double[2][2];

	public static final double[] CENTER_START_POSITION = new double[] { 1.42, 13.5 };
	public static final double[] LEFT_START_POSITION = new double[] { 1.42, 22 };
	public static final double[] RIGHT_START_POSITION = new double[] { 1.42, 5 };

	public static final double[] CENTER_DRIVE_OFF_WALL = new double[] { 4.42, 13.5 };
	public static final double[] CROSS_BASE_LINE = new double[] { 27, 13.5 };
	public static final double[] SWITCH_PREP_LEFT_A = new double[] { 7.2, 17 };
	public static final double[] SWITCH_PREP_RIGHT_A = new double[] { 7.2, 10 };
	public static final double[] SWITCH_PREP_LEFT_B = new double[] { 14, 22 };
	public static final double[] SWITCH_PREP_RIGHT_B = new double[] { 14, 5 };
	public static final double[] CUBE_PYRAMID = new double[] { 10, 13.5 };

	public static final double[] leftScaleLocation = new double[] { 27, 19.5 };
	public static final double[] rightScaleLocation = new double[] { 27, 7.5 };
	public static final double[] leftSwitchLocation = new double[] { 14, 17 };
	public static final double[] rightSwitchLocation = new double[] { 14, 8 };
	public static final double[] leftCornerCubeLocation = new double[] { 16.9, 19.3 };
	public static final double[] rightCornerCubeLocation = new double[] { 16.9, 7.6 };

	public static final double[] leftScaleScoringZoneA = new double[] { 24, 20 };
	public static final double[] rightScaleScoringZoneA = new double[] { 24, 7 };
	public static final double[] leftSwitchScoringZoneA = new double[] { 12, 18 };
	public static final double[] rightSwitchScoringZoneA = new double[] { 12, 9 };
	public static final double[] leftScaleScoringZoneB = new double[] { 27, 21.7 };
	public static final double[] rightScaleScoringZoneB = new double[] { 27, 5.3 };
	public static final double[] leftSwitchScoringZoneB = new double[] { 13.9, 20.6 };
	public static final double[] rightSwitchScoringZoneB = new double[] { 13.9, 6.4 };
	public static final double[] SWITCH_SCORING_ZONE_LEFT_C = new double[] { 17, 18 };
	public static final double[] SWITCH_SCORING_ZONE_RIGHT_C = new double[] { 17, 9 };
	public static final double[] LEFT_CROSS_ZONE = new double[] { 20, 21 };
	public static final double[] RIGHT_CROSS_ZONE = new double[] { 20, 6 };
	public static final double[] LEFT_JUNCTION = new double[] { 20, 18 };
	public static final double[] RIGHT_JUNCTION = new double[] { 20, 9 };

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int X = 0;
	public static final int Y = 1;

	public void inititalize() {
		SCALE_LOCATION[LEFT][X] = 27;
		SCALE_LOCATION[LEFT][Y] = 19.5;

		// SCALE_LOCATION[RT][X] = 27;
		// SCALE_LOCATION[LEFT][Y] = 19.5;
	}

}
