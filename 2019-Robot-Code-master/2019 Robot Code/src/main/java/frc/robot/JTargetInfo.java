/*******************************************************************************************/
/* The MIT License (MIT)                                                                   */
/*                                                                                         */
/* Copyright (c) 2014 - Marina High School FIRST Robotics Team 4276 (Huntington Beach, CA) */
/*                                                                                         */
/* Permission is hereby granted, free of charge, to any person obtaining a copy            */
/* of this software and associated documentation files (the "Software"), to deal           */
/* in the Software without restriction, including without limitation the rights            */
/* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell               */
/* copies of the Software, and to permit persons to whom the Software is                   */
/* furnished to do so, subject to the following conditions:                                */
/*                                                                                         */
/* The above copyright notice and this permission notice shall be included in              */
/* all copies or substantial portions of the Software.                                     */
/*                                                                                         */
/* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR              */
/* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                */
/* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE             */
/* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                  */
/* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,           */
/* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN               */
/* THE SOFTWARE.                                                                           */
/*******************************************************************************************/

/*******************************************************************************************/
/* We are a high school robotics team and always in need of financial support.             */
/* If you use this software for commercial purposes please return the favor and donate     */
/* (tax free) to "Marina High School Educational Foundation"  (Huntington Beach, CA)       */
/*******************************************************************************************/

package frc.robot;

import java.util.Arrays;
import java.util.List;

public class JTargetInfo {
	public static final String ipAddressRaspberryPi = "10.42.76.8";
	public static final String ipAddressRoboRio = "10.42.76.2";
    
    // user ports must be between 1180 and 1190
	public static final int textPortRoboRioReceive= 1180;  // Where the UDP packet will be sent
	public static final int streamSourcePortOnRaspberryPi = 1185;  
	public static final int streamAnnotatedSourcePortOnRaspberryPi = 1186;  

	public int isCargoBayDetected;  // 0 == false
	public double visionPixelX;
	
	public int nSequence;
	public long timeSinceLastCameraFrameMilliseconds;
	public long timeLatencyThisCameraFrameMilliseconds;

	int commaPos;
	String word;
	int num;
	double fnum;
	String s;

	public JTargetInfo() {
		init();
	}

	public void init() {
		isCargoBayDetected = 0;
		visionPixelX = 0.0;
		timeSinceLastCameraFrameMilliseconds = 0;
		timeLatencyThisCameraFrameMilliseconds = 0;

		commaPos = 0;
		word = "";
		num = 0;
		s = "";
	}

	public void initTargetInfoFromText(String str) {
		List<String> items = Arrays.asList(str.split("\\s*,\\s*"));
		if(items.size() < 4)
		{
			if(str.length() == 0)
			{
				System.out.printf("vision text message - No data received\n");	
			}
			else
			{
				System.out.printf("vision text message parse error:   %s  \n", str);
			}
		}
		int idx = 0;
		isCargoBayDetected = Integer.parseInt(items.get(idx++));
		visionPixelX = Double.parseDouble(items.get(idx++));
		nSequence = Integer.parseInt(items.get(idx++));
		timeSinceLastCameraFrameMilliseconds = Integer.parseInt(items.get(idx++));
		timeLatencyThisCameraFrameMilliseconds = Integer.parseInt(items.get(idx++));
	}

	public String numberToText() {
		s += Integer.toString(isCargoBayDetected) + ",";
		s += Double.toString(visionPixelX) + ",";
		s += Integer.toString(nSequence) + ",";
		s += Long.toString(timeSinceLastCameraFrameMilliseconds) + ",";
		s += Long.toString(timeLatencyThisCameraFrameMilliseconds) + ",";
		return s;
	}

	public String displayText() {
		String str = "Seq: " + nSequence + "  Time Since Last Frame: " + timeSinceLastCameraFrameMilliseconds + "ms.\n";
		str += "Latency This Frame: " + timeSinceLastCameraFrameMilliseconds + "ms.\n";
		if (isCargoBayDetected != 0) {
			str += "X Pixel: " + visionPixelX + "\n";
		} else {
			str += "No Cargo Bay Detected\n";
		}
		return str;
	}

	public void initFormattedTextFromTargetInfo() {
		// targetInfoText = Text;
		// TODO: Assign values to target info data members by parsing the text string
		// from initFormattedTextFromTargetInfo()

		// Format text for transmission to the cRio
		initFormattedTextFromTargetInfo();
	}

}
