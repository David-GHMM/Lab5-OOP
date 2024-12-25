import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    private static final int PORT = 12345;
    private static final Map<String, PrintWriter> onlineClients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Сервер запущен на порту " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Получаем имя клиента
                clientName = in.readLine();
                System.out.println("Новый клиент подключился: " + clientName);
                
                synchronized (onlineClients) {
                    onlineClients.put(clientName, out);
                }
                
                // Оповещаем всех о новом клиенте
                broadcastMessage("СИСТЕМА: " + clientName + " присоединился к чату");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientName + ": " + message);
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Клиент отключился: " + clientName);
            } finally {
                if (clientName != null) {
                    synchronized (onlineClients) {
                        onlineClients.remove(clientName);
                    }
                    broadcastMessage("СИСТЕМА: " + clientName + " покинул чат");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (onlineClients) {
                for (PrintWriter writer: onlineClients.values()) {
                    if (onlineClients.get(clientName) != writer)
                    {
                        writer.println(message);
                    }
                }
            }
        }
    }
}
