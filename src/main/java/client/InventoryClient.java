package client;

import bean.ProductRepository;
import controller.InventoryController;
import bean.UserBean;
import view.InventoryView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class InventoryClient implements Runnable
{
    private static Socket clientSocket;

    private static InventoryController controller;

    volatile List<UserBean> users;

    private boolean isServerRunning = true;

    InventoryView view;

    public InventoryClient(Socket socket, List<UserBean> users)
    {
        this.clientSocket = socket;

        this.users = users;
    }

    @Override
    public void run()
    {
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            view = new InventoryView();

            ProductRepository repository=new ProductRepository();

            controller = new InventoryController(users, view, clientSocket, repository);

            controller.start();

            clientSocket.close();
        }
        catch (IOException e)
        {
            isServerRunning = false;

            view.displayMessage("Server is down. Please try again later.");

            e.printStackTrace();
        }
    }
}