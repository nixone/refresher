package sk.nixone.refresher.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;

import sk.nixone.refresher.FileMetadata;
import sk.nixone.refresher.ProgressUpdateListener;
import sk.nixone.refresher.ServerConnection;
import sk.nixone.refresher.VersionMetadata;
import sk.nixone.refresher.VersionUpdater;
import sk.nixone.refresher.basic.BasicServerConnection;

public class Frame extends JFrame {
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
	
	private JButton updateButton;
	private JButton runButton;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	
	private String messageUpdatedToVersion;
	private String messageUpdatingToVersion;
	private String messageInitializingUpdater;
	private String messageDone;
	private String runButtonText;
	private String updateButtonText;
	private String messageThereIsNewerVersion;
	private String messageFetchingUpdateInfo;
	private String messageEverythingUpToDate;
	
	private ProgressUpdateListener progressListener = new ProgressUpdateListener() {
		@Override
		public void onVersionStarted(final VersionMetadata version) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText(String.format(messageUpdatingToVersion, version.toString()));
				}
			});
		}
		
		@Override
		public void onVersionFinished(final VersionMetadata version) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText(String.format(messageUpdatedToVersion, version.toString()));
					progressBar.setString(messageDone);
					runButton.setEnabled(true);
				}
			});
		}
		
		@Override
		public void onVersionDownload(final VersionMetadata version, final int sizeDownloaded) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setMinimum(0);
					try {
						progressBar.setMaximum(version.getTotalSize());
					} catch(Exception e)
					{
						e.printStackTrace();
					}
					
					progressBar.setValue(sizeDownloaded);
				}
			});
		}
		
		@Override
		public void onFileStarted(final FileMetadata file) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setString(file.getRelativePath());
				}
			});
		}
		
		@Override
		public void onFileFinished(FileMetadata file) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setString(" ");
				}
			});
		}
		
		@Override
		public void onFileDownload(FileMetadata file, int sizeDownloaded) {
			// nothing
		}
	};
	
	public Frame(Properties properties)
	{
		super(properties.getProperty("gui.title", "Updater"));
		
		this.messageInitializingUpdater = properties.getProperty("gui.messageInitializingUpdater", "Initializing updater...");
		this.messageDone = properties.getProperty("gui.messageDone", "Done.");
		this.messageUpdatedToVersion = properties.getProperty("gui.messageUpdatedToVersion", "Updated to version %s.");
		this.messageUpdatingToVersion = properties.getProperty("gui.messageUpdatingToVersion", "Updating to version %s...");
		this.runButtonText = properties.getProperty("gui.runButtonText", "Run");
		this.updateButtonText = properties.getProperty("gui.updateButtonText", "Update");
		this.messageFetchingUpdateInfo =  properties.getProperty("gui.messageFetchingUpdateInfo", "Fetching update information...");
		this.messageThereIsNewerVersion = properties.getProperty("gui.messageThereIsNewerVersion", "There is newer version %s.");
		this.messageEverythingUpToDate = properties.getProperty("gui.messageEverythingUpToDate", "Everything is up to date.");
		
		setSize(400, 150);
		
		createComponents();
		createLayout();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void createComponents()
	{		
		updateButton = new JButton(updateButtonText);
		Font font = updateButton.getFont();
		font = font.deriveFont(15f);
		updateButton.setFont(font);
		updateButton.setEnabled(false);
		
		runButton = new JButton(runButtonText);
		runButton.setFont(font);
		
		statusLabel = new JLabel(" ");
		statusLabel.setFont(font);
		
		progressBar = new JProgressBar();
		progressBar.setFont(font);
		progressBar.setString(messageInitializingUpdater);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
	}
	
	private void createLayout()
	{
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.CENTER)
				.addComponent(statusLabel)
				.addComponent(progressBar)
				.addGroup(
					layout.createSequentialGroup()
					.addComponent(runButton)
					.addComponent(updateButton)
				)
		);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(statusLabel)
					.addComponent(progressBar)
					.addGroup(
						layout.createParallelGroup(Alignment.CENTER)
						.addComponent(runButton)
						.addComponent(updateButton)
					)
			);
	}
	
	public void run(final VersionUpdater updater)
	{
		try {
			progressBar.setIndeterminate(true);
			progressBar.setString(messageFetchingUpdateInfo);
			
			if(updater.hasNewerVersion())
			{
				progressBar.setIndeterminate(false);
				progressBar.setString(" ");
				
				updateButton.setEnabled(true);
				
				statusLabel.setText(String.format(messageThereIsNewerVersion, updater.getNewerVersion().toString()));
			}
			else
			{
				progressBar.setString(" ");
				progressBar.setIndeterminate(false);
				statusLabel.setText(messageEverythingUpToDate);
			}
			
			updateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					updateButton.setEnabled(false);
					runButton.setEnabled(false);
					
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								updater.updateToLatestVersion(progressListener);
							} catch(Exception e) 
							{
								statusLabel.setText("Error while updating.");
								e.printStackTrace();
							}
						}
					});
					t.setDaemon(true);
					t.start();
				}
			});
		} catch(Exception e) {
			statusLabel.setText("Error while updating.");
			e.printStackTrace();
		}
	}
}
