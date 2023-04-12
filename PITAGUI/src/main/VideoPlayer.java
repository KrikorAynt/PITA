package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MediaTracker;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class VideoPlayer {
	
	private PlayerPanel player;
	private JLabel graph;

	public VideoPlayer() {
	    JFrame frame = new JFrame();
	    frame.setTitle("PITA Video Player");

	    JPanel panel = new JPanel(new BorderLayout());

	    // Video player panel
	    player = new PlayerPanel();
	    JPanel playerPanel = new JPanel(new BorderLayout());
	    playerPanel.add(player, BorderLayout.CENTER);

	    // Video controls panel
	    JPanel controlsPane = new JPanel();
	    JButton pauseButton = new JButton("Pause");
	    JButton rewindButton = new JButton("Rewind");
	    JButton skipButton = new JButton("Skip");
	    controlsPane.add(pauseButton);
	    controlsPane.add(rewindButton);
	    controlsPane.add(skipButton);

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

	    // Video list panel
	    JPanel listPanel = new JPanel(new GridLayout(0, 1));
	    String[] vidList = getList().split(",");
	    for (String title : vidList) {
	        if (title.equals("User Has No Videos")) {
	            JLabel label = new JLabel(title, SwingConstants.CENTER);
	            listPanel.add(label);
	            break;
	        }
	        Video vid = new Video(title, ".\\videos");
            CSV csv = new CSV(title,".\\videos",vid.getExercise());
	        JButton download = new JButton("<html>"+vid.getExercise()+"<br>"+title.replace(".avi", "")+"</html>");
	        download.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent event) {
	                JButton clickedButton = (JButton) event.getSource();
	                
	                String graphImg = csv.imgRec();
	                ImageIcon icon = new ImageIcon(graphImg);
	                graph.setIcon(null); // clear the previous image
	                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
	                    graph.setText(null); // clear any previous text
	                    graph.setIcon(icon); // set the new image
	                } else {
	                    graph.setText("Error loading image");
	                }
	                if (!(vid.vidExist())) {
	                    vid.videoRec();
	                    player.play(vid.getPath());
	                } else if ((vid.vidExist())) {
	                    player.play(vid.getPath() + "\\" + vid.getTitle());
	                }
	            }
	        });
	        listPanel.add(download);
	    }

	    // Graph panel
	    graph = new JLabel();
	    graph.setHorizontalAlignment(JLabel.CENTER);
	    graph.setVerticalAlignment(JLabel.CENTER);
	    graph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	    // Main menu button
	    JButton main = new JButton("Main Menu");
	    main.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent event) {
	            driver.mainMenu.open();
	            player.close();
	            frame.setVisible(false);
	        }
	    });

	    // Add components to main panel
	    panel.add(playerPanel, BorderLayout.CENTER);
	    panel.add(controlsPane, BorderLayout.SOUTH);
	    panel.add(listPanel, BorderLayout.LINE_START);
	    panel.add(main, BorderLayout.NORTH);
	    panel.add(graph, BorderLayout.LINE_END);

	    frame.add(panel);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
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
