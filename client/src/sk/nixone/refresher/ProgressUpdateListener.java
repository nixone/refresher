package sk.nixone.refresher;

public interface ProgressUpdateListener
{
	public void onVersionStarted(VersionMetadata version);
	public void onFileStarted(FileMetadata file);
	public void onFileDownload(FileMetadata file, int sizeDownloaded);
	public void onVersionDownload(VersionMetadata version, int sizeDownloaded);
	public void onFileFinished(FileMetadata file);
	public void onVersionFinished(VersionMetadata version);
}
