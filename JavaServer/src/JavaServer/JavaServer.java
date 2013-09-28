package JavaServer;

/**
 * This application's purpose is to listen for connections from clients
 * and once the connection is established, it responds to file transfer commands
 * from the clients.
 * 
 * The application should accept two functions: GET and SEND. The client uses 
 * GET to request a file from the server. Client uses SEND to send a file to the
 * server.
 * 
 * In this application, both members have provided supplementary files to
 * demonstrate the use of TCP and Socket programming. The code also works with
 * each other, resulting in cross-platform compatibility.
 * 
 * Usage of JavaServer:
 * 
 * java JavaServer
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

    public JavaServer() {};
    /**
     * @param args the command line arguments
     */
    static private final int BUFFER = 2048;
    static private Socket sock = null;
    static private int count;
    
    public static void main(String[] args) {
        try {
            ServerSocket servsock = new ServerSocket(7005);
            System.out.println("Listening for connections...");
            while (true) {
              sock = servsock.accept();
              System.out.println("Connected! Host: " + servsock.getInetAddress().getHostAddress()
                      + " port: " + servsock.getLocalPort());

              InputStream in = sock.getInputStream();
              
              // Listen for GET or SEND packet,
              // then call the appropriate method.
              listen(in);

              sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static public void GET(String fileName) {
        // send to client
        System.out.println("GET() method called.");
        try {
            File myFile = new File(fileName);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            OutputStream os = sock.getOutputStream();
            byte[] byteArray = new byte[BUFFER];
            while((count = bis.read(byteArray)) >= 0) {
                  os.write(byteArray, 0, count);
                  os.flush();
            } 
            System.out.println("File Transfer Finished: " + fileName + ".");
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static public void SEND(String fileName) {
        // receive from client
        System.out.println("SEND() method called.");
        try {
            File myFile = new File(fileName);
            InputStream is = sock.getInputStream();
            
            FileOutputStream fos = new FileOutputStream(myFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            byte[] byteArray = new byte[BUFFER];
            while ((count = is.read(byteArray)) >= 0) {
                bos.write(byteArray, 0, count);
            }
            System.out.println("File Transfer finished: " + fileName + ".");
            fos.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static public void sendPacket(String str, OutputStream sout) {
        byte[] bbuf = str.getBytes();

        try {
            sout.write(bbuf);
            System.out.println("Sending packet: " + str.toString() + " ...");
        } catch (IOException ioe) {
            System.err.println("Error with Packet transmission. Terminating connection.");
            return;
        }
    }
    
    static public void listen(InputStream istr) {
        byte[] buf = new byte[BUFFER];
        int len;
        while (true) {
            try {
                len = istr.read(buf, 0, BUFFER);
            }
            catch (IOException ioe) {
                System.err.println("Error with Packet transmission. Terminating connection.");
                break;	// probably a socket ABORT; treat as a close
            }
            
            if (len == -1) break;
            String str = "";
            str = new String(buf, 0, len);
            System.out.println("Received: '" + str + "' from Host.");

            if (str.toString().equalsIgnoreCase("GET")) {
                try {
                    sendPacket("ACK", sock.getOutputStream());
                    System.out.println("Calling GET() Method.");
                    Thread.sleep(2000);
                    GET("text.txt");
                } catch (IOException ioe) {
                    System.err.println("ERROR: COULD NOT SEND PACKET!");
                    ioe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                
            } else if (str.toString().equalsIgnoreCase("SEND")) {
                try {
                    sendPacket("ACK", sock.getOutputStream());
                    System.out.println("Calling SEND() Method.");
                    SEND("Flight.docx");
                    Thread.sleep(2000);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            } else {
                // Invalid packet. Terminate the session.
                System.err.println("Packet content invalid.");
                break;
            }
        }
        
        try {
            istr.close();
        } catch (IOException ioe) {
            System.err.println("Error: Bad Stream Close");
            return;
        }
            
    }
}
