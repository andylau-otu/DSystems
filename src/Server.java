import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    public static int threadsRunning = 0;

    public static void main(String argv[]) throws Exception {
        ServerSocket hi = new ServerSocket(3500);

        while (!hi.isClosed()) {
            Socket client = hi.accept();

//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

            System.out.println("Server Listening on port 3500....");

            ClientHandler clientHandler = new ClientHandler(client);
            Thread t = new Thread(clientHandler);
            t.start();
//                bw.write("HI\n");
//                System.out.println("Hello Client");
//                bw.flush();


        }


    }

}
