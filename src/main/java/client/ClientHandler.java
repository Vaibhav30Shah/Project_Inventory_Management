package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import bean.ProductBean;
import bean.UserBean;
import server.InventoryServer;

public class ClientHandler
{
    static volatile List<ProductBean> products;

    volatile List<UserBean> users;

    public ClientHandler(List<ProductBean> products, List<UserBean> users)
    {
        this.users = users;

        this.products = products;
    }

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("127.0.0.1", 1428);

            List<UserBean> users = UserBean.loadUserData();

            List<ProductBean> products = ProductBean.loadProductData();

            InventoryClient client = new InventoryClient(socket, products, users);

            Thread clientThread = new Thread(client);

            clientThread.start();
        }
        catch (IOException e)
        {
            System.out.println("Server is Closed.");
        }
    }
}