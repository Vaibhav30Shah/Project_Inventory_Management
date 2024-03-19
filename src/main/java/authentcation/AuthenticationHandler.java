package authentcation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AuthenticationHandler implements Runnable
{
    private final Socket clientSocket;


    public AuthenticationHandler(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String email = in.readLine();

            String password = in.readLine();

            System.out.println("Server is trying to validate your credentials. Please Wait!!");

            if (authenticate(email, password) )
            {
                String authToken = generateAuthToken();

                out.println(authToken);
            }
            else
            {
                out.println("Invalid credentials");
            }

            clientSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean authenticate(String email, String password)
    {
        if (!AuthenticationServer.registeredUsers.containsKey(email))
        {
            return false;
        }

        String registeredPassword = AuthenticationServer.registeredUsers.get(email);

        return registeredPassword != null && registeredPassword.equals(password);
    }

    private String generateAuthToken()
    {
        System.out.println("User is trying to login");

        return "Logged in";
    }
}
