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
 * java -jar "JavaServer.jar"
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

    
    static private final int BUFFER = 8196;     // An int to specify buffer for transfer.
    static private Socket sock = null;          // Globalized socket to be instantiated in main() method
    static private int count;                   // A globalized int placeholder

    /**
     * Sets up the application to accept any incoming connections on the 
     * specified port. Once connected, listen for the messages sent by the client.
     * Return valid messages based on the protocols set up.
     * 
     * Once everything is verified and validated, conduct the file transfer.
     */
    public static void main(String[] args) {
        try {
            // As the server, we set up the ServerSocket class.
            ServerSocket servsock = new ServerSocket(7005);
            System.out.println("Listening for connections...");
            while (true) {
                // accept all incoming requests on port 7005
                sock = servsock.accept();
                System.out.println("Connected! Host: " + servsock.getInetAddress().getHostAddress()
                        + " port: " + servsock.getLocalPort());

                // Prep the input stream.
                InputStream in = sock.getInputStream();

                // Listen for GET or SEND packet,
                // then call the appropriate method.
                listen(in);

                // Close the socket with the client after we're done.
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET() is an internal method that, with respect to the client, be sending 
     * the requested file from the client. 
     * 
     * @param fileName - the fileName to be sent to the client.
     */
    static private void GET(String fileName) {
        System.out.println("GET() method called.");
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
    }

    /**
     * SEND() is an internal method that, with respect to the client, be receiving
     * the declared file from the client. 
     * 
     * @param fileName - the fileName to be received from the client.
     */
    static private void SEND(String fileName) {
        System.out.println("SEND() method called.");

        byte[] buf = new byte[BUFFER];
        
        try {
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            while ((count = is.read(buf)) > -1) {
                bos.write(buf, 0, count);
            }
            System.out.println("File Transfer finished.");
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * sendPacket() is an internal method that the application uses to send 
     * messages to the Client based on the messages received from them.
     * 
     * The client has written protocols that will either accept or reject actions
     * based on the validity of the message.
     * 
     * @param str - the Message to be sent to the client
     * @param sout - the Output Stream
     */
    static private void sendPacket(String str, OutputStream sout) {
        byte[] bbuf = str.getBytes();

        try {
            sout.write(bbuf);
            System.out.println("Sending packet: " + str.toString() + " ...");
        } catch (IOException ioe) {
            System.err.println("Error with Packet transmission. Terminating connection.");
            return;
        }
    }

    /**
     * listen() is an internal method that the application uses to listen from the
     * Client. It has a a set of protocols that, upon receiving the expected message
     * from the Client, either retrieves a file from the client or sends to the client
     * the file requested.
     * 
     * @param istr - the Input Stream of the application listening from the Server
     */
    static private void listen(InputStream istr) {
        byte[] buf = new byte[BUFFER];
        int len;
        while (true) {
            try {
                len = istr.read(buf, 0, BUFFER);
            } catch (IOException ioe) {
                System.err.println("Error with Packet transmission. Terminating connection.");
                break;	// probably a socket ABORT; treat as a close
            }

            if (len == -1) {
                break;
            }
            String str = "";
            str = new String(buf, 0, len);
            System.out.println("Received: '" + str + "' from Host.");

            // Our protocol.
            // If str is GET or SEND, GET() or SEND() appropriately.
            // Otherwise, break the loop and terminate the session.
            if (str.toString().equalsIgnoreCase("GET")) {
                try {
                    sendPacket("ACK", sock.getOutputStream());
                    System.out.println("Calling GET() Method.");
                    // Acknowledge and accept the file being requested.
                    // Send it.
                    String file = new String(buf, 0, istr.read(buf, 0, BUFFER));
                    System.out.println(file);

                    Thread.sleep(2000);
                    GET(file);
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
                    // Acknowledge the method. 
                    String file = new String(buf, 0, istr.read(buf, 0, BUFFER));

                    SEND(file);
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
