import controller.InventoryController;
import view.InventoryView;

public class Main {
    public static void main(String[] args) {
        InventoryView view = new InventoryView();
        InventoryController controller = new InventoryController(view);
        controller.start();
    }
}