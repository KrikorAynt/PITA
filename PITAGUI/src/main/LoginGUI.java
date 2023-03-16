package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import java.io.IOException;  
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URI;


public class LoginGUI{
	private JTextField userText;
	private JPasswordField passText;
	protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
	
	public LoginGUI() {
		JFrame frame = new JFrame();
		
		JButton button = new JButton("Login");
		button.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	String user = userText.getText();
	    		@SuppressWarnings("deprecation")
				String password = passText.getText();
	    		
	    		if(login(user, password)) {
	    			driver.mainMenu = new MainMenu();
	    			user = "";
	    			password = "";
	    			userText.setText("");
	    			passText.setText("");
	    			frame.setVisible(false);
	    		}
	    		else {
	    			user = "";
	    			password = "";
	    			userText.setText("");
	    			passText.setText("");
	    		}	
	        }
	    });
		
		JButton signUp = new JButton("Sign Up");
		signUp.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	            new SignUpGUI();
	            frame.setVisible(false);
	        }
	    });
		
		JLabel label = new JLabel("LOGIN HERE", SwingConstants.CENTER);
		userText = new JTextField(20);
		userText.setBounds(100, 20, 165, 25);
		passText = new JPasswordField();
		passText.setBounds(100, 20, 165, 25);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));
		panel.add(label);
		panel.add(userText);
		panel.add(passText);
		panel.add(button);
		panel.add(signUp);
		panel.add(infoLabel);
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PETA Login");
		frame.getRootPane().setDefaultButton(button);
		
		
		frame.pack();
		frame.setVisible(true);
	}
	public boolean login(String username, String password) {
		  
		try {
				HttpRequest request = HttpRequest.newBuilder()
				    .uri(URI.create(driver.url+"login"))
				    .header("cookie", "session=s%253AHJEksfpD4EWcyB4TtdnSOBP3LKcxBY25.db2YDmriKgk8jSy87tOYpty0l%252Farw%252FEd3uRIoUjJaAY")
				    .header("Content-Type", "application/json")
				    .method("POST", HttpRequest.BodyPublishers.ofString("{\n\"user\": \""+username+"\",\n\"pass\": \""+password+"\"\n}"))
				    .build();
				HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				infoLabel.setText(response.body());
				
				if (response.body().equals("User Doesn't Exist!"))
					return false;
				else if(response.body().equals("Wrong Password!"))
					return false;
				else if(response.body().equals("Logged In!")) {

					List<String> cookieHeaders = response.headers().allValues("Set-Cookie");
					driver.cookie = cookieHeaders.get(0);
					driver.cookie = driver.cookie.split(";")[0];
					
					return true;
				}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;  
		
	}
	

}
