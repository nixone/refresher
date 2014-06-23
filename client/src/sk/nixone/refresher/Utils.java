package sk.nixone.refresher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utils {
	static public String toNBytesString(int bytes, int round)
	{
		String [] units = new String[]{"b", "kiB", "MiB", "GiB"};
		
		int currentUnit = 0;
		float currentSize = bytes;
		
		while(currentUnit < units.length-1 && currentSize > 1024f)
		{
			++currentUnit;
			currentSize /= 1024f;
		}
		
		return String.format("%."+round+"f %s", currentSize, units[currentUnit]);
	}
	
	static public String read(InputStream stream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();
		
		String line;
		
		while((line = reader.readLine()) != null)
		{
			builder.append(line);
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	static public String md5FromFile(File file) throws IOException
	{
		if(!file.exists())
		{
			return "";
		}
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		byte[] buffer = new byte[8192];
		
		try (InputStream is = new FileInputStream(file)) {
		  DigestInputStream dis = new DigestInputStream(is, md);
		  
		  while(dis.read(buffer) != -1);
		}
		byte[] digest = md.digest();
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < digest.length; i++) {
	    	sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    return sb.toString();
	}
	
	static public List<String> readMultipleProperties(Properties all, String prefix)
	{
		ArrayList<String> result = new ArrayList<>();
		
		for(int i=1; ; ++i) {
			String propertyName = prefix+"."+String.valueOf(i);
			String propertyValue = all.getProperty(propertyName);
			
			if(propertyValue == null)
			{
				break;
			}
			
			result.add(propertyValue);
		}
		
		return result;
	}
}
