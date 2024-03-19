package controller;

import bean.Product;
import bean.ProductRepository;
import bean.UserBean;
import authentcation.AuthenticationServer;
import categories.*;
import view.InventoryView;
import view.Validator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryController
{
    private final InventoryView view;

    private volatile List<UserBean> users;

    private UserBean currentUser;

    Socket socket;

    static int count = 0;

    static volatile List<Product> cart;

    ProductRepository productRepository;

    public static String USER_ORDER_HISTORY_FILE = "src/main/java/files/userOrderHistory.txt";

    public InventoryController(List<UserBean> users, InventoryView view, Socket socket, ProductRepository repository)
    {
        this.users = users;

        this.view = view;

        this.socket = socket;

        cart = new ArrayList<>();

        this.productRepository = repository;
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
        if (!isServerRunning())
        {
            view.displayMessage("Server is down. Please try again later.");

            return;
        }

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
                    System.out.println(socket.getInetAddress() + " exited");

                    socket.close();
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
        try
        {
            String firstName = view.getUserInput("Enter first name: ");

            String email = view.getUserInput("Enter email: ");

            String password = view.getUserInput("Enter password: ");

            if (Validator.isValidEmail(email) && Validator.isValidPassword(password))
            {
                Socket socket = new Socket("localhost", 1429); // Connect to the AuthenticationServer

                UserBean newUser = new UserBean(firstName, email, password);

                if (newUser.emailExist(email, users))
                {
                    view.displayMessage("User already exists. Please login");
                }
                else
                {
                    users.add(newUser);

                    AuthenticationServer.addNewUser(email, password);

//                    AuthenticationServer.registeredUsers.put(email, password);

                    UserBean.saveUserData(users);

                    users = UserBean.loadUserData();

                    view.displayMessage("Signup successful!");
                }
            }
            else
            {
                view.displayMessage("Email or Password is wrong");
            }
        }
        catch (IOException e)
        {
            view.displayMessage("Server is down.");
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

            out.println(email);

            out.println(password);

            String authToken = in.readLine();

            System.out.println(authToken);

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

                    view.displayMessage("Login successful! Welcome " + currentUser.getFirstName());

                    if (currentUser.isAdmin())
                    {
                        handleAdminMenu();
                    }
                    else
                    {
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
        users=UserBean.loadUserData();

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

            try
            {
                Socket socket = new Socket("localhost", 1429);

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
                        viewProductsByCategory();
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
                socket.close();
            }
            catch (IOException e)
            {
                view.displayMessage("Server is down.");
            }
        }
    }

    private Product createProductInstance(int category, String productName, int productPrice)
    {
        String productId;

        switch (category)
        {
            case 1:
                productId = "E" + (int) (Math.random() * 10000);
                return new ElectronicsProduct(productId, productName, productPrice);

            case 2:
                productId = "C" + (int) (Math.random() * 10000);
                return new ClothingProduct(productId, productName, productPrice);

            case 3:
                productId = "FH" + (int) (Math.random() * 10000);
                return new HomeDecorAndFurniture(productId, productName, productPrice);

            case 4:
                productId = "B" + (int) (Math.random() * 10000);
                return new Books(productId, productName, productPrice);

            case 5:
                productId = "SFB" + (int) (Math.random() * 10000);
                return new SportsFitnessBagsLuggage(productId, productName, productPrice);

            default:
                System.out.println("Invalid category");
        }
        return null;
    }

    private void addProduct()
    {
        try
        {
            Socket serverCheckObject = new Socket("localhost", 1429);

            int category = view.getUserIntInput("Enter category: \n1. Electronics \n2. Clothing \n3. Furniture and Home Decor" +
                    "\n4. Books \n5. Sports, Fitness, Bags and Luggage");

            if (category == 1 || category == 2 || category == 3 || category == 4 || category == 5)
            {
                String productName = view.getUserInput("Enter product name: ");

                while (productName == "")
                {
                    view.displayMessage("Enter product name please!");

                    productName = view.getUserInput("Enter product name: ");
                }

                int productPrice = view.getUserIntInput("Enter product price: ");

                while (productPrice <= 0)
                {
                    view.displayMessage("Please enter valid price");

                    productPrice = view.getUserIntInput("Enter product price: ");
                }
//        String productId=null;

                Product product = createProductInstance(category, productName, productPrice);

                List<Product> products = productRepository.getProducts(category);

                products.add(product);

                productRepository.saveProducts(category, products);

                view.displayMessage("Product added successfully!");

                serverCheckObject.close();
            }
            else
            {
                view.displayMessage("Invalid Product category");

                addProduct();
            }
        }
        catch (IOException e)
        {
            view.displayMessage("Server is Down");
        }
    }

    private void updateProduct()
    {
        try
        {
            Socket serverCheckObject = new Socket("localhost", 1429);

            int category = view.getUserIntInput("Enter category: \n1. Electronics \n2. Clothing \n3. Furniture and Home Decor" +
                    "\n4. Books \n5. Sports, Fitness, Bags and Luggage");

            String productId = view.getUserInput("Enter product ID to update: ");

            List<Product> products = productRepository.getProducts(category);

            Product product = findProduct(products, productId);

            if (product != null)
            {
                String newName = view.getUserInput("Enter new product name (leave blank to keep current): ");

                if (!newName.isBlank())
                {
                    product.setProductname(newName);
                }

                int newPrice = view.getUserIntInput("Enter new product price (0 to keep current): ");

                if (newPrice != 0)
                {
                    while (newPrice < 0)
                    {
                        view.displayMessage("Price can't be negative");

                        newPrice = view.getUserIntInput("Enter new product price (0 to keep current): ");
                    }

                    product.setProductPrice(newPrice);

                }

                productRepository.saveProducts(category, products);

                view.displayMessage("Product details updated successfully!");
            }
            else
            {
                view.displayMessage("Invalid product ID!");
            }

            serverCheckObject.close();
        }
        catch (IOException e)
        {
            view.displayMessage("Server is down");
        }
    }

    private void removeProduct()
    {
        try
        {
            Socket serverCheckObject = new Socket("localhost", 1429);

            int category = view.getUserIntInput("Enter category: \n1. Electronics \n2. Clothing \n3. Furniture and Home Decor" +
                    "\n4. Books \n5. Sports, Fitness, Bags and Luggage");

            if (category == 1 || category == 2 || category == 3 || category == 4 || category == 5)
            {
                String productId = view.getUserInput("Enter product ID to remove: ");

                List<Product> products = productRepository.getProducts(category);

                Product product = findProduct(products, productId);

                if (product != null)
                {
                    products.remove(product);

                    productRepository.saveProducts(category, products);

                    view.displayMessage("Product removed successfully!");
                }
                else
                {
                    view.displayMessage("Invalid product ID!");
                }

                serverCheckObject.close();
            }
            else
            {
                view.displayMessage("Invalid Product Category");

                removeProduct();
            }
        }
        catch (IOException e)
        {
            view.displayMessage("Server is down");
        }
    }

    private void handleCustomerMenu()
    {
        boolean repeat = true;

        while (repeat)
        {
            view.displayMessage("0 For Logout\n1 For View Category wise Products\n2 For Add To Cart \n3 For Remove from Cart\n4 For View Cart \n5 For Order History");

            int choice = view.getUserIntInput("Enter your choice: ");

            if (socket.isConnected() && socket.isBound())
            {
                try
                {
                    Socket serverCheckObject = new Socket("localhost", 1429);

                    switch (choice)
                    {
                        case 0:
                            view.displayMessage("Thank you.....");
                            repeat = false;
                            break;

                        case 1:
                            viewProductsByCategory();
//                    view.displayProducts(products);
                            break;

                        case 2:
//                        new Socket("localhost", 1429);
                            addToCart();
                            break;

                        case 3:
//                        new Socket("localhost", 1429);
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
                    serverCheckObject.close();
                }
                catch (IOException e)
                {
                    view.displayMessage("Serves is down");
                }
            }
            else
            {
                view.displayMessage("Server is down");
            }
        }
    }

    private void viewProductsByCategory()
    {
        int category = view.getUserIntInput("Enter category: \n1. Electronics \n2. Clothing \n3. Furniture and Home Decor" +
                "\n4. Books \n5. Sports, Fitness, Bags and Luggage");

        if (category == 1 || category == 2 || category == 3 || category == 4 || category == 5)
        {
            List<Product> products = productRepository.getProducts(category);

            view.displayProducts(products);
        }
        else
        {
            view.displayMessage("Invalid Product category");

            viewProductsByCategory();
        }
    }

    private void addToCart()
    {
        try
        {
            int category = view.getUserIntInput("Enter category: \n1. Electronics \n2. Clothing \n3. Furniture and Home Decor" +
                    "\n4. Books \n5. Sports, Fitness, Bags and Luggage");

            if (category == 1 || category == 2 || category == 3 || category == 4 || category == 5)
            {
                String productId = view.getUserInput("Enter product ID: ");

                Socket serverCheckObject = new Socket("localhost", 1429);

                int quantity = getProductQuantity();

                List<Product> products = productRepository.getProducts(category);

                Product product = findProduct(products, productId);

                if (product != null)
                {
                    product.setQuantity(quantity);

                    product.setProductPrice(product.getProductPrice() * quantity);

                    cart.add(product);

                    view.displayMessage("Product added to cart!");
                }
                else
                {
                    view.displayMessage("Invalid product ID!");
                }

                serverCheckObject.close();
            }
            else
            {
                view.displayMessage("Invalid Product category");

                addToCart();
            }
        }
        catch (IOException e)
        {
            view.displayMessage("Server is Down");
        }
    }

    private int getProductQuantity()
    {
        int quantity = view.getUserIntInput("Enter quantity: ");

        if (quantity > 0)
        {
            return quantity;
        }

        else
        {
            System.out.println("Quantity Must be grater than 0!");

            return getProductQuantity();
        }
    }

    private Product findProduct(List<Product> products, String productId)
    {
        for (Product product : products)
        {
            if (product.getProductId().equals(productId))
            {
                return product;
            }
        }
        return null;
    }

    private void removeFromCart()
    {
        String productId = view.getUserInput("Enter productId: ");

        int category = 0;

        try
        {
            Socket serverCheckObject = new Socket("localhost", 1429);

            switch (productId.charAt(0))
            {
                case 'E':
                    category = 1;
                    break;

                case 'C':
                    category = 2;
                    break;

                case 'F':
                    category = 3;
                    break;

                case 'B':
                    category = 4;
                    break;

                case 'S':
                    category = 5;
                    break;
            }

            List<Product> products = productRepository.getProducts(category);

            Product product = findProduct(products, productId);

            if (product != null)
            {
                if (cart.removeIf(currentProduct -> currentProduct.getProductId().equals(productId)))
                {
                    cart.remove(product);

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

            socket.close();
        }
        catch (IOException e)
        {
            view.displayMessage("Server is down");
        }
    }

    private void viewCart()
    {
        int price = 0;

        float cgst = 0.125f;

        float sgst = 0.125f;

        double grand_total = 0;

        view.displayMessage("Cart Items:");

        List<Product> orderedProducts = new ArrayList<>();

        for (Product product : cart)
        {
            view.displayMessage(product.getProductId() + "\t" + product.getProductName() + "\t" + product.getQuantity() + "\t" + (product.getProductPrice() * product.getQuantity()));

            price += (product.getProductPrice() * product.getQuantity());

            orderedProducts.add(product);
        }

        if (price > 0)
        {

            view.displayMessage("Total bill: " + price);

            view.displayMessage("CGST(12.5%): " + (price * cgst));

            view.displayMessage("SGST(12.5%): " + (price * sgst));

            grand_total = price + (price * cgst) + (price * sgst);

            view.displayMessage("Grand Total: " + grand_total);

            try
            {
                Socket serverCheckObject = new Socket("localhost", 1429);

                if (price > 0)
                {
                    boolean wantToCheckout = view.getUserBooleanInput("Do you want to checkout? (y/n): ");

                    if (wantToCheckout)
                    {
                        currentUser.saveOrderHistory(orderedProducts);

                        cart.clear();

                        view.displayMessage("Order placed successfully!");
                    }
                }

                serverCheckObject.close();
            }
            catch (IOException e)
            {
                view.displayMessage("Server is down");
            }
        }
        else
        {
            view.displayMessage("Cart is empty");
        }
    }

    private void adminViewUserOrderHistory()
    {
        try
        {
            File file = new File(USER_ORDER_HISTORY_FILE);

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
            File file = new File(USER_ORDER_HISTORY_FILE);

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

                if (orderHistoryFound == false)
                {
                    view.displayMessage("No order history found for user " + currentUser.getFirstName());
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

    private boolean isServerRunning()
    {
        return socket.isConnected() && !socket.isClosed();
    }
}