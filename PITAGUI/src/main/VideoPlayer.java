package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VideoPlayer {

	public VideoPlayer() {
		JFrame frame = new JFrame();
		
		JButton upload = new JButton("Upload");
		upload.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	Video vid = new Video("fortnite.mp4",".\\videos");
	        	vid.videoSend();
	        }
	    });
		
		JButton download = new JButton("Download");
		download.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	Video vid = new Video("fortnite.mp4",".\\videos");
	        	vid.videoRec();
	        }
	    });
		
		JLabel label = new JLabel("VIDEO", SwingConstants.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));
		panel.add(label);
		panel.add(upload);
		panel.add(download);
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PETA Video Player");
		
		
		frame.pack();
		frame.setVisible(true);
	}

}
