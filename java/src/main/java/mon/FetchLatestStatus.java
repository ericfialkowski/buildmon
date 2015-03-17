package mon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FetchLatestStatus
{
    private static final Logger LOG = Logger.getLogger(FetchLatestStatus.class);
    private final URL url;

    public FetchLatestStatus(String host, String project) throws MalformedURLException
    {
        this.url = new URL(String.format("http://%s/job/%s/api/xml?depth=1", host, project));
    }

    public BuildStates getLatest()
    {
        BuildStates status = BuildStates.UNKNOWN;
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url.openStream());

            NodeList nodes = doc.getElementsByTagName("build");
            if (nodes.getLength() > 0)
            {
                Element recentBuild = (Element) nodes.item(0);
                NodeList resultsNodes = recentBuild.getElementsByTagName("result");
                Element results = (Element) resultsNodes.item(0);
                String statusString = getCharacterDataFromElement(results).toUpperCase();

                NodeList buildingNodes = recentBuild.getElementsByTagName("building");
                Element buildingResults = (Element) buildingNodes.item(0);
                String buildingStatus = getCharacterDataFromElement(buildingResults);
                if (Boolean.parseBoolean(buildingStatus))
                {
                    statusString = "BUILDING";
                }
                status = BuildStates.fromStatusMessage(statusString);
            }

        }
        catch (IOException | ParserConfigurationException | SAXException ex)
        {
            LOG.warn("Error getting current status", ex);
        }
        return status;
    }

    private String getCharacterDataFromElement(Element e)
    {
        String rtn = "";
        if (e != null)
        {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData)
            {
                CharacterData cd = (CharacterData) child;
                rtn = cd.getData();
            }
        }
        LOG.debug("Parsed character data: " + rtn);
        return rtn;
    }

    public static void main(String[] args) throws MalformedURLException
    {
        FetchLatestStatus fetcher = new FetchLatestStatus("devbuild", "chat-stack");
        BuildStates latest = fetcher.getLatest();
        System.out.println(latest.getState());
    }
}
