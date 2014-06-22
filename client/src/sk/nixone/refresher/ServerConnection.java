package sk.nixone.refresher;

import java.util.Collection;

public interface ServerConnection
{
	static public class Exception extends java.lang.Exception
	{
		public Exception(Throwable cause)
		{
			super(cause);
		}
	}
	
	public Collection<VersionMetadata> getVersions() throws ServerConnection.Exception;
	
	public VersionMetadata getLatestVersion() throws ServerConnection.Exception;
}
