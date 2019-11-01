package org.usfirst.frc.team4276.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class PIXY {
	public String name;
	private static final int MAX_FRAMES = 10;
	private I2C i2c;
	private java.util.Timer updatePixy;

	public PIXY(String nam, Port port, int addr) {
		try {
			
			name = nam;
			i2c = new I2C(port, addr);			
		} catch (Exception e) {
			SmartDashboard.putString("debug", "PIXY constructor failed");
		}
	}
	
	public class Frame {
		// 0, 1 0 sync (0xaa55)
		// 2, 3 1 checksum (sum of all 16-bit words 2-6)
		// 4, 5 2 signature number
		// 6, 7 3 x center of object
		// 8, 9 4 y center of object
		// 10, 11 5 width of object
		// 12, 13 6 height of object

		int sync = 0;
		int checksum = 0;
		public int signature;
		public int xCenter;
		public int yCenter;
		public int width;
		public int height;
	}

	public ArrayList<Frame> getFrames() throws IOException {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		byte[] bytes = new byte[14 * MAX_FRAMES];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = 0;
		}

		// wait for sync
		if (!i2c.read(0, 1, bytes)) {
			return frames;
		}
		if (bytes[0] != 0x55) {
			return frames;
		}
		if (!i2c.read(0, 1, bytes)) {
			return frames;
		}
		if (bytes[0] != 0xaa) {
			return frames;
		}

		// read frames.
		if (!i2c.read(0, 14, bytes)) {
			return frames;
		}
		while (frames.size() <= MAX_FRAMES) {
			Frame frame = new Frame();
			frame.sync = convertBytesToInt(bytes[1], bytes[0]);
			// System.out.println("\nsync: "+Integer.toHexString(frame.sync));
			frame.checksum = convertBytesToInt(bytes[3], bytes[2]);
			frame.signature = convertBytesToInt(bytes[5], bytes[4]);
			frame.xCenter = convertBytesToInt(bytes[7], bytes[6]);
			frame.yCenter = convertBytesToInt(bytes[9], bytes[8]);
			frame.width = convertBytesToInt(bytes[11], bytes[10]);
			frame.height = convertBytesToInt(bytes[13], bytes[12]);

			// sync must equal =0x55aa;
			if (frame.sync != 0xaa55) {
				// System.out.println("Bad Pixy frame sync = " +
				// frame.sync+" "+frame.checksum);
				break;
			}
			// if the checksum is 0 or the checksum is a sync byte, then there
			// are no more frames.
			if (frame.checksum == 0 || frame.checksum == 0xaa55) {
				break;
			}
			frames.add(frame);

			// Read next frame
			if (!i2c.read(0, 14, bytes)) {
				return frames;
			}
		}
		return frames;
	}

	public int convertBytesToInt(int msb, int lsb) {
		// System.out.println(Integer.toHexString(msb)+"
		// "+Integer.toHexString(lsb));
		if (msb < 0)
			msb += 256;
		int value = msb * 256;

		if (lsb < 0) {
			// lsb should be unsigned
			value += 256;
		}
		value += lsb;
		return value;
	}
}