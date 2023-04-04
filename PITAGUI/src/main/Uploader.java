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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class Uploader {
	private JTextField vidText;
	private JTextField csvText;
	private PlayerPanel player;
	private JTextField exerText;

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

/*
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
*/
	public Uploader() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(300, 300, 300, 300));
		panel.setLayout(new GridLayout(0,1));

		//Video Stuff Below
		JButton upload = new JButton("Record");
		upload.addActionListener(new ActionListener() {

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

				        //File mostRecentFile = findMostRecentFile(directoryPath);
				        //if (mostRecentFile != null) {
				        //  System.out.println("Most recent file: " + mostRecentFile.getAbsolutePath());
				        //}

				        for (File file : matchingFiles) {
				          System.out.println(file.getAbsolutePath());
				          Runtime.getRuntime().exec(extractPath+"\\xefextract.exe -v -s "+file.getAbsolutePath());
				          Thread.sleep(20000); // waits for 20 second
				          if (file.delete()) {
				            System.out.println("Deleted file: " + file.getAbsolutePath());
				            } else {
				            System.out.println("Failed to delete file: " + file.getAbsolutePath());
									}
				        //Runtime.getRuntime().exec(extractPath+"\\xefextract.exe -v -s "+mostRecentFile);
				        }
				        }catch (IOException e) {
				            System.out.println("FROM CATCH" + e.toString());

				        }catch(InterruptedException e) {
				        	e.printStackTrace();
				        }
									// .\\videos
									Video vid = new Video(vidText.getText(),"KinectXEFTools-master\\KinectXEFTools-master\\examples\\XEFExtract\\bin\\Release\\netcoreapp2.1\\win10-x64\\videos",exerText.getText());
									CSV csv = new CSV(csvText.getText(),"KinectXEFTools-master\\KinectXEFTools-master\\examples\\XEFExtract\\bin\\Release\\netcoreapp2.1\\win10-x64\\videos",exerText.getText());
									vid.videoSend();
									csv.csvSend();
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
		JLabel label1 = new JLabel("CSV", SwingConstants.CENTER);
		JLabel label2 = new JLabel("Exercise", SwingConstants.CENTER);
		vidText = new JTextField(20);
		vidText.setBounds(100, 20, 165, 25);
		csvText = new JTextField(20);
		csvText.setBounds(100, 20, 165, 25);
		exerText = new JTextField(20);
		exerText.setBounds(100, 20, 165, 25);

		panel.add(label);
		panel.add(vidText);
		panel.add(label1);
		panel.add(csvText);
		panel.add(label2);
		panel.add(exerText);
		panel.add(upload);
		panel.add(main);

		frame.add(panel,BorderLayout.LINE_END);
		frame.add(player,BorderLayout.CENTER);
		frame.add(controlsPane, BorderLayout.SOUTH);
		frame.setTitle("PETA Uploader");

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
