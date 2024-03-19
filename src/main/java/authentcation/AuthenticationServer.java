package authentcation;

import bean.UserBean;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class AuthenticationServer
{
    private static final int PORT = 1429;
    public  static List<UserBean> users;
    public static Map<String, String> registeredUsers = new ConcurrentHashMap<>();

    public static void addNewUser(String email, String password)
    {
        registeredUsers.put(email, password);

        System.out.println(registeredUsers.get(email));
    }

    public static void main(String[] args)
    {
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Authentication Server started on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected for authentication: " + clientSocket.getInetAddress().getHostAddress());

                System.out.println("Inside AS run "+ registeredUsers);

                // Create a new thread to handle the authentication request
                users=UserBean.loadUserData();

                for(UserBean user : users)
                {
                    AuthenticationServer.addNewUser(user.getEmail(), user.getPassword());
                }

                Thread authHandler = new Thread(new AuthenticationHandler(clientSocket));

                authHandler.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}