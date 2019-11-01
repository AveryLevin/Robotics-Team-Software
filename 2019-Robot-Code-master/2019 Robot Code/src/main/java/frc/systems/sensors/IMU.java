/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.systems.sensors;

import com.analog.adis16448.frc.ADIS16448_IMU;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IMU {

    ADXRS450_Gyro imu;

    // ADIS16448_IMU imu;

    public IMU() {

        imu = new ADXRS450_Gyro();
        // imu = new ADIS16448_IMU();
        imu.reset();
        imu.calibrate();
    }

    public double getYaw() {
        return imu.getAngle();
        // return imu.getYaw();
    }

    public void giveReadouts() {
        SmartDashboard.putNumber("Yaw", imu.getAngle());

    }
}
