package authentcation;

import bean.UserBean;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationServer implements Runnable
{
    private static final int PORT = 1429;

    private static Map<String, String> registeredUsers = new HashMap<>();

    public AuthenticationServer()
    {
        List<UserBean> users=UserBean.loadUserData();
        initializeRegisteredUsers(users);
    }

    public static Map initializeRegisteredUsers(List<UserBean> users)
    {
        for (UserBean user : users)
        {
            registeredUsers.put(user.getEmail(), user.getPassword());
        }
        return registeredUsers;
    }

    public static void addNewUser(String email, String password)
    {
        registeredUsers.put(email, password);
    }

    @Override
    public void run()
    {
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Authentication Server started on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected for authentication: " + clientSocket.getInetAddress().getHostAddress());

                System.out.println("Inside AS run "+registeredUsers);

                // Create a new thread to handle the authentication request
                Thread authHandler = new Thread(new AuthenticationHandler(clientSocket, AuthenticationServer.initializeRegisteredUsers(UserBean.loadUserData())));

                authHandler.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}