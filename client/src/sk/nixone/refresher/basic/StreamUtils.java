package sk.nixone.refresher.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {
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
}
