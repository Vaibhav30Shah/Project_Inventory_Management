package server;

import authentcation.AuthenticationServer;
import bean.ProductRepository;
import bean.UserBean;
import client.ClientHandler;
import controller.InventoryController;
import view.InventoryView;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryServer
{
    private static final int PORT = 1428;

    public static volatile List<UserBean> users = new ArrayList<>();

    private static volatile boolean shuttingDown = false;

    static InventoryController controller;


    public static void main(String[] args)
    {
//        ProductRepository productRepository = new ProductRepository();
//
//        InventoryView view = new InventoryView();
//
        users = UserBean.loadUserData();
//
//        if (users.isEmpty() || products.isEmpty())
//        {
//            initializeDummyData();
//        }
//
//        UserBean.saveUserData(users);
//
////        productRepository = new ProductRepository();
//        ProductBean.saveProductData(products);

//        AuthenticationServer authServer = new AuthenticationServer();
//
//        Thread authThread = new Thread(authServer);
//
//        authThread.start();

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Inventory Server started on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostName());

                ClientHandler clientHandler = new ClientHandler(InventoryServer.users);

                InventoryView view=new InventoryView();

                ProductRepository repository=new ProductRepository();

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (!shuttingDown) {
                        try
                        {
                            clientSocket.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    shuttingDown = true;
                }));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void initializeDummyData()
    {
        // Add dummy users
        users.add(new UserBean("Vaibhav", "vaibhav@gmail.com", "vaibhav123"));

        users.add(new UserBean("Ram", "ram@gmail.com", "ram123"));

        users.add(new UserBean("Shyam", "shyam@gmail.com", "shyam123"));

        users.add(new UserBean("Ravan", "ravan@gmail.com", "ravan123"));

        UserBean admin = new UserBean("admin", "admin@admin.com", "admin");

        admin.setAdmin(true);

        users.add(admin);


        // Add dummy products
//        products.add(new ProductBean(100000, "iPhone"));
//
//        products.add(new ProductBean(55000, "LED TV"));
//
//        products.add(new ProductBean(5000, "Monitor"));
//
//        products.add(new ProductBean(400, "Mouse"));
//
//        products.add(new ProductBean(1000, "Keyboard"));
//
//        products.add(new ProductBean(100, "USB Cable"));
//
//        products.add(new ProductBean(17000, "Airpods"));
//
//        products.add(new ProductBean(1000, "Speaker"));
//
//        products.add(new ProductBean(60000, "HP Laptop"));
//
//        products.add(new ProductBean(4500, "Graphics Card"));
    }


}