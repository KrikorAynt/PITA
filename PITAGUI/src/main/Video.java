package main;

import java.io.FileInputStream;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class Video {

	private String title;
	private String path;
	
	public Video(String title,String path) {
		 this.title = title;
		 this.path = path;
		
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
			    .method("POST", HttpRequest.BodyPublishers.ofString("{\n\"title\": \""+title+"\",\n\"vid\": \""+video+"\"\n}"))
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
				
				byte[] videoBytes = Base64.getDecoder().decode(response.body());
				FileOutputStream fileOS = new FileOutputStream("./videos/"+title);
				fileOS.write(videoBytes);
				fileOS.close();
				path = "./videos/"+title;
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
}
