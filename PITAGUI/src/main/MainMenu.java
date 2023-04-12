package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenu {
    
    protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
    protected JFrame frame = new JFrame();
    public MainMenu() {
        
    	
    	frame.setPreferredSize(new Dimension(700, 400));
    	
        // Create buttons
        JButton upload = new JButton("Upload New Exercise");
        upload.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new Uploader();
                frame.setVisible(false);
            }
        });
        
        JButton logout = new JButton("Log Out");
        logout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                
                logout();
                frame.setVisible(false);
                
                
            }
        });
        
        JButton watch = new JButton("Your Exercises");
        watch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new VideoPlayer();
                frame.setVisible(false);
            }
        });
        
        // Create main menu label
        JLabel label = new JLabel("Main Menu", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 36));
        
        // Create top panel for main menu label
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.add(label);
        
        // Create bottom panel for buttons and info label
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.setBackground(Color.WHITE);
        
        // Add text above the buttons
        JLabel instructionsTop = new JLabel("To record a new exercise select Upload New Exercise", SwingConstants.CENTER);
        instructionsTop.setFont(new Font("Arial", Font.PLAIN, 18));
        bottomPanel.add(instructionsTop);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add buttons
        bottomPanel.add(upload);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add text below the buttons
        JLabel instructionsBottom = new JLabel("To check your exercises select Your Exercises", SwingConstants.CENTER);
        instructionsBottom.setFont(new Font("Arial", Font.PLAIN, 18));
        bottomPanel.add(instructionsBottom);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        bottomPanel.add(watch);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        

        
        bottomPanel.add(logout);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        bottomPanel.add(infoLabel);
        bottomPanel.add(Box.createVerticalGlue());
        
        // Add top and bottom panels to main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("PITA");
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        frame.setResizable(false);
        frame.setVisible(true);
        
        accInfo();
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
	public void open() {
		frame.setVisible(true);
	}
}
