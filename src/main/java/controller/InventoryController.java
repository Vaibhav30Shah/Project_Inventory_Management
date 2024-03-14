package controller;

import authentcation.AuthenticationHandler;
import bean.ProductBean;
import bean.UserBean;
import authentcation.AuthenticationServer;
import view.InventoryView;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InventoryController {
    private InventoryView view;
    private Scanner scanner = new Scanner(System.in);
    private volatile List<UserBean> users;
    private volatile List<ProductBean> products;
    private UserBean currentUser;
    Socket socket;
    static int count = 0;

    public InventoryController(List<ProductBean> products, List<UserBean> users, InventoryView view, Socket socket)
    {
        this.products = products;

        this.users = users;

        this.view = view;

        this.socket = socket;
    }


    public void start()
    {
        int choice = -1;

        while (choice != 3)
        {
            view.displayMenu();

            choice = view.getUserIntInput("Enter your choice: ");

            handleUserChoice(choice);
        }
    }

    private void handleUserChoice(int choice)
    {
        switch (choice)
        {
            case 1:
                signup();
                break;

            case 2:
                login();
                break;

            case 3:
                view.displayMessage("Exiting...");

                try
                {
                    socket.close();

                    System.out.println(socket.getInetAddress() + " exited");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            default:
                view.displayMessage("Invalid choice");
        }
    }


    private void signup()
    {
        String firstName = view.getUserInput("Enter first name: ");

        String email = view.getUserInput("Enter email: ");

        String password = view.getUserInput("Enter password: ");

        UserBean newUser = new UserBean(firstName, email, password);

        if (newUser.emailExist(email, users))
        {
            view.displayMessage("User already exists. Please login");

            return;
        }
        else
        {
            users.add(newUser);

            AuthenticationServer.addNewUser(email, password);

            UserBean.saveUserData(users);

            users = UserBean.loadUserData();

            AuthenticationServer server=new AuthenticationServer();

            server.initializeRegisteredUsers(users);

            view.displayMessage("Signup successful!");
        }
    }

    private void login()
    {
        String email = view.getUserInput("Enter email: ");

        String password = view.getUserInput("Enter password: ");

        try
        {
            Socket socket = new Socket("localhost", 1429); // Connect to the AuthenticationServer

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            AuthenticationServer server=new AuthenticationServer();

            server.initializeRegisteredUsers(users);

            out.println(email);

            out.println(password);

            String authToken = in.readLine();

            if (authToken.equals("Invalid credentials"))
            {
                count++;

                view.displayMessage("Invalid credentials!");
            }
            else
            {
                currentUser = findUser(email, password);

                if (currentUser != null)
                {
                    count = 0;

                    view.displayMessage("Login successful!");

                    if (currentUser.isAdmin())
                    {
                        handleAdminMenu();
                    }
                    else {
                        handleCustomerMenu();
                    }
                }
                else
                {
                    count++;

                    view.displayMessage("Invalid credentials!");
                }
            }

            if (count > 3)
            {
                view.displayMessage("You exceeded the login limits. Try Again later.");

                socket.close();
            }

            socket.close();
        }
        catch (IOException e)
        {
            System.out.println("Sorry we cant log you in as server might be down.");
        }
    }

    private UserBean findUser(String email, String password)
    {
        for (UserBean user : users)
        {
            if (user.getEmail().equals(email))
            {
                if (user.getPassword().equals(password))
                {
                    return user;
                }
                else if (user.isAdmin() && password.equals("admin"))
                {
                    // Handle the case where the admin user's password is "admin"
                    return user;
                }
            }
        }
        return null;
    }

    private void handleAdminMenu()
    {
        boolean repeat = true;

        while (repeat)
        {
            view.displayMessage("\n0 For Logout\n1 For Add Product\n2 For List Products" +
                    "\n3 For List Users \n4 For Update Product Details \n5 For Viewing Order History" +
                    "\n6 For Removing Product");

            int choice = view.getUserIntInput("Enter your choice: ");

            switch (choice)
            {
                case 0:
                    view.displayMessage("Thank you for visiting us...");
                    repeat = false;
                    break;

                case 1:
                    addProduct();
                    break;

                case 2:
                    view.displayProducts(products);
                    break;

                case 3:
                    view.displayUsers(users);
                    break;

                case 4:
                    updateProduct();
                    break;

                case 5:
                    adminViewUserOrderHistory();
                    break;

                case 6:
                    removeProduct();
                    break;

                default:
                    view.displayMessage("Invalid choice");
            }
        }
    }

    private void addProduct()
    {
        String productName = view.getUserInput("Enter product name: ");

        int productPrice = view.getUserIntInput("Enter product price: ");

        ProductBean product = new ProductBean(productPrice, productName);

        products.add(product);

//        ProductBean.saveProductData(products);

        view.displayMessage("Product added successfully!");
    }

    private void updateProduct()
    {
        int productId = view.getUserIntInput("Enter product ID to update: ");

        ProductBean product = findProduct(productId);

        if (product != null)
        {
            String newName = view.getUserInput("Enter new product name (leave blank to keep current): ");

            if (!newName.isBlank())
            {
                product.setProductName(newName);
            }

            int newPrice = view.getUserIntInput("Enter new product price (0 to keep current): ");

            if (newPrice != 0)
            {
                product.setPrice(newPrice);
            }

//            ProductBean.saveProductData(products);

            view.displayMessage("Product details updated successfully!");
        }
        else
        {
            view.displayMessage("Invalid product ID!");
        }
    }

    private void removeProduct()
    {
        int productId = view.getUserIntInput("Enter product ID to remove: ");

        ProductBean product = findProduct(productId);

        if (product != null)
        {
            products.remove(product);

//            ProductBean.saveProductData(products);

            view.displayMessage("Product removed successfully!");
        }
        else
        {
            view.displayMessage("Invalid product ID!");
        }
    }

    private void handleCustomerMenu()
    {
        boolean repeat = true;

        while (repeat)
        {
            view.displayMessage("0 For Logout\n1 For View Products\n2 For Add To Cart \n3 For Remove from Cart\n4 For View Cart \n5 For Order History");

            int choice = view.getUserIntInput("Enter your choice: ");

            switch (choice)
            {
                case 0:
                    view.displayMessage("Thank you.....");
                    repeat = false;
                    break;

                case 1:
                    view.displayProducts(products);
                    break;

                case 2:
                    addToCart();
                    break;

                case 3:
                    removeFromCart();
                    break;

                case 4:
                    viewCart();
                    break;

                case 5:
                    viewUserOrderHistory();
                    break;

                default:
                    view.displayMessage("Invalid choice");
            }
        }
    }

    private void addToCart()
    {
        int productId = view.getUserIntInput("Enter productId for cart: ");

        ProductBean product = findProduct(productId);

        if (product != null)
        {
            currentUser.cart.add(product);

            view.displayMessage("Product added to cart!");
        }
        else
        {
            view.displayMessage("Invalid product ID!");
        }
    }

    private void removeFromCart()
    {
        int productId = view.getUserIntInput("Enter productId for cart: ");

        ProductBean product = findProduct(productId);

        if (product != null)
        {
            if (currentUser.cart.removeIf(currentProduct -> currentProduct.getProductId() == productId))
            {
                currentUser.cart.remove(product);

                view.displayMessage("Product removed from cart!");
            }
            else
            {
                view.displayMessage("Product not added in Cart!");
            }
        }
        else
        {
            view.displayMessage("Invalid product ID!");
        }
    }

    private ProductBean findProduct(int productId)
    {
        for (ProductBean product : products)
        {
            if (product.getProductId() == productId)
            {
                return product;
            }
        }
        return null;
    }

    private void viewCart()
    {
        int price = 0;

        view.displayMessage("Cart Items:");

        List<ProductBean> orderedProducts = new ArrayList<>();

        for (ProductBean product : currentUser.cart)
        {
            view.displayMessage(product.getProductId() + "\t" + product.getProductName() + "\t" + product.getPrice());

            price += product.getPrice();

            orderedProducts.add(product);
        }

        view.displayMessage("Total bill: " + price);

        if (price > 0)
        {
            boolean wantToCheckout = view.getUserBooleanInput("Do you want to checkout? (y/n): ");

            if (wantToCheckout)
            {
                currentUser.saveOrderHistory(orderedProducts);

                currentUser.cart.clear();

                view.displayMessage("Order placed successfully!");
            }
        }
    }

    private void adminViewUserOrderHistory()
    {
        try
        {
            File file = new File("userOrderHistory.txt");

            if (file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    String[] orderData = line.split(",");

                    String email = orderData[0];

                    List<String> orderedProducts = Arrays.asList(orderData).subList(1, orderData.length);

                    view.displayMessage("User: " + email);

                    view.displayMessage("Ordered Products: " + String.join(", ", orderedProducts));

                    view.displayMessage("-----------------------");
                }
                reader.close();
            }
            else
            {
                view.displayMessage("No order history found.");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void viewUserOrderHistory()
    {
        try
        {
            File file = new File("userOrderHistory.txt");

            if (file.exists())
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;

                boolean orderHistoryFound = false;

                while ((line = reader.readLine()) != null)
                {

                    String[] orderData = line.split(",");

                    String email = orderData[0];

                    if (email.equals(currentUser.getEmail()))
                    {
                        orderHistoryFound = true;

                        List<String> orderedProducts = Arrays.asList(orderData).subList(1, orderData.length);

                        view.displayMessage("Ordered Products: " + String.join(", ", orderedProducts));
                    }
                }

                reader.close();

                if (orderHistoryFound==false)
                {
                    view.displayMessage("No order history found for user "+currentUser.getFirstName());
                }
            }
            else
            {
                view.displayMessage("No order history found.");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}