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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SignUpGUI {

	private JTextField userText;
	private JPasswordField passText;
	protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
	
	public SignUpGUI() {
		JFrame frame = new JFrame();
		
		JButton signUp = new JButton("Sign Up");
		signUp.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	String user = userText.getText();
	    		@SuppressWarnings("deprecation")
				String password = passText.getText();
	    		
	    		if(create(user, password)) {
	    			new LoginGUI();
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
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	            new LoginGUI();
	            frame.setVisible(false);
	        }
	    });
		
		JLabel label = new JLabel("SIGN UP HERE", SwingConstants.CENTER);
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
		panel.add(signUp);
		panel.add(login);
		panel.add(infoLabel);
		
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PETA Sign Up");
		frame.getRootPane().setDefaultButton(signUp);
		
		
		frame.pack();
		frame.setVisible(true);
	}
	public boolean create(String username, String password) {
		  
		try {
			HttpRequest request = HttpRequest.newBuilder()
				    .uri(URI.create("http://localhost:5000/signup"))
				    .header("cookie", "session=s%253AHJEksfpD4EWcyB4TtdnSOBP3LKcxBY25.db2YDmriKgk8jSy87tOYpty0l%252Farw%252FEd3uRIoUjJaAY")
				    .header("Content-Type", "application/json")
				    .method("POST", HttpRequest.BodyPublishers.ofString("{\n\t\"user\": \""+username+"\",\n\t\"pass\": \""+password+"\"\n}"))
				    .build();
				HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				infoLabel.setText(response.body());
				if (response.body().equals("User Created. Well done!"))
					return true;
				else if(response.body().equals("User already exists. Try again!"))
					return false;
				
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;  
		
	}
}
