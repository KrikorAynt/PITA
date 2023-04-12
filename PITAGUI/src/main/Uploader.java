package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;


public class Uploader {
	private JTextField vidText;
	private JTextField csvText;
	private JTextField exerText;
	private JComboBox<String> exerDrop;
	private ImageIcon backgroundImage;
	private PlayerPanel player;
	
    

	public static List<File> findFilesByExtension(String directoryPath, String extension) {
		File directory = new File(directoryPath);
		List<File> matchingFiles = new ArrayList<>();

		if (!directory.isDirectory()) {
			System.err.println(directoryPath + " is not a directory!");
			return matchingFiles;
		}

		File[] files = directory.listFiles();
		if (files == null) {
			System.err.println("Error: could not list files in " + directoryPath);
			return matchingFiles;
		}

		for (File file : files) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
				matchingFiles.add(file);
			}
			else if (file.isDirectory()) {
				matchingFiles.addAll(findFilesByExtension(file.getAbsolutePath(), extension));
			}
		}
		return matchingFiles;
	}


  public static File findMostRecentFile(String directoryPath) {
    File directory = new File(directoryPath);
    File[] files = directory.listFiles();
    if (files == null || files.length == 0) {
      System.err.println("No files found in " + directoryPath);
      return null;
    }

    return getMostRecentFile(files);
  }

  private static File getMostRecentFile(File[] files) {
    File mostRecent = files[0];
    for (int i = 1; i < files.length; i++) {
      if (files[i].lastModified() > mostRecent.lastModified()) {
        mostRecent = files[i];
      }
    }
    return mostRecent;
  }

	public Uploader() {
		JFrame frame = new JFrame();
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridLayout(0,1));
		contentPane.add(panel, BorderLayout.CENTER);
		frame.setPreferredSize(new Dimension(1000, 1000));

		 backgroundImage = new ImageIcon(".\\2Dview.jpg");

	        
		//Video Stuff Below
		JButton record = new JButton("Record");
		record.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	//Add recording and converting here
						try {
					    // Kinect Studio Automation
					        String kinectStudioPath = "C:\\Program Files\\Microsoft SDKs\\Kinect\\v2.0_1409\\Tools\\KinectStudio\\KStudio.exe";
					        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start \"KStudio\" \"" + kinectStudioPath + "\"");
					        builder.redirectErrorStream(true);
					        builder.start();
								}catch (IOException e) {
										System.out.println("FROM CATCH" + e.toString());
								}
							}
		});


		JButton upload = new JButton("Upload");
		upload.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
        	//Add recording and converting here
						try{
								// THE CONVERSION PART STAYS IN UPLOAD BUTTON (20 SECONDS TO COMPLETE CONVERSION)
			    			// XEF Conversion Automation
					        String extractPath = "C:\\Users\\antho\\OneDrive\\Documents\\GitHub\\PITA\\PITA\\PITAGUI\\KinectXEFTools-master\\KinectXEFTools-master\\examples\\XEFExtract\\bin\\Release\\netcoreapp2.1\\win10-x64";
					        String directoryPath = extractPath+"\\videos";
					        String extension = ".xef";
					        List<File> matchingFiles = findFilesByExtension(directoryPath, extension);
	
					        File mostRecentFile = findMostRecentFile(directoryPath);
					        if (mostRecentFile != null) {
					          System.out.println("Most recent file: " + mostRecentFile.getAbsolutePath());
					        }
	
					        for (File file : matchingFiles) {
					          System.out.println(file.getAbsolutePath());
					          Runtime.getRuntime().exec(extractPath+"\\xefextract.exe -v -s "+file.getAbsolutePath());
					          Thread.sleep(20000); // waits for 20 second
					          if (file.delete()) {
					            System.out.println("Deleted file: " + file.getAbsolutePath());
					            } else {
					            System.out.println("Failed to delete file: " + file.getAbsolutePath());
										}
					          Runtime.getRuntime().exec(extractPath+"\\xefextract.exe -v -s "+mostRecentFile.getAbsolutePath());
					          
					          String fileName = mostRecentFile.getName();
					          fileName = fileName.substring(0, fileName.lastIndexOf('.'));
					          Video vid = new Video(fileName+"_Video.avi",".\\KinectXEFTools-master\\KinectXEFTools-master\\examples\\XEFExtract\\bin\\Release\\netcoreapp2.1\\win10-x64\\videos",(String)exerDrop.getSelectedItem());
							  CSV csv = new CSV(fileName+"_Skeleton.txt",".\\KinectXEFTools-master\\KinectXEFTools-master\\examples\\XEFExtract\\bin\\Release\\netcoreapp2.1\\win10-x64\\videos",(String)exerDrop.getSelectedItem());
							  vid.videoSend();
							  csv.csvSend();
				        }
				        }catch (IOException e) {
				            System.out.println("FROM CATCH" + e.toString());

				        }catch(InterruptedException e) {
				        	e.printStackTrace();
				        }
						// .\\videos
						
						
	        }
	    });

		JButton example = new JButton("Show Example Video");
		example.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent event) {
	        	try {
	        		String exercise = (String)exerDrop.getSelectedItem();
	    			HttpRequest request = HttpRequest.newBuilder()
	    			    .uri(URI.create(driver.url+"reqExerVid?exercise="+exercise))
	    			    .header("cookie", driver.cookie)
	    			    .header("Content-Type", "application/json")
	    			    .method("GET", HttpRequest.BodyPublishers.noBody())
	    			    .build();
	    				HttpResponse<String> response;
	    				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
	    				if(!response.body().equals("Requested Video DNE")) {
	    					byte[] videoBytes = Base64.getDecoder().decode(response.body());
	    					FileOutputStream fileOS = new FileOutputStream("./videos/"+exercise+"_example.avi");
	    					fileOS.write(videoBytes);
	    					fileOS.close();
	    					player.play("./videos/"+exercise+"_example.avi");
	    					
	    				}
	    				
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	        }
	    });
		
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

		

        exerDrop = new JComboBox<>(getExerList().split(","));
        
		vidText = new JTextField(20);
		vidText.setBounds(100, 20, 165, 25);
		csvText = new JTextField(20);
		csvText.setBounds(100, 20, 165, 25);
		
		player = new PlayerPanel();
		JPanel playerPanel = new JPanel(new BorderLayout());
	    playerPanel.add(player, BorderLayout.CENTER);
	    
	    JLabel instructionsTop = new JLabel("<html><br>To upload a newly recorded exercise select Upload <br> To upload a existing .xef file place it in the videos folder and select Upload</html>", SwingConstants.CENTER);
        instructionsTop.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel instructionsBottom = new JLabel("<html>To record a new exercise select Record <br> For recording select all streams except 'Nui Long Exposure IR' <br> Check your 2D settings to match the example image -><br></html>", SwingConstants.CENTER);
        instructionsBottom.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel graph = new JLabel();
        graph.setText(null); // clear any previous text
        graph.setIcon(backgroundImage);
        
        panel.add(instructionsTop);
        panel.add(upload);
		panel.add(instructionsBottom);
		//contentPane1.add(panel, BorderLayout.CENTER);
		//frame.setContentPane(contentPane1);
		panel.add(record);
		panel.add(example);
		panel.add(exerDrop);
		panel.add(upload);
		panel.add(main);

		frame.add(panel,BorderLayout.LINE_START);
		frame.add(playerPanel,BorderLayout.CENTER);
		frame.add(graph,BorderLayout.LINE_END);
		frame.add(controlsPane,BorderLayout.SOUTH);
		frame.setTitle("PITA Uploader");

		frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	player.close();
                System.exit(0);
            }
        });


		frame.pack();

		frame.setResizable(false);
		frame.setVisible(true);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public String getExerList() {

		try {
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"reqExerList"))
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
