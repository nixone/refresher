package sk.nixone.refresher.basic;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.nixone.refresher.ServerConnection;
import sk.nixone.refresher.VersionMetadata;

public class BasicServerConnection implements ServerConnection
{
	private boolean fresh = false;
	
	private String queryUri;
	
	private HashMap<String, VersionMetadata> versions = new HashMap<>();
	
	private VersionMetadata latestVersion = null;
	
	public BasicServerConnection(String queryUri)
	{
		this.queryUri = queryUri;
	}
	
	@Override
	public Collection<VersionMetadata> getVersions() throws ServerConnection.Exception
	{
		refreshIfNeeded();
		return versions.values();
	}

	@Override
	public VersionMetadata getLatestVersion() throws ServerConnection.Exception {
		refreshIfNeeded();
		return latestVersion;
	}
	
	protected JSONObject queryOperationAsJSON(String operationString) throws ServerConnection.Exception
	{
		try {
			URLConnection connection = (new URL(queryUri+operationString)).openConnection();
			
			return new JSONObject(StreamUtils.read(connection.getInputStream()));
		} catch (IOException e) {
			throw new ServerConnection.Exception(e);
		}
	}
	
	private void refreshIfNeeded() throws ServerConnection.Exception
	{
		if(fresh) {
			return;
		}
		
		versions.clear();
		latestVersion = null;
		
		JSONObject response = queryOperationAsJSON("");
		JSONArray all = response.getJSONArray("all");
		
		for(int i=0; i<all.length(); i++)
		{
			VersionMetadata version = new BasicVersionMetadata(this, all.getString(i));
			versions.put(version.getIdentifier(), version);
		}
		
		latestVersion = versions.get(response.getString("latest"));
		
		fresh = true;
	}
}
