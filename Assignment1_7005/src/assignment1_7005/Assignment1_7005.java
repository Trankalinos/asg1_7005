package assignment1_7005;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Assignment1_7005 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*String host = "www.google.com";
        String protocol = "http";
        try {
            Socket socket = new Socket(host, 80);
            OutputStream os = socket.getOutputStream();
            boolean autoflush = true;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // send an HTTP request to the web server
            out.println("GET / HTTP/1.1");
            out.println("Host: " + host + ":80");
            out.println("Connection: Close");
            out.println();

            // read the response
            boolean loop = true;
            StringBuilder sb = new StringBuilder(8096);
            while (loop) {
                if (in.ready()) {
                    int i = 0;
                    while (i != -1) {
                        i = in.read();
                        sb.append((char) i);
                    }
                    loop = false;
                }
            }

            // display the response to the out console
            System.out.println(sb.toString());
            socket.close();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        try {
            ServerSocket servsock = new ServerSocket(7005);
            File myFile = new File("Flight.docx");
            int count;
            while (true) {
              Socket sock = servsock.accept();
              /* byte[] mybytearray = new byte[(int) myFile.length()]; */
              byte[] mybytearray = new byte[2048];
              BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
              // bis.read(mybytearray, 0, mybytearray.length);
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
