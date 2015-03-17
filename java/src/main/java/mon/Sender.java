package mon;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * Simple class to send the byte commands to the arduino
 * 
 * @author ericfialkowski
 */
public class Sender
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        if (args.length != 1)
        {
            System.out.println("Usage: <program name> serial_port");
            String[] portNames = SerialPortList.getPortNames();
            System.out.println("Possible port names");
            for (String portName : portNames)
            {
                System.out.printf("\t%s%n", portName);
            }
            System.exit(1);
        }
        SerialPort serialPort = new SerialPort(args[0]);
        try
        {
            serialPort.openPort();
            // we don't need to send data at a high rate.
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);

            boolean stillWaiting = true;
            int waitCount = 5;
            do
            {                
                byte rx[] = serialPort.readBytes();
                if (rx != null)
                {
                    for (byte b : rx)
                    {
                        if (b == 125)
                        {
                            stillWaiting = false;
                        }
                    }
                }
                else
                {
                    if (--waitCount > 0)
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }
                }
            }
            while (stillWaiting && waitCount > 0);
            
            System.out.println("Enter data to send. Blank line to exit");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            while (line != null && line.length() > 0)
            {
                serialPort.writeString(line);
                line = scanner.nextLine();
            }
            
            serialPort.closePort();
        }
        catch (SerialPortException ex)
        {
            System.out.println(ex);
        }
    }
}
