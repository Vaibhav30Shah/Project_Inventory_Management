package client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import bean.UserBean;

public class ClientHandler
{
    volatile List<UserBean> users;

    public ClientHandler(List<UserBean> users)
    {
        this.users = users;
    }

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("10.20.40.229", 1428);

            Socket authSocket = new Socket("10.20.40.229", 1429);

            List<UserBean> users = UserBean.loadUserData();

            InventoryClient client = new InventoryClient(socket, users);

            Thread clientThread = new Thread(client);

            clientThread.start();
        }
        catch (IOException e)
        {
            System.out.println("Server is Closed.");
        }
    }
}