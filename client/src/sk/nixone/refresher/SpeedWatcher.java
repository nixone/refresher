package sk.nixone.refresher;

import java.util.LinkedList;

public class SpeedWatcher
{
	class HistoryRecord
	{
		final private long timestamp;
		final private int bytes;
		
		private HistoryRecord(int bytes)
		{
			this.timestamp = System.currentTimeMillis();
			this.bytes = bytes;
		}
	}
	
	private int historyDuration = 1000;
	private LinkedList<HistoryRecord> history = new LinkedList<SpeedWatcher.HistoryRecord>();
	private float currentSpeed = 0;
	
	public void onDownload(int bytes)
	{
		HistoryRecord newRecord = new HistoryRecord(bytes);
		
		history.addLast(newRecord);
		
		while((newRecord.timestamp - history.getFirst().timestamp) > historyDuration)
		{
			history.removeFirst();
		}
		
		long totalBytes = history.getLast().bytes - history.getFirst().bytes;		
		long historyDuration = history.getLast().timestamp - history.getFirst().timestamp;
		
		if(historyDuration > 0)
		{
			currentSpeed = (float)totalBytes/(float)(historyDuration/1000f);
		}
	}
	
	public float getCurrentSpeed()
	{
		return currentSpeed;
	}
}