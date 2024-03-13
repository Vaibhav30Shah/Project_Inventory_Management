package controller;


import bean.ProductBean;
import bean.UserBean;
import authentcation.AuthenticationServer;
import view.InventoryView;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class InventoryController {
    private InventoryView view;
    private Scanner scanner=new Scanner(System.in);
    private List<UserBean> users;
    private List<ProductBean> products;
    private UserBean currentUser;
    Socket socket;
    static int count=0;

    public InventoryController(List<ProductBean> products, List<UserBean> users, InventoryView view, Socket socket) {
        this.products = products;
        this.users = users;
        this.view = view;
        this.socket=socket;
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
            case 1:
                signup();
                break;
            case 2:
                login();
                break;
            case 3:
                view.displayMessage("Exiting...");
                try {
                    socket.close();
                    System.out.println(socket.getInetAddress()+" exited");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                view.displayMessage("Invalid choice");
        }
    }


    private void signup() {
        String firstName = view.getUserInput("Enter first name: ");
        String email = view.getUserInput("Enter email: ");
        String password = view.getUserInput("Enter password: ");
        UserBean newUser = new UserBean(firstName, email, password);
        if (newUser.emailExist(email, users)) {
            view.displayMessage("User already exists. Please login");
            return;
        } else {
            users.add(newUser);
            AuthenticationServer.addNewUser(email, password);
            newUser.saveUserData(users);
            view.displayMessage("Signup successful!");
        }
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
                count++;
                view.displayMessage("Invalid credentials!");
            } else {
                currentUser = findUser(email, password);
                if (currentUser != null) {
                    count=0;
                    view.displayMessage("Login successful!");
                    if (currentUser.isAdmin()) {
                        handleAdminMenu();
                    } else {
                        handleCustomerMenu();
                    }
                } else {
                    count++;
                    view.displayMessage("Invalid credentials!");
                }
            }

            if(count>3){
                view.displayMessage("You exceeded the login limits. Try Again later.");
                socket.close();
                System.exit(0);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserBean findUser(String email, String password) {
        for (UserBean user : users) {
            if (user.getEmail().equals(email)) {
                if (user.getPassword().equals(password)) {
                    return user;
                } else if (user.isAdmin() && password.equals("admin")) {
                    // Handle the case where the admin user's password is "admin"
                    return user;
                }
            }
        }
        return null;
    }

    private void handleAdminMenu() {
        boolean repeat = true;
        while (repeat) {
            view.displayMessage("\n0 For Logout\n1 For Add Product\n2 For List Products\n3 For List Users \n4 For Update Product Details");
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
                case 4:
                    updateProduct();
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
        ProductBean.saveProductData(products);
        view.displayMessage("Product added successfully!");
    }

    private void updateProduct(){
//        int pid=view.getUserIntInput("Enter Product ID to update: ");
//        String choice=view.getUserInput("What you want to update? ProductName(N) or Price(P)?");
//        switch (choice){
//            case "N":
//                int index=products.indexOf(pid);
//                String newName=view.getUserInput("Enter new name: ");
//                products.set(index+1,ProductBean(price, newName));
//        }
    }

    private void handleCustomerMenu() {
        boolean repeat = true;
        while (repeat) {
            view.displayMessage("0 For Logout\n1 For View Products\n2 For Add To Cart \n3 For Remove from Cart\n4 For View Cart");
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
                    removeFromCart();
                    break;
                case 4:
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

    private void removeFromCart() {
        int productId = view.getUserIntInput("Enter productId for cart: ");
        ProductBean product = findProduct(productId);
        if (product != null) {
            if(currentUser.cart.removeIf(currentProduct->currentProduct.getProductId()==productId)){
                currentUser.cart.remove(product);
                view.displayMessage("Product removed from cart!");
            }else{
                view.displayMessage("Product not added in Cart!");
            }
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
            view.displayMessage(product.getProductId()+"\t"+product.getProductName()+"\t"+product.getPrice());
            price+=product.getPrice();
        }
        view.displayMessage("Total bill: "+price);
    }
}