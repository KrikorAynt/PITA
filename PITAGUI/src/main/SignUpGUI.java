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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.net.URI;

public class SignUpGUI {
    private JTextField userText;
    private JPasswordField passText;
    protected JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
    private Image backgroundImage;

    public SignUpGUI() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("PITA Sign Up");
        frame.setPreferredSize(new Dimension(700, 400));

        try {
            backgroundImage = ImageIO.read(new File(".\\pita.jpg"));
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
        contentPane.setBackground(Color.ORANGE); // Set the background color to black

        contentPane.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(00, 400, 00, 10));
        panel.setLayout(new GridLayout(0, 1));

        JLabel label = new JLabel("SIGN UP HERE", SwingConstants.CENTER);
        label.setForeground(Color.BLACK);
        label.setFont(label.getFont().deriveFont(20f));

        userText = new JTextField();
        userText.setPreferredSize(new Dimension(100, 20));

        passText = new JPasswordField();
        passText.setPreferredSize(new Dimension(100, 20));

        JButton button = new JButton("Sign Up");
        // button.setPreferredSize(new Dimension(80, 30));

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                String user = userText.getText();
                @SuppressWarnings("deprecation")
                String password = passText.getText();

                if (create(user, password)) {
                    new LoginGUI();
                    user = "";
                    password = "";
                    userText.setText("");
                    passText.setText("");
                    frame.setVisible(false);
                } else {
                    user = "";
                    password = "";
                    userText.setText("");
                    passText.setText("");
                }
            }
        });

        JButton login = new JButton("Login");
        // login.setPreferredSize(new Dimension(80, 30));

        login.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                new LoginGUI();
                frame.setVisible(false);
            }
        });

        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new GridLayout(2, 2));
        fieldPanel.add(new JLabel("Username:"));
        fieldPanel.add(userText);
        fieldPanel.add(new JLabel("Password:"));
        fieldPanel.add(passText);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(button);
        buttonPanel.add(login);

        panel.add(label);
        panel.add(fieldPanel);
        panel.add(buttonPanel);
        panel.add(infoLabel);

        contentPane.add(panel, BorderLayout.CENTER);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(button);
        frame.setVisible(true);
    }


	
	public boolean create(String username, String password) {
		  
		try {
			HttpRequest request = HttpRequest.newBuilder()
				    .uri(URI.create(driver.url+"signup"))
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
