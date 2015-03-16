package mon;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FetchLatestStatus 
{
	private static final Logger LOG = Logger.getLogger(FetchLatestStatus.class);
	private final String url;

	public FetchLatestStatus(String host, String project)
	{
		this.url = String.format("http://%s/job/%s/api/xml?depth=1", host, project);
	}
	
	public BuildStates getLatest()
	{
		BuildStates status = BuildStates.UNKNOWN;
		Client client = Client.create();
		WebResource webResource = client.resource(url);

		ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);

		if (response == null || response.getStatus() != 200)
		{
			return BuildStates.DEMO;
		}

		Document doc = response.getEntity(Document.class);

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
            if ( Boolean.parseBoolean(buildingStatus))
            {
                statusString = "BUILDING";
            }
            status = BuildStates.fromStatusMessage(statusString);
		}
                		
		client.destroy();
		return status;
	}

	private String getCharacterDataFromElement(Element e)
	{
		String rtn = "";
		if ( e != null)
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
	
	public static void main(String[] args) 
	{
		FetchLatestStatus fetcher = new FetchLatestStatus("devbuild", "chat-stack");
		BuildStates latest = fetcher.getLatest();
		System.out.println(latest.getState());
	}
}
