package bean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String FILE_NAME = "productData.txt";
    private static List<ProductBean> products = new ArrayList<>();

    static {
        loadProductData();
    }

    public static List<ProductBean> getProducts() {
        return products;
    }

    public static void addProduct(ProductBean product) {
        products.add(product);
        saveProductData(products);
    }

    public static void updateProduct(int productId, String newProductName, int newProductPrice) {
        ProductBean product = findProduct(productId);
        if (product != null) {
            if (!newProductName.isBlank()) {
                product.setProductName(newProductName);
            }
            if (newProductPrice != 0) {
                product.setPrice(newProductPrice);
            }
            saveProductData(products);
        }
    }

    public static void removeProduct(int productId) {
        ProductBean product = findProduct(productId);
        if (product != null) {
            products.remove(product);
            saveProductData(products);
        }
    }

    private static ProductBean findProduct(int productId) {
        for (ProductBean product : products) {
            if (product.getProductId() == productId) {
                return product;
            }
        }
        return null;
    }

    public static void saveProductData(List<ProductBean> products)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME)))
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

    public static List<ProductBean> loadProductData()
    {
        List<ProductBean> produts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                String[] splitArray = line.split(",");

                String productId = splitArray[0];

                String productName = splitArray[1];

                int price = Integer.parseInt(splitArray[2]);

                ProductBean product = new ProductBean(price, productName);

                produts.add(product);
            }
        }
        catch (FileNotFoundException e)
        {
            File file = new File(FILE_NAME);

            try
            {
                file.createNewFile();
            }
            catch (IOException ex)
            {
                System.out.println(e);
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        return produts;
    }
}