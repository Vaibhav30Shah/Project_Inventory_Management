package categories;

import bean.Product;

public class ElectronicsProduct implements Product
{
    private String productId;

    private String productName;

    private int productPrice;

    private int quantity;

    public ElectronicsProduct(String productId, String productName, int productPrice)
    {
//        this.productId = "E"+(int) (Math.random() * 10000);

        this.productId=productId;

        this.productName = productName;

        this.productPrice = productPrice;
    }

    @Override
    public String getProductId()
    {
        return productId;
    }

    @Override
    public String getProductName()
    {
        return productName;
    }

    @Override
    public void setProductname(String name)
    {
        this.productName=productName;
    }

    @Override
    public int getProductPrice()
    {
        return productPrice;
    }

    @Override
    public void setProductPrice(int price)
    {
        this.productPrice=productPrice;
    }

    @Override
    public int getQuantity()
    {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
}