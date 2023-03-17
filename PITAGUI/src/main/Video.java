package main;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class Video {

	private String title;
	private String path;
	private String exercise;
	
	public Video(String title,String path, String exercise) {
		 this.title = title;
		 this.path = path;
		 this.exercise = exercise;
	}
	public Video(String title, String path) {
		 this.title = title;
		 this.path = path;
		 try {
				HttpRequest request = HttpRequest.newBuilder()
				    .uri(URI.create(driver.url+"reqExer?title="+title))
				    .header("cookie", driver.cookie)
				    .method("GET", HttpRequest.BodyPublishers.noBody())
				    .build();
					HttpResponse<String> response;
					response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
					this.exercise = response.body().replace("[", "").replace("]", "").replace("\"", "");;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	}
	public boolean videoSend() {
		
		try {
			FileInputStream fileIS = new FileInputStream(path+"\\"+title);
			ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			byte[] byteBufferString = new byte[1024];
			
			for (int i; (i = fileIS.read(byteBufferString)) != -1;) 
		    {
				byteArrayOS.write(byteBufferString, 0, i);
		        
		    }
			
			String video = Base64.getEncoder().encodeToString(byteArrayOS.toByteArray());
			
			
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"sendVid"))
			    .header("cookie", driver.cookie)
			    .header("Content-Type", "application/json")
			    .method("POST", HttpRequest.BodyPublishers.ofString("{\n" +
	                    "  \"title\": \""+title+"\",\n" +
	                    "  \"vid\": \""+video+"\",\n" +
	                    "  \"exercise\": \""+exercise+"\"\n" +
	                    "}"))
			    .build();
				HttpResponse<String> response;
				fileIS.close();
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				if (response.body().equals("Video Exists!"))
					return false;
				else if(response.body().equals("Video Accepted!"))
					return true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return false;
				
	}
	public boolean videoRec() {
		try {
			
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"reqVid?title="+title))
			    .header("cookie", driver.cookie)
			    .header("Content-Type", "application/json")
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
				HttpResponse<String> response;
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				if(!response.body().equals("Requested Video DNE")) {
					byte[] videoBytes = Base64.getDecoder().decode(response.body());
					FileOutputStream fileOS = new FileOutputStream("./videos/"+title);
					fileOS.write(videoBytes);
					fileOS.close();
					path = "./videos/"+title;
					return true;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return false;
	}
	public String getPath() {
		return path;
	}
	public String getTitle() {
		return title;
	}
	public boolean vidExist() {
		File video = new File(path+"\\"+title);
        return video.exists();
	}
	public String getExercise() {
		
		return exercise;
	}
}
