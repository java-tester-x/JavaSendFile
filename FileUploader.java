import java.net.*;
import java.io.*;

public class FileUploader {

	public static void main(String[] args) throws Exception{
		System.out.println("Hi!");
		//dowload();
		upload();
	}

	public static void dowload() throws IOException {
		try {
		    URL myURL = new URL("http://orcl.rfpgu.ru");

		    BufferedReader in = new BufferedReader(new InputStreamReader(myURL.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
		} 
		catch (IOException e) {
		    // exception handler code here
		    // ...
		}
	}
    
	public static void upload() throws IOException {
	    URL myURL = new URL("http://orcl.rfpgu.ru/upload_1981.php");

	    String param    = "value";
	    File textFile   = new File("uploads/my_start.txt");
	    File binaryFile = new File("uploads/winter.jpg");
	    // Just generate some unique random value.
	    String boundary = Long.toHexString(System.currentTimeMillis());
		// Line separator required by multipart/form-data.
	    String CRLF     = "\r\n";
		String charset  = "UTF-8";
	    
	    URLConnection connection = myURL.openConnection();
	    connection.setDoOutput(true);
	    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

	    PrintWriter writer = null;
	    try {
	        OutputStream output = connection.getOutputStream();
	        writer = new PrintWriter(new OutputStreamWriter(output, charset), true); // true = autoFlush, important!
	        
	        // Send normal param.
	        writer.append("--" + boundary).append(CRLF);
	        writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
	        writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
	        writer.append(CRLF);
	        writer.append(param).append(CRLF).flush();

	        // Send text file.
	        System.out.println("textFile: "+textFile.getName());
	        System.out.println("Content-Type: "+URLConnection.guessContentTypeFromName(textFile.getName()));
	        writer.append("--" + boundary).append(CRLF);
	        writer.append(
	        	"Content-Disposition: form-data; name=\"upfile\"; filename=\""
	        	+ textFile.getName() + "\"").append(CRLF);
	        writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
	        writer.append(CRLF).flush();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(textFile), charset)
        	);
	        try {
	            for (String line; (line = reader.readLine()) != null;) {
	                writer.append(line).append(CRLF);
	            }
	        } finally {
	            try { reader.close(); } catch (IOException logOrIgnore) {}
	        }
	        writer.flush();

	        // Send binary file.
	        System.out.println("binaryFile: "+binaryFile.getName());
	        System.out.println("Content-Type: "+URLConnection.guessContentTypeFromName(binaryFile.getName()));
	        writer.append("--" + boundary).append(CRLF);
	        writer.append(
	            "Content-Disposition: form-data; name=\"upfile\"; filename=\""
	            + binaryFile.getName() + "\"").append(CRLF);
	        writer.append(
	            "Content-Type: "
                + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
	        writer.append("Content-Transfer-Encoding: binary").append(CRLF);
	        writer.append(CRLF).flush();
	        InputStream input = new FileInputStream(binaryFile);
	        try {
	            byte[] buffer = new byte[1024];
	            for (int length = 0; (length = input.read(buffer)) > 0;) {
	                output.write(buffer, 0, length);
	            }
	            output.flush(); // Important! Output cannot be closed. Close of writer will close output as well.
	        } finally {
	            try { input.close(); } catch (IOException logOrIgnore) {}
	        }
	        writer.append(CRLF).flush(); // CRLF is important! It indicates end of binary boundary.

	        // End of multipart/form-data.
	        writer.append("--" + boundary + "--").append(CRLF);
	    } finally {
	        if (writer != null) writer.close();
	    }

	    //Retrieve response
        BufferedReader br = new BufferedReader(  
        new InputStreamReader(connection.getInputStream()));  
        String line = br.readLine();  
        while ( line != null ) {  
            System.out.println(line);  
            line = br.readLine();  
        }  
        br.close();
	}

}