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
 * java -jar "JavaClient.jar"
 *      Enter Server IP Address: [Server IP address]
 *      Enter Server Port:       [Server Port]
 *      Enter Method:            [GET | SEND]
 *      Enter File Name:         [file name]
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
import java.util.Scanner;

public class JavaClient {

    static private final int BUFFER = 8196;         // An int to specify buffer for transfer.
    static private int count;                       // A global variable placeholder for int.
    static private Socket sock;                     // Globalized socket object
    static private String address = "127.0.01";     // Server address; default value: 127.0.0.1
    static private int port = 7005;                 // Server port; default value: 7005
    static private String method = "GET";           // Method; default value: GET
    static private String fileName = "text.txt";    // FileName; default value: text.txt
    static private OutputStream sout = null;        // Globalized output stream object

    /**
     * User enters parameters in main method. 
     * Application then attempts to connect to server. If it does...
     * then the user specified directions will take place.
     * 
     */
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter Server IP Address: ");
        address = scan.next();
        System.out.println();
        System.out.print("Enter Server Port: ");
        port = scan.nextInt();
        System.out.println();
        System.out.print("Enter Method: ");
        method = scan.next();
        System.out.println();
        System.out.print("Enter File Name: ");
        fileName = scan.next();
        
        try {
            // Connect to the Server.
            sock = new Socket(address, port);

            System.out.println("Connection Established: " + sock.getInetAddress()
                    + " port: " + sock.getPort());

            // Prep the sockets for input / output exchange.
            InputStream is = sock.getInputStream();
            System.out.println("Listening...");

            try {
                sout = sock.getOutputStream();
            } catch (IOException ioe) {
                System.err.println("Encountered an error.");
                ioe.printStackTrace();
            }
            
            // Send a packet to the Server with our method and output specs.
            sendPacket(method, sout);
            System.out.println("Sending packet...");

            // listen for ACK from Server, then send request for file.
            listen(is);
            
            // close the socket after transfer is finished.
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sendPacket() is an internal method that the application uses to send 
     * messages to the Server.
     * 
     * The server has written protocols that will either accept or reject actions
     * based on the validity of the message.
     * 
     * @param str - the Message to send to the Server.
     * @param sout - the Output Stream.
     */
    static private void sendPacket(String str, OutputStream sout) {
        byte[] buf = str.getBytes();

        try {
            // Send the message
            sout.write(buf);
            System.out.println("Sending request: '" + str + "' ...");
        } catch (IOException ioe) {
            System.err.println("Packet sending messed up. Terminating connection.");
            return;
        }
    }

    /**
     * listen() is an internal method that the application uses to listen from the
     * Server. It has a a set of protocols that, upon receiving the expected message
     * from the Server, either requests a file from the Server or to upload to the
     * Server.
     * 
     * @param istr - the Input Stream of the application listening from the Server
     */
    static private void listen(InputStream istr) {
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

            // Listen for packets.
            String str = new String(buf, 0, len);
            System.out.println("Received: '" + str + "' from Host.");

            if (str.toString().trim().equalsIgnoreCase("ACK")) {
                if (method.toString().equalsIgnoreCase("GET")) {
                    // Send a packet with our desired filename to be retrieved 
                    // from the server.
                    sendPacket(fileName, sout);

                    System.out.println("Calling sendPacket() Method.");
                    System.out.println("Requesting file: " + fileName + "...");
                    try {
                        FileOutputStream fos = new FileOutputStream(fileName);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        while ((count = istr.read(buf)) > -1) {
                            bos.write(buf, 0, count);
                        }
                        System.out.println("File Transfer finished.");
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                } else if (method.toString().equalsIgnoreCase("SEND")) {
                    
                    // Send a packet with our desired filename to be uploaded to
                    // the server.
                    sendPacket(fileName, sout);

                    System.out.println("Calling sendPacket() Method.");
                    System.out.println("Sending file: " + fileName + "...");

                    try {
                        File myFile = new File(fileName);
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
                        OutputStream os = sock.getOutputStream();
                        byte[] byteArray = new byte[BUFFER];
                        while ((count = bis.read(byteArray)) > -1) {
                            os.write(byteArray, 0, count);
                            os.flush();
                        }
                        System.out.println("File Transfer Finished: " + fileName + ".");
                        os.close();
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
