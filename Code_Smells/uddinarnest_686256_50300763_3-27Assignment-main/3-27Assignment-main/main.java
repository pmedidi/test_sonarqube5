import java.util.ArrayList;
import java.util.List;

public class main {
	public static void main(String[] args) {
        Item item1 = new Item("Book", 20, 1, 5, new AmountCalculateDiscount());
        Item item2 = new TaxableItem("Laptop", 1000, 1, 0.1, new PercentageCalculateDiscount());

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Order order = new Order(items, "John Doe", "johndoe@example.com");

        System.out.println("Total Price: " + order.calculateTotalPrice());

        order.sendConfirmationEmail();

        order.printOrder();
    }
}

