
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client {
    public Socket socket;
    public BufferedReader br;
    public BufferedWriter bw;
    public String userName;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.userName = userName;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // int x = 7, y = 5;
        } catch (IOException e) {
            closeClient(socket, br, bw);
        }

    }

    public static void main(String[] argv) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Username");
        String userName = scanner.next();
        Socket echo = new Socket("localhost", 3500);
        Client client = new Client(echo, userName);
        //client.status();
        client.listenMsg();
        client.sendMsg();

    }

    public void listenMsg() {
        new Thread(new Runnable(){
            @Override
            public void run() {

                String groupMsg;
                while (socket.isConnected()) {
                    try {

                        groupMsg = br.readLine();
                        System.out.println(groupMsg);
                        System.out.flush();
//                        if (br.ready()== true) {
//                            groupMsg = br.readLine();
//                            System.err.println(groupMsg);
//                        }

                    } catch (Exception e) {
                        System.err.println("getting no data");
                        closeClient(socket, br, bw);
                        break;
                    }
                }

            }
        }).start();

    }

    public void sendMsg() {
        Scanner scanner = new Scanner(System.in);
        try {
            bw.write(userName);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            closeClient(socket, br, bw);
        }
        while( socket.isConnected()) {
            try {
                String msg = scanner.nextLine();
                bw.write(userName);
                bw.newLine();
                bw.flush();
                bw.write(msg);
                bw.newLine();
                bw.flush();

//                String sReply = br.readLine();
//                System.out.println(sReply);
            } catch (IOException e) {
                scanner.close();
                closeClient(socket, br, bw);
                break;

            }

        }
    }

    public void status() {

        try {
            String state = br.readLine();

            if (state.equals("Server is busy")) {
                System.out.println("Server is busy");
                socket.close();
            } else {
                System.out.println("Connected to server...");
            }

        } catch (Exception e) {
            closeClient(socket, br, bw);

        }

    }

    public void closeClient(Socket socket, BufferedReader br, BufferedWriter bw) {
        try {
            if (br != null){
                br.close();
                System.out.println("Reader terminated ");
            }
            if (bw != null){
                bw.close();
                System.out.println("Writer terminated ");
            }
            if (socket != null) {
                socket.close();
                System.out.println("Socket terminated ");
            }

        } catch (IOException e)  {
            closeClient(socket, br, bw);
        }


    }
}

