package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URI;


public class LoginGUI{
	private JTextField userText;
	private JPasswordField passText;
	protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
	private Image backgroundImage;
	
	public LoginGUI() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PITA Login");
		frame.setPreferredSize(new Dimension(600, 400));
		
		try {
			backgroundImage = ImageIO.read(new File("/pita.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel contentPane = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, this);
			}
		};
		
		contentPane.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));
		panel.setLayout(new GridLayout(0,1));
		
		JLabel label = new JLabel("LOGIN HERE", SwingConstants.CENTER);
		label.setForeground(Color.BLACK);
		label.setFont(label.getFont().deriveFont(20f));
		
		userText = new JTextField(20);
		passText = new JPasswordField();
		
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
		
		panel.add(label);
		panel.add(new JLabel("Username:"));
		panel.add(userText);
		panel.add(new JLabel("Password:"));
		panel.add(passText);
		panel.add(button);
		panel.add(signUp);
		panel.add(infoLabel);
		
		contentPane.add(panel, BorderLayout.CENTER);
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setLocationRelativeTo(null);
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
