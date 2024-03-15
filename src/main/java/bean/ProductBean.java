package bean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductBean
{
    private int productId;

    private int price;

    private String productName;

    public static String FILE_NAME = "src/main/java/files/productData.txt";

    public ProductBean(int price, String productName)
    {
        this.price = price;

        this.productName = productName;

        this.productId = (int) (Math.random() * 10000);
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

    //    public void updateName(int pid, String name){
//        this.productName=name;
//    }
    public int getProductId()
    {
        return productId;
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

}
