package org.usfirst.frc.team4276.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.InterruptHandlerFunction;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class EncoderWithNotify extends Encoder {
	
	EncoderWithNotify(int enc_A, int enc_B) {
		super(enc_A, enc_B, false, Encoder.EncodingType.k1X);

		SmartDashboard.putString("debug", "enc_A, enc_B " + enc_A + "    " + enc_B);

		// Register an interrupt handler
		m_bSource.requestInterrupts(new InterruptHandlerFunction<Object>() {

			@Override
			public void interruptFired(int interruptAssertedMask, Object param) {
				Robot.turntable1.encoderUpdate();
			}
		});

		// Listen for a rising edge
		m_bSource.setUpSourceEdge(true, false);
		// Enable digital interrupt pin
		m_bSource.enableInterrupts();
	}
}
