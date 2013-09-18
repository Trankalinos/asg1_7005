package JavaServer;

/**
 * This application's purpose is to either listen for connections from clients
 * and once the connection is established, it responds to file transfer commands
 * from the clients.
 * 
 * The application should accept two functions: GET and SEND. The client uses 
 * GET to request a file from the server. Client uses SEND to send a file to the
 * server.
 * 
 * 
 * @author  David Tran      Java
 * @partner Martin Javier   Ruby
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args[0].toString().equalsIgnoreCase("GET")) {
            GET(args[1]);
        } else if (args[0].toString().equalsIgnoreCase("SEND")) {
            SEND(args[1]);
        } else {
            System.out.println("Java Socket File Transfer");
            System.out.println("GET [fileName] - retrieves the specified file and transfers to client.");
            System.out.println("SEND [fileName] - sends a file to the server from the client.");    
        }
    }
    
    public static void GET(String fileName) {
        try {
            Socket sock = new Socket("127.0.0.1", 7005);
            byte[] mybytearray = new byte[2048];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream("Flight.docx");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            int count;
            
            while ((count = is.read(mybytearray)) >= 0) {
                bos.write(mybytearray, 0, count);
            }
            
            bos.close();
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void SEND(String fileName) {
        try {
            ServerSocket servsock = new ServerSocket(7005);
            File myFile = new File(fileName);
            int count;
            while (true) {
              Socket sock = servsock.accept();
              byte[] mybytearray = new byte[2048];
              BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));

              OutputStream os = sock.getOutputStream();
              
              while((count = bis.read(mybytearray)) >= 0) {
                  os.write(mybytearray, 0, count);
                  os.flush();
              }
              
              sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
