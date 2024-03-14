package server;

import authentcation.AuthenticationServer;
import bean.ProductBean;
import bean.ProductRepository;
import bean.UserBean;
import client.ClientHandler;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryServer
{
    private static final int PORT = 1428;

    private static List<ProductBean> products = new ArrayList<>();

    private static List<UserBean> users = new ArrayList<>();

    private static ProductRepository productRepository;

    private static volatile boolean shuttingDown = false;


    public static void main(String[] args)
    {
        users = UserBean.loadUserData();
        products = ProductBean.loadProductData();

        if (users.isEmpty() || products.isEmpty())
        {
            initializeDummyData();
        }

        UserBean.saveUserData(users);

//        productRepository = new ProductRepository();
        ProductBean.saveProductData(products);

        AuthenticationServer authServer = new AuthenticationServer();

        Thread authThread = new Thread(authServer);

        authThread.start();

//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            if (!shuttingDown) {
//                System.out.println("Server is writing");
//                saveDataBeforeShutdown();
//            }
//            shuttingDown = true;
//        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Inventory Server started on port " + PORT);

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostName());

                ClientHandler clientHandler = new ClientHandler(products, users);

                // new thread to handle the client
//                Thread clientThread = new Thread(new InventoryClient(clientSocket, products, users));
//                clientThread.start();
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
        products.add(new ProductBean(100000, "iPhone"));

        products.add(new ProductBean(55000, "LED TV"));

        products.add(new ProductBean(5000, "Monitor"));

        products.add(new ProductBean(400, "Mouse"));

        products.add(new ProductBean(1000, "Keyboard"));

        products.add(new ProductBean(100, "USB Cable"));

        products.add(new ProductBean(17000, "Airpods"));

        products.add(new ProductBean(1000, "Speaker"));

        products.add(new ProductBean(60000, "HP Laptop"));

        products.add(new ProductBean(4500, "Graphics Card"));
    }

    private static void saveDataBeforeShutdown()
    {
        UserBean.saveUserData(users);

        ProductBean.saveProductData(products);
    }

    private static void saveUserData()
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UserBean.FILE_NAME)))
        {
            for (UserBean user : users)
            {
                writer.write(user.getFirstName() + "," + user.getEmail() + "," + user.getPassword() + "," + user.isAdmin() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void saveProductData()
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ProductBean.FILE_NAME)))
        {
            for (ProductBean product : products)
            {
                writer.write(product.getProductId() + "," + product.getProductName() + "," + product.getPrice() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}