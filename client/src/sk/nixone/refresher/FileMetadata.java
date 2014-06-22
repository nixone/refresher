package sk.nixone.refresher;

import java.io.InputStream;

public interface FileMetadata
{
	public int getSize();
	public String getName();
	public String getRelativePath();
	public String getMD5();
	public InputStream getInputStream() throws ServerConnection.Exception;
}
