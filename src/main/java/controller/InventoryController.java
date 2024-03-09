package controller;


import bean.ProductBean;
import bean.UserBean;
import view.InventoryView;

import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private InventoryView view;
    private List<UserBean> users;
    private List<ProductBean> products;
    private UserBean currentUser;

    public InventoryController(InventoryView view) {
        this.view = view;
        users = new ArrayList<>();
        products = new ArrayList<>();
        initializeDummyData();
    }

    private void initializeDummyData() {
        // Add dummy users
        users.add(new UserBean("Vaibhav", "vaibhav@gmail.com", "vaibhav123"));
        users.add(new UserBean("Aagnesh", "aagnesh@gmail.com", "aagnesh123"));
        users.add(new UserBean("Dhruv", "dhruv@gmail.com", "dhruv123"));
        users.add(new UserBean("Ram", "ram@gmail.com", "ram123"));
        UserBean admin = new UserBean("admin", "admin@admin.com", "admin");
        admin.setAdmin(true);
        users.add(admin);

        // Add dummy products
        products.add(new ProductBean(50000, "iPhone"));
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
        view.displayMessage("Signup successful!");
    }

    private void login() {
        String email = view.getUserInput("Enter email: ");
        String password = view.getUserInput("Enter password: ");
        UserBean user = findUser(email, password);
        if (user != null) {
            currentUser = user;
            view.displayMessage("Login successful!");
            if (user.isAdmin()) {
                handleAdminMenu();
            } else {
                handleCustomerMenu();
            }
        } else {
            view.displayMessage("Invalid credentials!");
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
            view.displayMessage("\n0 For Logout\n1 For Add Product\n2 For List Products\nEnter choice");
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
            view.displayMessage("0 For Logout\n1 For View Products\n2 For Add To Cart\n3 For View Cart\nEnter your choice..");
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
        view.displayMessage("Cart Items:");
        for (ProductBean product : currentUser.cart) {
            view.displayMessage(product.getProductName());
        }
    }
}