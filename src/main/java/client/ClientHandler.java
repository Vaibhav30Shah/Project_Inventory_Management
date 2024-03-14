package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import bean.ProductBean;
import bean.ProductRepository;
import bean.UserBean;
import client.InventoryClient;

public class ClientHandler
{
    static List<ProductBean> products;

    private ProductRepository productRepository;

    List<UserBean> users;

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

//            List<ProductBean> products = ProductBean.loadProductData();

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