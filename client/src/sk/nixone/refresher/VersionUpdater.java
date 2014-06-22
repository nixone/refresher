package sk.nixone.refresher;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class VersionUpdater {
	private ServerConnection connection;
	private File versionInfoFile;
	private File rootDirectory;
	
	public VersionUpdater(ServerConnection connection, File versionInfoFile, File rootDirectory)
	{
		this.connection = connection;
		this.versionInfoFile = versionInfoFile;
		this.rootDirectory = rootDirectory;
	}
	
	public boolean hasCurrentVersion()
	{
		return versionInfoFile.exists();
	}
	
	public String getCurrentVersionIdentifier() 
	{
		try {
			try (Scanner scanner = new Scanner(versionInfoFile)) {
				return scanner.nextLine();
			}
		} catch (IOException e) {
			return null;
		}
	}
	
	public boolean hasNewerVersion() throws ServerConnection.Exception
	{
		VersionMetadata latest = connection.getLatestVersion();
		
		return !latest.getIdentifier().equals(getCurrentVersionIdentifier());
	}
	
	public VersionMetadata getNewerVersion() throws ServerConnection.Exception
	{
		return connection.getLatestVersion();
	}
	
	public void updateToLatestVersion(ProgressUpdateListener listener) throws ServerConnection.Exception
	{
		FileUpdater fileUpdater = new FileUpdater(rootDirectory, connection.getLatestVersion(), listener);
		fileUpdater.update();
		
		try {
			if(versionInfoFile.exists())
			{
				versionInfoFile.delete();
			}
			versionInfoFile.createNewFile();
			
			try(PrintWriter writer = new PrintWriter(versionInfoFile)) {
				writer.println(connection.getLatestVersion().getIdentifier());
			}
		} catch(IOException e) {
			throw new ServerConnection.Exception(e);
		}
	}
}
