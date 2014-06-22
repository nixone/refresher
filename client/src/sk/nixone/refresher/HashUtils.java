package sk.nixone.refresher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils 
{
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
		
		try (InputStream is = new FileInputStream(file)) {
		  DigestInputStream dis = new DigestInputStream(is, md);
		  while(dis.read() != -1);
		}
		byte[] digest = md.digest();
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < digest.length; i++) {
	    	sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    
	    return sb.toString();
	}
}
