package controller;


import bean.ProductBean;
import bean.UserBean;
import authentcation.AuthenticationServer;
import view.InventoryView;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class InventoryController {
    private InventoryView view;
    private List<UserBean> users;
    private List<ProductBean> products;
    private UserBean currentUser;

    public InventoryController(List<ProductBean> products, List<UserBean> users, InventoryView view) {
        this.products = products;
        this.users = users;
        this.view = view;
    }


    public void start() {
        int choice = -1;
        while (choice != 3) {
            view.displayMenu();
            choice = view.getUserIntInput("Enter your choice: ");
            handleUserChoice(choice);
        }
    }

    private void handleUserChoice(int choice) {
        switch (choice) {
            case 0:
                displayUsers();
                break;
            case 1:
                signup();
                break;
            case 2:
                login();
                break;
            case 3:
                view.displayMessage("Exiting...");
                break;
            default:
                view.displayMessage("Invalid choice");
        }
    }

    private void displayUsers() {
        view.displayUsers(users);
    }

    private void signup() {
        String firstName = view.getUserInput("Enter first name: ");
        String email = view.getUserInput("Enter email: ");
        String password = view.getUserInput("Enter password: ");
        UserBean newUser = new UserBean(firstName, email, password);
        users.add(newUser);
        AuthenticationServer.addNewUser(email, password);
        try {
            BufferedWriter userWrite=new BufferedWriter(new FileWriter("userData.txt"));
            userWrite.write(firstName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        view.displayMessage("Signup successful!");
    }

    private void login() {
        String email = view.getUserInput("Enter email: ");
        String password = view.getUserInput("Enter password: ");

        try {
            Socket socket = new Socket("localhost", 1429); // Connect to the AuthenticationServer
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(email);
            out.println(password);

            String authToken = in.readLine();

            if (authToken.equals("Invalid credentials")) {
                view.displayMessage("Invalid credentials!");
            } else {
                // User is authenticated, proceed with user-specific operations
                currentUser = findUser(email, password);
                view.displayMessage("Login successful!");
                if (currentUser.isAdmin()) {
                    handleAdminMenu();
                } else {
                    handleCustomerMenu();
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserBean findUser(String email, String password) {
        for (UserBean user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void handleAdminMenu() {
        boolean repeat = true;
        while (repeat) {
            view.displayMessage("\n0 For Logout\n1 For Add Product\n2 For List Products\n3 For List Users");
            int choice = view.getUserIntInput("Enter your choice: ");
            switch (choice) {
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
                default:
                    view.displayMessage("Invalid choice");
            }
        }
    }

    private void addProduct() {
        String productName = view.getUserInput("Enter product name: ");
        int productPrice = view.getUserIntInput("Enter product price: ");
        ProductBean product = new ProductBean(productPrice, productName);
        products.add(product);
        view.displayMessage("Product added successfully!");
    }

    private void handleCustomerMenu() {
        boolean repeat = true;
        while (repeat) {
            view.displayMessage("0 For Logout\n1 For View Products\n2 For Add To Cart\n3 For View Cart");
            int choice = view.getUserIntInput("Enter your choice: ");
            switch (choice) {
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
                    viewCart();
                    break;
                default:
                    view.displayMessage("Invalid choice");
            }
        }
    }

    private void addToCart() {
        int productId = view.getUserIntInput("Enter productId for cart: ");
        ProductBean product = findProduct(productId);
        if (product != null) {
            currentUser.cart.add(product);
            view.displayMessage("Product added to cart!");
        } else {
            view.displayMessage("Invalid product ID!");
        }
    }

    private ProductBean findProduct(int productId) {
        for (ProductBean product : products) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    private void viewCart() {
        int price=0;
        view.displayMessage("Cart Items:");
        for (ProductBean product : currentUser.cart) {
            view.displayMessage(product.getProductName()+"\t"+product.getPrice());
            price+=product.getPrice();
        }
        view.displayMessage("Total bill: "+price);
    }
}