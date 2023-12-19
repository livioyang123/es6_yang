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
    private String getContentType(File file) {
        String fileName = file.getName();
        String contentType = "text/html";

        if (fileName.endsWith(".css")) {
            contentType = "text/css";
        } else if (fileName.endsWith(".js")) {
            contentType = "application/javascript";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        }

        return contentType;
    }
    public void sendFile(DataOutputStream outputStream, String status, File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(fileData);
        }

        String contentType = getContentType(file);

        // Invia la risposta HTTP con lo status, gli headers e il corpo del messaggio
        String response = "HTTP/1.1 " + status + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "\r\n";
        outputStream.write(response.getBytes());
        outputStream.write(fileData);
    }
  
    @Override
    public void run() {
        try (
            BufferedReader inDaClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream outVersoClient = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            File file = new File("es6_yang\\index.html");
            File file_notFound = new File("es6_yang\\404.html");

            boolean html_inviato = false;

            if (file.exists() && !file.isDirectory() && !html_inviato) {
                sendFile(outVersoClient, "200 OK", file);
                System.out.println("index.html inviato");
                html_inviato = true;


                do {
                String msg = inDaClient.readLine();

                if (msg != null) {
                    if (msg.contains("GET") && msg.contains("indexStyle.css")) {
                        sendFile(outVersoClient, "200 OK", new File("es6_yang\\indexStyle.css"));
                        System.out.println(msg);
                        System.out.println("css inviato");
                        break;  
                    } else if (msg.contains("GET") && msg.contains("js.js")) {
                        sendFile(outVersoClient, "200 OK", new File("es6_yang\\js.js"));
                        System.out.println(msg);
                        System.out.println("js inviato");
                        break;  
                    }
                }
            } while (html_inviato);


            } else {
                sendFile(outVersoClient, "404 Not Found", file_notFound);
            }

            

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
