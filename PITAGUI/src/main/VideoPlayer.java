package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class VideoPlayer {
	private JTextField vidText;
	private PlayerPanel player;
	public VideoPlayer() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));
		//Video Stuff Bellow 
		JButton upload = new JButton("Upload");
		upload.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	Video vid = new Video(vidText.getText(),".\\videos");
	        	vid.videoSend();
	        }
	    });
		String[] vidList = getList().split(",");
		for(String title : vidList) {
			if(title.equals("User Has No Videos")) {
				JLabel label = new JLabel(title, SwingConstants.CENTER);
				panel.add(label);
				break;
			}
			JButton download = new JButton(title);
			download.addActionListener(new ActionListener() {
	
		        @Override
		        public void actionPerformed(ActionEvent event) {
		        	JButton clickedButton = (JButton) event.getSource();
		            Video vid = new Video(clickedButton.getText(), ".\\videos");
		        	if(!(vid.vidExist())) {
		        		vid.videoRec();
		        		
		        		player.play(vid.getPath());
		        	}
		        	else if((vid.vidExist()))
		        		
		        		player.play(vid.getPath()+"\\"+vid.getTitle());
		        }
		    });
			panel.add(download);
		}
		JButton main = new JButton("Main Menu");
		main.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	driver.mainMenu.open();
	        	player.close();
	        	frame.setVisible(false);
	        }
	    });
		
		JPanel controlsPane = new JPanel();
        JButton pauseButton = new JButton("Pause");
        controlsPane.add(pauseButton);
        JButton rewindButton = new JButton("Rewind");
        controlsPane.add(rewindButton);
        JButton skipButton = new JButton("Skip");
        controlsPane.add(skipButton);
        player = new PlayerPanel();
		

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.player.controls().pause();
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	player.player.controls().skipTime(-10000);
            }
        });

        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	player.player.controls().skipTime(10000);
            }
        });

		
		JLabel label = new JLabel("VIDEO", SwingConstants.CENTER);
		vidText = new JTextField(20);
		vidText.setBounds(100, 20, 165, 25);
		
		
		panel.add(label);
		panel.add(vidText);
		panel.add(upload);
		panel.add(main);
		
		frame.add(panel,BorderLayout.LINE_END);
		frame.add(player,BorderLayout.CENTER);
		frame.add(controlsPane, BorderLayout.SOUTH);
		frame.setTitle("PETA Video Player");
		
		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.close();
                System.exit(0);
            }
        });
		
		
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public String getList() {
		  
		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"reqVidList"))
			    .header("cookie", driver.cookie)
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
				HttpResponse<String> response;
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				return response.body();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		
		return "";
	}
	
}
