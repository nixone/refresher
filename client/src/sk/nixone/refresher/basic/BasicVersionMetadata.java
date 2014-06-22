package sk.nixone.refresher.basic;

import java.util.Collection;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.nixone.refresher.FileMetadata;
import sk.nixone.refresher.ServerConnection;
import sk.nixone.refresher.VersionMetadata;

public class BasicVersionMetadata implements VersionMetadata {

	private BasicServerConnection server;
	private String identifier;
	private boolean fresh = false;
	private int size;
	private LinkedList<FileMetadata> files = new LinkedList<>();
	
	protected BasicVersionMetadata(BasicServerConnection server, String identifier)
	{
		this.server = server;
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() 
	{
		return identifier;
	}
	
	@Override
	public int getTotalSize() throws ServerConnection.Exception
	{
		refreshIfNeeded();
		return size;
	}

	@Override
	public Collection<FileMetadata> getFiles() throws ServerConnection.Exception
	{
		refreshIfNeeded();
		return files;
	}
	
	private void refreshIfNeeded() throws ServerConnection.Exception
	{
		if(fresh)
		{
			return;
		}
		
		fresh = false;
		files.clear();
		
		JSONObject response = server.queryOperationAsJSON("?operation=version&version="+identifier);
		size = response.getInt("size");
		
		JSONArray fileArray = response.getJSONArray("files");
		
		for(int i=0; i<fileArray.length(); i++)
		{
			files.add(new BasicFileMetadata(fileArray.getJSONObject(i)));
		}
		
		fresh = true;
	}
	
	@Override
	public String toString()
	{
		return identifier;
	}
}
