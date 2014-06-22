package sk.nixone.refresher;

import java.util.Collection;

public interface VersionMetadata
{
	public String getIdentifier();
	public int getTotalSize() throws ServerConnection.Exception;
	public Collection<FileMetadata> getFiles() throws ServerConnection.Exception;
}
