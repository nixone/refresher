package sk.nixone.refresher;

import java.io.File;
import java.util.List;

public class FileExecFlagAwarder {
	private File rootDirectory;
	
	public FileExecFlagAwarder()
	{
		this(new File("."));
	}
	
	public FileExecFlagAwarder(File rootDirectory)
	{
		this.rootDirectory = rootDirectory;
	}
	
	public void award(String filePath)
	{
		File file = new File(rootDirectory, filePath);
		file.setExecutable(true);
	}
	
	public void award(List<String> filePaths)
	{
		for(String filePath : filePaths)
		{
			award(filePath);
		}
	}
}
