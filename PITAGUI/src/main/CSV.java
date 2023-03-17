package main;

import java.io.FileInputStream;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class CSV {

	private String title;
	private String path;
	private String exercise;
	
	public CSV(String title,String path, String exercise) {
		 this.title = title;
		 this.path = path;
		 this.exercise = exercise;
	}
	
	public boolean csvSend() {
		
		try {
			FileInputStream fileIS = new FileInputStream(path+"\\"+title);
			ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
			byte[] byteBufferString = new byte[1024];
			
			for (int i; (i = fileIS.read(byteBufferString)) != -1;) 
		    {
				byteArrayOS.write(byteBufferString, 0, i);
		        
		    }
			
			String csvFile = Base64.getEncoder().encodeToString(byteArrayOS.toByteArray());
			
			
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"skeleSend"))
			    .header("cookie", driver.cookie)
			    .header("Content-Type", "application/json")
			    .method("POST", HttpRequest.BodyPublishers.ofString("{\n" +
	                    "  \"title\": \""+this.title+"\",\n" +
	                    "  \"csv\": \""+csvFile+"\",\n" +
	                    "  \"exercise\": \""+this.exercise+"\"\n" +
	                    "}"))
			    .build();
				HttpResponse<String> response;
				fileIS.close();
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				if (response.body().equals("Error"))
					return false;
				else if(response.body().equals("CSV Accepted!"))
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
	public String imgRec() {
		try {
			
			HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create(driver.url+"reqGraph?exercise="+exercise))
			    .header("cookie", driver.cookie)
			    .header("Content-Type", "application/json")
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
				HttpResponse<String> response;
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				if(!response.body().equals("Requested Graph DNE")) {
					byte[] videoBytes = Base64.getDecoder().decode(response.body());
					FileOutputStream fileOS = new FileOutputStream("./graphs/"+exercise+".jpg");
					fileOS.write(videoBytes);
					fileOS.close();
					return "./graphs/"+exercise+".jpg";
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return "";
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
}
