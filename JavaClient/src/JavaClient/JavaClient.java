/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaClient;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Trankalinos
 */
public class JavaClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Socket sock = new Socket("127.0.0.1", 7005);
            byte[] mybytearray = new byte[2048];
            InputStream is = sock.getInputStream();
            FileOutputStream fos = new FileOutputStream("Flight.docx");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            int count;
            // int bytesRead = is.read(mybytearray, 0, mybytearray.length);
            
            while ((count = is.read(mybytearray)) >= 0) {
                bos.write(mybytearray, 0, count);
            }
            
            bos.close();
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
