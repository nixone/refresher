package sk.nixone.refresher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUpdater {
	private File rootDirectory;
	private VersionMetadata version;
	private ProgressUpdateListener listener;
	
	private int versionBytes;
	
	public FileUpdater(File rootDirectory, VersionMetadata version, ProgressUpdateListener listener)
	{
		this.rootDirectory = rootDirectory;
		this.version = version;
		this.listener = listener;
	}
	
	public void update() throws ServerConnection.Exception
	{
		versionBytes = 0;
		
		listener.onVersionStarted(version);
		
		for(FileMetadata file : version.getFiles())
		{
			update(file);
		}
		
		listener.onVersionFinished(version);
	}
	
	private void update(FileMetadata file) throws ServerConnection.Exception
	{
		int fileBytes = 0;
		
		listener.onFileStarted(file);
		
		try {		
			File target = new File(rootDirectory, file.getRelativePath());
			String md5 = Utils.md5FromFile(target);
			
			if(!md5.equals(file.getMD5())) {
				File temp = File.createTempFile("refresherdownload", ".download");
				OutputStream output = new FileOutputStream(temp);
				InputStream input = file.getInputStream();
				
				byte[] data = new byte[1024];
				int byteCount;
				
				while((byteCount = input.read(data)) != -1)
				{
					versionBytes += byteCount;
					fileBytes += byteCount;
					
					output.write(data, 0, byteCount);

					listener.onFileDownload(file, fileBytes);
					listener.onVersionDownload(version, versionBytes);
				}
				
				output.close();
				input.close();
				
				target.mkdirs();
				
				Files.copy(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				versionBytes += file.getSize();
				listener.onFileDownload(file, file.getSize());
				listener.onVersionDownload(version, versionBytes);
			}
		} catch (IOException e) {
			throw new ServerConnection.Exception(e);
		}
		
		listener.onFileFinished(file);
	}
}
