import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> threads = new ArrayList<>();
    Socket client;
    public BufferedReader br;
    public BufferedWriter bw;
    public String userName;
    public static ArrayList<String> users = new ArrayList<>();

    public ClientHandler (Socket client) {
        try {
            this.client = client;
            bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.userName = br.readLine();
            broadcastAll(userName, " has joined the chat");

        } catch (Exception e) {
            closeClient(client,br,bw);
        }

    }

    public void closeClient(Socket socket, BufferedReader br, BufferedWriter bw) {
        threads.remove(this);
        broadcastAll(userName, ": has left the chat");
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
            closeClient(client,br,bw);

        }
    }

    public void broadcastAll(String clientName, String msg) {
        for (ClientHandler mThreads : threads) {
            try {
                if (msg.equals(": /shrug")){
                    if (!mThreads.userName.equals(userName)) {
                        mThreads.bw.write(clientName + ": |_(o_o)_|");
                        mThreads.bw.newLine();
                        mThreads.bw.flush();
                    }
                }

                else if (msg.startsWith(": /list")) {
                    if (mThreads.userName.equals(userName)) {
                        mThreads.bw.write("These are the users online");
                        mThreads.bw.newLine();
                        mThreads.bw.flush();
                        for (int i =0; i < users.size(); i++) {

                            mThreads.bw.write(users.get(i));
                            mThreads.bw.newLine();
                            mThreads.bw.flush();

                        }
                    }


//                    mThreads.bw.write("Tell me who to duel");
//                    mThreads.bw.newLine();
//                    mThreads.bw.flush();
//                    String opponent = "";
//                    opponent = br.readLine();
//                    System.out.println(opponent);
//                    for (ClientHandler people : threads) {
//                        if(people.userName.equals(opponent)) {
//                            System.out.println(people.userName + " " + opponent);
//                            people.bw.write(userName + " has challenged you to a duel! \nDo you accept? y/n");
//                            mThreads.bw.newLine();
//                            mThreads.bw.flush();
//                            if (people.br.readLine().equals('y')) {
//                                people.bw.write(people + " has accepted your duel!");
//                                mThreads.bw.newLine();
//                                mThreads.bw.flush();
//                            }
//                        }
//                    }


                }
                else if (msg.startsWith(": /") && mThreads.userName.equals(userName)) {
                    mThreads.bw.write("invalid command");
                    mThreads.bw.newLine();
                    mThreads.bw.flush();

                }
                else if (!mThreads.userName.equals(userName)){
                    mThreads.bw.write(clientName + msg);
                    System.out.println("Sending to all clients");
                    mThreads.bw.newLine();
                    mThreads.bw.flush();
                }
                int cNum = threads.indexOf(mThreads);
                System.out.println(cNum);
            } catch (IOException e) {

            }
        }
    }

//    public void listenMsg(String msg) {
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//
//                String d;
//                while (client.isConnected()) {
//                    try {
//
//                        d = br.readLine();
//                        for (int i =0; i < users.size(); i++)
//                            if (msg.startsWith(": /duel") && msg.contains(users.get(i))){
//
//
//                            }
//                        System.out.println(d);
//                        System.out.flush();
////                        if (br.ready()== true) {
////                            groupMsg = br.readLine();
////                            System.err.println(groupMsg);
////                        }
//
//                    } catch (Exception e) {
//                        System.err.println("getting no data");
//                        closeClient(socket, br, bw);
//                        break;
//                    }
//                }
//
//            }
//        }).start();
//
//    }

    @Override
    public void run() {
        try {
            threads.add(this);
            System.out.println("Start thread for " + userName + ", there are "+ threads.size() + " threads running");
            System.out.println("IP: " + client.getInetAddress().getHostAddress());
            System.out.println("Port: " + client.getPort());
            users.add(userName);


            while (client.isConnected()) {
                try {
                    String clientName = br.readLine();
                    String cMsg = br.readLine();

                    System.out.println("Client said: " + cMsg);

                    broadcastAll(clientName, ": "+cMsg);
//                    broadcastAll(clientName, ": /shrug");

                } catch (IOException e ) {
                    System.out.println("Problem Sending to all clients");
                    closeClient(client,br,bw);
                    break;

                }
            }

        } catch (Exception e) {
            closeClient(client,br,bw);
            System.out.println(e);
        } finally {
            try {
//                client.close();
//                threadsRunning--;
                System.out.println("Thread closed");

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}