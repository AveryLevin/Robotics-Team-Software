/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frc.robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JReceiver
{
    String m_host = JTargetInfo.ipAddressRaspberryPi;	
    DatagramSocket  m_bbbTextSocket = null;
    boolean m_initOK = false;

    void init()
    {
    	boolean isOK = true;
    	if(m_bbbTextSocket == null)
    	{
	        try
	        {
        		m_bbbTextSocket = new DatagramSocket(JTargetInfo.textPortRoboRioReceive);
        	}
	        catch (Exception e)
	        {
	            System.err.println("Couldn't get I/O for the connection to: " + m_host);
	            isOK = false;
	        }
    	}
        m_initOK = isOK;
    }
    
    String getOneLineFromSocket()
    {
    	if(!m_initOK)
    	{
    		return null;
    	}
        String textInput;
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
	        m_bbbTextSocket.receive(packet);       
	        textInput = new String(packet.getData(), 0, packet.getLength());
        } catch (IOException ex) {
            Logger.getLogger(JReceiver.class.getName()).log(Level.SEVERE, null, ex);
            textInput = null;
        }
       return textInput;
    }
}
