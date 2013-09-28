package JavaClient;

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
 * Usage of JavaClient:
 * 
 * java JavaClient [address] [port] [GET | SEND] [filePath/fileName]
 *              [address]   = the ip address of the server
 *              [port]      = the port specified by the server
 *              [GET]       = as the client, to retrieve from the server
 *              [SEND]      = as the client, to send to the server
 *    [[filePath]/fileName] = specifying the file to be sent or retrieved
 *                            The filePath is not required if the method is GET.    
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
import java.net.Socket;

public class JavaClient {

    // public JavaClient() {}
    /**
     * @param args the command line arguments
     */
    static private final int BUFFER = 2048;
    static private int count;
    static private Socket sock;
    static private String address = "";
    static private int port;
    static private String method = "";
    static private String fileName = "";

    public static void main(String[] args) {
        address = "127.0.0.1";
        port = 7005;
        method = "GET"; // cmd line argument 1
        fileName = "text.txt"; // cmd line argument 2    
        try {
            sock = new Socket(address, port);

            System.out.println("Connection Established: " + sock.getInetAddress()
                    + " port: " + sock.getPort());

            InputStream is = sock.getInputStream();
            System.out.println("Listening...");

            OutputStream sout = null;
            try {
                sout = sock.getOutputStream();
            } catch (IOException ioe) {
                System.err.println("Something fucked up.");
            }

            

            //method = "SEND";
            //fileName = "Flight.docx";

            sendPacket(method, sout);
            System.out.println("Sending packet...");

            // listen for ACK from Server, then send request for file.
            listen(is, method, fileName);

            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void sendPacket(String str, OutputStream sout) {
        byte[] buf = str.getBytes();

        try {
            sout.write(buf);
            System.out.println("Sending request: '" + str + "' ...");
        } catch (IOException ioe) {
            System.err.println("Packet sending fucked up.");
            return;
        }
    }

    static public void listen(InputStream istr, String method, String fileName) {
        byte[] buf = new byte[BUFFER];
        int len = 0;
        while (true) {

            try {
                len = istr.read(buf, 0, BUFFER);
            } catch (IOException ioe) {
                System.err.println("bad read");
                break;	// probably a socket ABORT; treat as a close
            } catch (Exception e) {
                e.printStackTrace();
            } 
            if (len == -1) {
                break;
            }
            
            String str = new String(buf, 0, len);
            System.out.println("Received: '" + str + "' from Host.");

            if (str.toString().equalsIgnoreCase("ACK")) {
                if (method.toString().equalsIgnoreCase("GET")) {
                    // sendPacket('text.txt', );
                    System.out.println("Calling sendPacket() Method.");
                    System.out.println("Requesting file: 'text.txt'.");
                    try {
                        FileOutputStream fos = new FileOutputStream(fileName);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        while ((count = istr.read(buf)) >= 0) {
                            bos.write(buf, 0, count);
                        }
                        System.out.println("File Transfer finished.");
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                } else if (method.toString().equalsIgnoreCase("SEND")) {
                    System.out.println("Calling sendPacket() Method.");
                    System.out.println("Sending file: 'Flight.docx'.");

                    try {
                        while (!sock.isClosed()) {

                            File myFile = new File(fileName);
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                            OutputStream os = sock.getOutputStream();
                            byte[] byteArray = new byte[BUFFER];
                            while ((count = bis.read(byteArray)) >= 0) {
                                os.write(byteArray, 0, count);
                                os.flush();
                            }
                            sock.close();
                        }
                        System.out.println("File Transfer Finished.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            } else {
                // Invalid packet. Terminate the session.
                System.err.println("Packet content invalid.");
                break;
            }
        }

        try {
            istr.close();
        } catch (IOException ioe) {
            System.err.println("bad stream close");
            return;
        }

    }
}
