package bean;

import bean.Product;
import categories.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository
{
    private static final String ELECTRONICS_FILE = "src/main/java/files/electronics_products.txt";

    private static final String CLOTHING_FILE = "src/main/java/files/clothing_products.txt";

    private static final String HOME_DECOR_FURNITURE_FILE = "src/main/java/files/home_decor_furniture_products.txt";

    private static final String BOOKS_FILE = "src/main/java/files/books_products.txt";

    private static final String SPORTS_FITNESS_AND_BAGS_FILE = "src/main/java/files/sports_fitness_bags_products.txt";

    public List<Product> getProducts(int category)
    {
        List<Product> products = new ArrayList<>();

        String fileName = getFileNameForCategory(category);

//        System.out.println(fileName);

        File file=new File(fileName);

        if(file.exists())
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    String[] data = line.split(",");

                    String productId = data[0];

                    String productName = data[1];

                    int productPrice = Integer.parseInt(data[2]);

                    Product product = createProductInstance(productId, category, productName, productPrice);

                    products.add(product);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }

        return products;
    }

    private String getFileNameForCategory(int category)
    {
        switch (category)
        {
            case 1:
                return ELECTRONICS_FILE;

            case 2:
                return CLOTHING_FILE;

            case 3:
                return HOME_DECOR_FURNITURE_FILE;

            case 4:
                return BOOKS_FILE;

            case 5:
                return SPORTS_FITNESS_AND_BAGS_FILE;

            default:
                System.out.println("Invalid Product category");
        }
        return " ";
    }

    private Product createProductInstance(String productId, int category, String productName, int productPrice)
    {
        switch (category)
        {
            case 1:
                return new ElectronicsProduct(productId, productName, productPrice);

            case 2:
                return new ClothingProduct(productId, productName, productPrice);

            case 3:
                return new HomeDecorAndFurniture(productId, productName, productPrice);

            case 4:
                return new Books(productId, productName, productPrice);

            case 5:
                return new SportsFitnessBagsLuggage(productId, productName, productPrice);

            default:
                System.out.println("Invalid Product Category");
                return null;
        }
    }

    public void saveProducts(int category, List<Product> products)
    {
        String fileName = getFileNameForCategory(category);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            for (Product product : products)
            {
                writer.write(product.getProductId() + "," + product.getProductName() + "," + product.getProductPrice() + "," + product.getQuantity() + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
