package mon;

/**
 * The various build states that can be sent to the arduino. Not all are actual 
 * build status returns (such as DEMO) but states that the arduino understands
 * 
 * @author ericfialkowski
 */
public enum BuildStates
{

    BLANK("Blank", (byte) '0'),
    BUILDING("Building", (byte) '1'),
    SUCCESS("Success", (byte) '2'),
    FAILURE("Failure", (byte) '3'),
    WARNING("Warning", (byte) '4'),
    UNKNOWN("Unknown", (byte) '5'),   
    DEMO("Demo", (byte) '8');

    static BuildStates fromStatusMessage(String statusString)
    {
        BuildStates rtn;
        switch (statusString)
        {
            case "BLANK":
                rtn = BLANK;
                break;
            case "BUILDING":
                rtn = BUILDING;
                break;
            case "SUCCESS":
                rtn = SUCCESS;
                break;
            case "FAILURE":
                rtn = FAILURE;
                break;
            case "WARNING":
			case "UNSTABLE":
                rtn = WARNING;
                break;
            case "DEMO":
                rtn = DEMO;
                break;
            default:
                rtn = UNKNOWN;
                break;
        }

        return rtn;
    }

    private final String state;
    private final byte command;

    private BuildStates(String state, byte command)
    {
        this.state = state;
        this.command = command;
    }

    public String getState()
    {
        return state;
    }

    public byte getCommand()
    {
        return command;
    }
    
    
}
