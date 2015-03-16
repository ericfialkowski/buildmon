package mon;

import java.io.IOException;
import java.util.Scanner;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

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
            serialPort.openPort();//Open serial port
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                                 SerialPort.DATABITS_8,
                                 SerialPort.STOPBITS_1,
                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);			            
            //Thread.sleep(2000);
            boolean stillWaiting = true;
			int waitCount = 5;
            do
            {				
                byte rx[] = serialPort.readBytes();
                if ( rx != null)
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
					if ( --waitCount > 0)
					{
						Thread.sleep(1000);
					}
				}
            }
            while (stillWaiting && waitCount > 0);

            
            System.out.println("Enter data to send. Blank line to exit");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            while ( line != null && line.length() > 0)
            {                                        
//                byte data = Byte.parseByte(line);
//                System.out.printf("Sending %d to %s%n", data, serialPort.getPortName());                                                    
                serialPort.writeString(line);
                line = scanner.nextLine();
            }
            
            
            serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex)
        {
            System.out.println(ex);
        }
    }
}
