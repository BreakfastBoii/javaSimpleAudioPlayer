package in.audioPlayer;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;


public class AudioPanel extends JPanel implements ActionListener  
{
	JLabel findLabel = new JLabel("Audio file: ");
	JTextField fileInput = new JTextField(30);
	JButton findFileButton = new JButton("Find file...");
	JFileChooser fileChooser = new JFileChooser();
	
	JButton playButton = new JButton("Play");
	JButton pauseButton = new JButton("Pause");
	JButton stopButton = new JButton("Stop");
	JButton restartButton = new JButton("Restart");
	JButton loopButton = new JButton("Loop: false");
	
	JPanel bottomBar = new JPanel(new GridLayout(1, 5));
	
	//JTextArea output = new JTextArea(10, 1);

	long startTime = 0;
	boolean loopClip = false;
	Clip currentClip = null;
	
	String desktopDir;
	
	public AudioPanel() {
		// TODO Auto-generated constructor stub
		
		setLayout(new FlowLayout());
		
		add(findLabel);
		
		desktopDir = System.getProperty("user.home") + File.separator + "Desktop";
		fileChooser.setCurrentDirectory(new File(desktopDir));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FindTypeFilter("wav", "WAV Audio Type"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		findFileButton.addActionListener(this);
		
		add(fileInput);
		add(findFileButton);
		
		playButton.addActionListener(this);
		pauseButton.addActionListener(this);
		stopButton.addActionListener(this);
		restartButton.addActionListener(this);
		loopButton.addActionListener(this);
		
		bottomBar.add(playButton);
		bottomBar.add(pauseButton);
		bottomBar.add(stopButton);
		bottomBar.add(restartButton);
		bottomBar.add(loopButton);
		
		add(bottomBar);
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
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioFile);
				currentClip = AudioSystem.getClip();

				currentClip.open(inputStream);
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
		}
		catch(Exception error)
		{
			JOptionPane.showMessageDialog(null, error.toString());
			
			//output.setText(error.toString());
			error.printStackTrace();
		}
	}

}
