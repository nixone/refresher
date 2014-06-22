package sk.nixone.refresher.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import sk.nixone.refresher.FileMetadata;
import sk.nixone.refresher.ServerConnection;

public class BasicFileMetadata implements FileMetadata
{
	private int size;
	private String name;
	private String relativePath;
	private String absolutePath;
	private String md5;
	
	protected BasicFileMetadata(JSONObject meta)
	{
		size = meta.getInt("size");
		name = meta.getString("name");
		relativePath = meta.getString("relativePath");
		absolutePath = meta.getString("absolutePath");
		md5 = meta.getString("md5");
	}
	
	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getRelativePath()
	{
		return relativePath;
	}

	@Override
	public String getMD5()
	{
		return md5;
	}

	@Override
	public InputStream getInputStream() throws ServerConnection.Exception
	{
		try
		{
			URLConnection connection = (new URL(absolutePath)).openConnection();
			return connection.getInputStream();
		}
		catch(IOException e)
		{
			throw new ServerConnection.Exception(e);
		}
	}
}
