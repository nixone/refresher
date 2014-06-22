package sk.nixone.refresher.gui;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import sk.nixone.refresher.ServerConnection;
import sk.nixone.refresher.VersionUpdater;
import sk.nixone.refresher.basic.BasicServerConnection;

public class Application {
	static public void main(String [] arguments) throws Exception
	{
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File("refresher.ini")));
		
		ServerConnection connection = new BasicServerConnection(properties.getProperty("server.uri"));
		VersionUpdater updater = new VersionUpdater(
				connection, 
				new File(properties.getProperty("local.versionFile", ".version")), 
				new File(properties.getProperty("local.applicationDirectory", "application"))
		);

		Frame frame = new Frame(properties);
		frame.run(updater);
	}
	
	static public String getOSName()
	{
		return System.getProperty("os.name").split(" ")[0];
	}
}
