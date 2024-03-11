package authentcation;

import bean.UserBean;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationServer implements Runnable{
    private static final int PORT = 1429;
    private static Map<String, String> registeredUsers = new HashMap<>();

    public AuthenticationServer(List<UserBean> users) {
        initializeRegisteredUsers(users);
    }

    private void initializeRegisteredUsers(List<UserBean> users) {
        for (UserBean user : users) {
            registeredUsers.put(user.getEmail(), user.getPassword());
        }
    }

    public static void addNewUser(String email, String password) {
        registeredUsers.put(email, password);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Authentication Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new thread to handle the authentication request
                Thread authHandler = new Thread(new AuthenticationHandler(clientSocket, registeredUsers));
                authHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AuthenticationServer(String email, String password) {
        registeredUsers.put(email,password);
    }

    public static boolean authenticate(String email, String password) {
        String registeredPassword = registeredUsers.get(email);
        return registeredPassword != null && registeredPassword.equals(password);
    }
}