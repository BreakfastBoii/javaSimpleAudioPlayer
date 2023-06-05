package in.audioPlayer;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AudioPanel extends JPanel implements ActionListener  
{
	JTabbedPane tabs = new JTabbedPane();
	
	//audio player panel
	JPanel audioPlayerPanel = new JPanel(new FlowLayout());
	
	JLabel titleLabel = new JLabel("Audio player/editor");
	JLabel findLabel = new JLabel("Audio file: ");
	JTextField fileInput = new JTextField(30);
	JButton findFileButton = new JButton("Find file...");
	JFileChooser fileChooser = new JFileChooser();

	JPanel controlBar = new JPanel(new GridLayout(1, 5));
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton stopButton = new JButton("Stop");
	JButton restartButton = new JButton("Restart");
	JButton loopButton = new JButton("Loop: false");
	
	
	JPanel editBar = new JPanel(new GridLayout(1, 5));
	
	JLabel bitRateLabel = new JLabel("Sample rate %: ");
	JSlider bitRateSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
	
	JButton exportButton = new JButton("Export audio...");
	
	//mic recorder
	JPanel micRecordPanel = new JPanel();
	JButton recordButton = new JButton("Record");
	
	JLabel versionLabel = new JLabel("0.0");
	
	//JTextArea output = new JTextArea(10, 1);

	long startTime = 0;
	boolean loopClip = false;
	Clip currentClip = null;
	AudioInputStream convertedStream = null;
	
	boolean recordingAudio = false;
	
	String desktopDir;
	
	public AudioPanel() {
		// TODO Auto-generated constructor stub
		
		//STOP FORGETTING TO ADD ACTION LISTENERS AAAAAAAAA
		setLayout(new BorderLayout());
		
		tabs.add("Audio player", audioPlayerPanel);
		tabs.add("Mic recorder", micRecordPanel);
		
		add(tabs, BorderLayout.NORTH);
		
		//add(titleLabel);
		
		desktopDir = System.getProperty("user.home") + File.separator + "Desktop";
		fileChooser.setCurrentDirectory(new File(desktopDir));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FindTypeFilter("wav", "WAV Audio Type"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		//Audio player
		audioPlayerPanel.add(findLabel);
		
		findFileButton.addActionListener(this);
		
		audioPlayerPanel.add(fileInput);
		audioPlayerPanel.add(findFileButton);
		
		playButton.addActionListener(this);
		pauseButton.addActionListener(this);
		stopButton.addActionListener(this);
		restartButton.addActionListener(this);
		loopButton.addActionListener(this);
		
		controlBar.add(playButton);
		controlBar.add(pauseButton);
		controlBar.add(stopButton);
		controlBar.add(restartButton);
		controlBar.add(loopButton);
		
		audioPlayerPanel.add(controlBar);

		bitRateSlider.setMajorTickSpacing(50);
		bitRateSlider.setMinorTickSpacing(5);
		//bitRateSlider.setPreferredSize(new Dimension(500, 100));
		bitRateSlider.setPaintTrack(true);
		bitRateSlider.setPaintTicks(true);
		bitRateSlider.setPaintLabels(true);
		
		
		editBar.add(bitRateLabel);
		editBar.add(bitRateSlider);
		
		audioPlayerPanel.add(editBar);
		
		versionLabel.setLocation(0, 500);
		exportButton.addActionListener(this);
		
		//audioPlayerPanel.add(exportButton);
		
		//Mic recorder
		recordButton.addActionListener(this);
		micRecordPanel.add(recordButton);
		micRecordPanel.add(exportButton);
		
		add(versionLabel, BorderLayout.SOUTH);
		//add(output);
		
	}
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(startTime);
		try
		{
			Object source = e.getSource();
			if(source == findFileButton)
			{ 
				if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileInput.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
			else if(source == playButton)
			{
				//get audio clip
				//try
				//{
				File audioFile = new File(fileInput.getText()).getAbsoluteFile();
				
				//if(GetFileExtension(audioFile.getName()).get().toLowerCase() != "wav")
				//{
					//currentClip = ConvertAudioToWAV(audioFile);
				//}
				//else
				//{
				
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
				
				//FLOAT DIVISION 
				float sampleRate = (((float)bitRateSlider.getValue() / 100f) * inputStream.getFormat().getSampleRate()); //precent to sample rate
				//inputStream.getFormat().getEncoding() 
				AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, inputStream.getFormat().getChannels(), inputStream.getFormat().getFrameSize(), sampleRate, inputStream.getFormat().isBigEndian());
				
				convertedStream = AudioSystem.getAudioInputStream(targetFormat, inputStream);
				currentClip = AudioSystem.getClip();
				currentClip.open(convertedStream); //uses the input stream
				//}
				currentClip.setMicrosecondPosition(startTime);
					
				currentClip.start();
				if(loopClip) currentClip.loop(Clip.LOOP_CONTINUOUSLY);
					
				startTime = 0;
				//}
				//catch(Exception error)
				//{
					//output.setText(error.toString());
				//}
			}	 
			else if(source == pauseButton)
			{
				startTime = currentClip.getMicrosecondPosition();
				currentClip.stop();
			}
			else if(source == stopButton)
			{
				startTime = 0;
				currentClip.stop();
			}
			else if(source == restartButton)
			{
				currentClip.setMicrosecondPosition(0);
			}
			else if(source == loopButton)
			{
				loopClip = !loopClip;
				loopButton.setText("Loop: " + loopClip);
				currentClip.loop(loopClip ? Clip.LOOP_CONTINUOUSLY : 0);
			}
			else if(source == exportButton)
			{
				//fileChooser.setDialogTitle("");
				
				//DOESNT WORK FOR NOW
				if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					File saveToFile = fileChooser.getSelectedFile();
					//saveToFile.renameTo(new File(fileChooser.getSelectedFile().getPath() + ".wav"));
					
					int totalFramesRead = 0;
					int bytesPerFrame = convertedStream.getFormat().getFrameSize();
					int numBytes = 1024 * bytesPerFrame;
					byte[] audioBytes = new byte[numBytes];
					
					int numBytesRead = 0;
					int numFramesRead = 0;
					while((numBytesRead = convertedStream.read(audioBytes)) != -1)
					{
						numFramesRead = numBytesRead / bytesPerFrame;
						totalFramesRead += numFramesRead;
						
					}
					
					//all bytes are 0
					for(int i = 0; i < audioBytes.length; i++)
					{
						System.out.println(audioBytes[i]);
					}
					
					FileOutputStream outputStream = new FileOutputStream(saveToFile);
					outputStream.write(audioBytes);
					
					//AudioSystem.write(convertedStream, AudioFileFormat.Type.WAVE, saveToFile);
					JOptionPane.showMessageDialog(null, "Successfully saved audio to " + saveToFile.getName() + ".");
				}
			}
			else if(source == recordButton)
			{
				recordingAudio = !recordingAudio;
				recordButton.setText(recordingAudio ? "Stop" : "Record");
				
				//aka wav recording
				AudioFormat recordFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false); //AudioFormat recordFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, recordFormat); //gets the computer mic stuff 
				
				final TargetDataLine targetLine = (TargetDataLine)AudioSystem.getTargetDataLine(recordFormat);
				targetLine.open();

				//why do I feel like this is unsafe/not the best way to implement
				Thread thread = new Thread()
				{
					@Override public void run()
					{
						try 
						{
						AudioInputStream audioStream = new AudioInputStream(targetLine);
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH-mm-ss");
						
						File audioFile = new File(desktopDir + File.pathSeparatorChar + "Recording " + dtf.format(LocalDateTime.now()) + ".wav");
							AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);
						} 
						catch (IOException e) 
						{
							JOptionPane.showMessageDialog(null, e.toString());
							e.printStackTrace();
						}
					}
				};
				
				if(recordingAudio)
				{
					System.out.println("Started recording");
					targetLine.start();
					thread.start();
				}
				else
				{
					targetLine.stop();
					targetLine.close();
				}
			}
		}
		catch(Exception error)
		{
			JOptionPane.showMessageDialog(null, error.toString());
			
			//output.setText(error.toString());
			error.printStackTrace();
		}
	}
	
	Clip ConvertAudioToWAV(File _file)
	{
		try
		{
		AudioInputStream in = AudioSystem.getAudioInputStream(_file);
		AudioInputStream convertIn = null;
		AudioFormat baseFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), baseFormat.getSampleSizeInBits() ,baseFormat.getChannels() * 2, baseFormat.getFrameSize(), baseFormat.getFrameRate(), false);
		
		convertIn = AudioSystem.getAudioInputStream(decodedFormat, in);
		
		return AudioSystem.getClip();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.toString());
			
			//output.setText(error.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	Optional<String> GetFileExtension(String _fileName)
	{
		return Optional.ofNullable(_fileName).filter(f -> f.contains(".")).map(f -> f.substring(_fileName.lastIndexOf('.') + 1));
	}
}
