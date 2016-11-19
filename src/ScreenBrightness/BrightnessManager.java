package ScreenBrightness;

/***********************************
 * Brightness Modifier
 * 
 * This pro
 * 
 * 
 * 
 ***********************************/



import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


public class BrightnessManager implements NativeKeyListener {
	private static boolean altPressed = false;
    
    public static void setBrightness(int brightness)
            throws IOException {
        //Creates a powerShell command that will set the brightness to the requested value (0-100), after the requested delay (in milliseconds) has passed. 
        String s = "$brightness = "+brightness+";"
                + "$delay = 0;"
                + "$myMonitor = Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods;"
                + "$myMonitor.wmisetbrightness($delay, $brightness)";
        String command = "powershell.exe  " + s;
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        powerShellProcess.getOutputStream().close();

        //Report any error messages
        String line;

        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        line = stderr.readLine();
        if (line != null)
        {
            System.err.println("Standard Error:");
            do
            {
                System.err.println(line);
            } while ((line = stderr.readLine()) != null);

        }
        stderr.close();

    }
    
    public static int getBrightness() throws IOException{
    	int brightness = 0;
    	String s = "Get-Ciminstance -Namespace root/WMI -ClassName WmiMonitorBrightness";
        String command = "powershell.exe  " + s;
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        powerShellProcess.getOutputStream().close();
        
        String line;

        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        line = stderr.readLine();
        if (line != null)
        {
            int count = 0;
            do
            {
            	count++;
            	if(count == 4) {
            		String[] data = line.split(" ");
            		brightness = Integer.parseInt(data[2]);
            	}
            } while ((line = stderr.readLine()) != null);

        }
        stderr.close();
    	return brightness;
    }
    
    public static void main(String[] args) {
    	
    	try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new BrightnessManager());
    	
    	
    	
    }

	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.getID()+"");
		if(arg0.getKeyCode() == NativeKeyEvent.VC_ALT_L || arg0.getKeyCode() == NativeKeyEvent.VC_ALT_R) {
			altPressed = true;
		}
		if(altPressed && arg0.getKeyCode() == NativeKeyEvent.VC_UP) {
			System.out.println("Up");
		try {
			if(BrightnessManager.getBrightness()<100)
    		BrightnessManager.setBrightness(BrightnessManager.getBrightness()+10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if(altPressed && arg0.getKeyCode() == NativeKeyEvent.VC_DOWN) {
			System.out.println("Down");
		try {
			if(BrightnessManager.getBrightness()>0)
				BrightnessManager.setBrightness(BrightnessManager.getBrightness()-10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getKeyCode() == NativeKeyEvent.VC_ALT_L || arg0.getKeyCode() == NativeKeyEvent.VC_ALT_R) {
			altPressed = false;
		}
		
	}

	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}