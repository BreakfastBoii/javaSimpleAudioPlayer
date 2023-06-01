package in.audioPlayer;

import javax.swing.*;

public class SimpleAudioPlayer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JFrame frame = new JFrame();
		frame.setSize(512, 512);
		frame.add(new AudioPanel());
		frame.setVisible(true);
	}

}
