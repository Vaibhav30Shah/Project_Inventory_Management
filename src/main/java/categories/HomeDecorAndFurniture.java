package categories;

import bean.Product;

public class HomeDecorAndFurniture implements Product
{
    private String productId;

    private String productName;

    private int productPrice;

    private int quantity;

    public HomeDecorAndFurniture(String productId, String productName, int productPrice)
    {
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

    }

    @Override
    public int getProductPrice()
    {
        return productPrice;
    }

    @Override
    public void setProductPrice(int price)
    {

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
