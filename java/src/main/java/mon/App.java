package mon;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App
{

    private static final Logger LOG = LogManager.getLogger(App.class);

    public static void main(String[] args) throws SerialPortException, MalformedURLException
    {
        int delay = 30;

        if (args.length < 3)
        {
            System.out.println("Usage: <program name> build_host project port <delay>");
            String[] portNames = SerialPortList.getPortNames();
            System.out.println("Possible port names");
            for (String portName : portNames)
            {
                System.out.printf("\t%s%n", portName);
            }
            System.exit(1);
        }

        if (args.length == 4)
        {
            delay = Integer.parseInt(args[3]);
        }

        LOG.info("Build Host: " + args[0]);
        LOG.info("Project: " + args[1]);
        LOG.info("Serial Port: " + args[2]);
        LOG.info("Delay: " + delay);

        FetchLatestStatus fetcher = new FetchLatestStatus(args[0], args[1]);
        final SerialPort serialPort = new SerialPort(args[2]);
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_9600,
                             SerialPort.DATABITS_8,
                             SerialPort.STOPBITS_1,
                             SerialPort.PARITY_NONE);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			if (serialPort.isOpened())
			{
				try
				{
					serialPort.closePort();
				}
				catch (SerialPortException ex)
				{
					System.out.println(ex);
				}
			}
		}));

        BuildStates previousStatus = BuildStates.BLANK;
        boolean running = true;
		boolean lastWasBroken = false;
        while (running)
        {
            try
            {
                TimeUnit.SECONDS.sleep(delay);
                BuildStates status = fetcher.getLatest();
                LOG.info(String.format("Latest Status = %s", status));
                if (status != previousStatus)
                {
                    byte data = status.getCommand();
                    LOG.debug(String.format("Sending %s to %s", status.getState(), serialPort.getPortName()));
                    serialPort.writeByte(data);
                    if (status == BuildStates.SUCCESS && lastWasBroken)
                    {   
                        TimeUnit.MILLISECONDS.sleep(500);
                        serialPort.writeByte((byte)'7');
						lastWasBroken = false;
                    }
                    else if (status == BuildStates.FAILURE && !lastWasBroken)
                    {
                        TimeUnit.MILLISECONDS.sleep(500);
                        serialPort.writeByte((byte)'6');
						lastWasBroken = true;
                    }
                    
                    previousStatus = status;
                }
                else
                {
                    LOG.debug("No status change");
                }
            }
            catch (SerialPortException ex)
            {
                LOG.warn("Exception sending serial data", ex);
            }
            catch (InterruptedException ex)
            {
                LOG.error("Sleep interrupted", ex);
                running = false;
            }
        }
    }
}
