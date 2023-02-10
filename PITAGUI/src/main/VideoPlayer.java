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
		
		JButton signUp = new JButton("Sign Up");
		signUp.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	
	        }
	    });
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	            
	        }
	    });
		
		JLabel label = new JLabel("SIGN UP HERE", SwingConstants.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));
		panel.add(label);
		panel.add(signUp);
		panel.add(login);
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PETA Sign Up");
		frame.getRootPane().setDefaultButton(signUp);
		
		
		frame.pack();
		frame.setVisible(true);
	}

}
