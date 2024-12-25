import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ваше имя: ");
        String name = scanner.nextLine();

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Подключено к серверу");

            // Поток для отправки сообщений серверу
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Поток для получения сообщений от сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправляем имя клиента
            out.println(name);

            // Создаем отдельный поток для получения сообщений от сервера
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Соединение с сервером потеряно");
                }
            });
            receiveThread.start();

            // Основной поток для отправки сообщений
            String message;
            while (true) {
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
                out.println(message);
            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Ошибка подключения к серверу");
            e.printStackTrace();
        }
    }
} 