package es6_yang;

import java.io.*;
import java.net.*;

public class Server {
    public  void avvioServer(){
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8000);
            System.out.println("Server in attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientHandlerThread = new Thread(new ClientHandler(clientSocket));
                clientHandlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void  sendFile(DataOutputStream outputStream, String status, File file) throws IOException{

        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(fileData);
        }
        // Invia la risposta HTTP con lo status, gli headers e il corpo del messaggio
        String response = "HTTP/1.1 " + status + "\r\n" +
                          "Content-Type: " + "text/html" + "\r\n" +
                          "Content-Length: " + file.length() + "\r\n" +
                          "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(fileData);

    }
  
    @Override
    public void run(){

        try (
            BufferedReader inDaClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outVersoClient = new DataOutputStream(clientSocket.getOutputStream());
        ) {

            try {
                File file = new File("es6_yang\\index.html");
                File file_notFound = new File("es6_yang\\404.html");

                if (file.exists() && !file.isDirectory())sendFile(outVersoClient,"200 OK",file);

                else sendFile(outVersoClient, "404 Not Found", file_notFound);

            } catch (Exception e) {
                // TODO: handle exception
            }
                
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally { //se viene chiuso il socket del client
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}