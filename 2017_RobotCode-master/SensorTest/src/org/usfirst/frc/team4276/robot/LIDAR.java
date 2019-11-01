package org.usfirst.frc.team4276.robot;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class LIDAR implements PIDSource {
	public String name;
	private int _nSequenceLidar = 0;

	public int nSequenceLidar() {
		return _nSequenceLidar;
	}

	private int _lidarDistanceCentimeters = 0;

	public int lidarDistanceCentimeters() {
		return _lidarDistanceCentimeters;
	}

	private I2C _i2c;
	private byte[] _distance;
	private java.util.Timer _updater;
	private LIDARUpdater _task;
	private int _samples = 0, errors = 0;

	private final int LIDAR_CONFIG_REGISTER = 0x00;
	private final int LIDAR_DISTANCE_REGISTER = 0x8f;

	public LIDAR(String nam, Port port, int addr) {
		try {
			name = nam;
			_i2c = new I2C(port, addr);

			_distance = new byte[2];

			_task = new LIDARUpdater();
			_updater = new java.util.Timer();
		} catch (Exception e) {
			SmartDashboard.putString("debug", "LIDAR constructor failed");
		}
	}

	// Distance in cm
	public synchronized int getDistance() {
		return (int) Integer.toUnsignedLong(_distance[0] << 8) + Byte.toUnsignedInt(_distance[1]);
	}

	public synchronized double pidGet() {
		return getDistance();
	}

	// Start 10Hz polling
	public synchronized void start() {
		_updater.scheduleAtFixedRate(_task, 0, 1000);
	}

	// Start polling for period in milliseconds
	public synchronized void start(int period) {
		_updater.scheduleAtFixedRate(_task, 0, period);
	}

	public synchronized void stop() {
		_updater.cancel();
	}

	// Update distance variable
	public synchronized void update() {

		_i2c.write(LIDAR_CONFIG_REGISTER, 0x4); // Initiate measurement
		Timer.delay(0.012); // Delay for measurement to be taken

		SmartDashboard.putBoolean(name + " LIDAR Aborted", _i2c.read(LIDAR_DISTANCE_REGISTER, 2, _distance)); // Read
		// in
		// measurement
		_samples++;
		SmartDashboard.putNumber(name + " LIDAR Counter", _samples);
		if (getDistance() > 0) {
			_nSequenceLidar++;
			_lidarDistanceCentimeters = getDistance();
			SmartDashboard.putNumber(name + " LIDAR Distance", getDistance());
		} else
			errors++;
		SmartDashboard.putNumber(name + " Errors", errors);

	}

	// Timer task to keep distance updated
	private class LIDARUpdater extends TimerTask {
		public void run() {
			while (true) {
				update();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub

	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return null;
	}
}