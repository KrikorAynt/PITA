package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenu {
	
	protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
	
	public MainMenu() {
		JFrame frame = new JFrame();
		
		JButton button = new JButton("Refresh");
		button.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	accInfo();
	        }
	    });
		
		JButton signUp = new JButton("Log Out");
		signUp.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	
	        	logout();
	        	frame.setVisible(false);
	        	
	        	
	        }
	    });
		
		JButton videos = new JButton("Watch my video");
		videos.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	new VideoPlayer();
	        	frame.setVisible(false);
	        }
	    });
		
		JLabel label = new JLabel("Main Menu", SwingConstants.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));
		panel.add(label);
		panel.add(button);
		panel.add(signUp);
		panel.add(videos);
		panel.add(infoLabel);
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PETA");
		//frame.getRootPane().setDefaultButton(button);
		
		accInfo();
		frame.pack();
		frame.setVisible(true);
	}
	public boolean accInfo() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"account"))
			    .header("cookie", driver.cookie)
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
				HttpResponse<String> response;
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				infoLabel.setText(response.body());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return true;
	}
	public boolean logout() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"logout"))
			    .header("cookie", driver.cookie)
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
				HttpResponse<String> response;
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				
				driver.login = new LoginGUI();
				driver.login.infoLabel.setText(response.body());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return true;
	}
}