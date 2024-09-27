import java.util.List;

public class Order {
    private List<Item> items;
    private String customerName;
    private String customerEmail;

    public Order(List<Item> items, String customerName, String customerEmail) {
        this.items = items;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public double calculateTotalPrice() {
    	double total = 0.0;
    	for (Item item : items) {
        	total += item.calculatePrice();
            if (item.hasGiftCard()) {
                total -= 10.0; // subtract $10 for gift card
            }
        }
    	if (total > 100.0) {
        	total *= 0.9; // apply 10% discount for orders over $100
    	}
    	return total;
    }

    public void sendConfirmationEmail() {
        String message = "Thank you for your order, " + customerName + "!\n\n" +
                "Your order details:\n";
        for (Item item : items) {
            message += item.getName() + " - " + item.getPrice() + "\n";
        }
        message += "Total: " + calculateTotalPrice();
        EmailSender.sendEmail(customerEmail, "Order Confirmation", message);
    }


    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

   public void printOrder() {
        System.out.println("Order Details:");
        for (Item item : items) {
            System.out.println(item.getName() + " - " + item.getPrice());
        }
   }

   public void addItemsFromAnotherOrder(Order otherOrder) {
        for (Item item : otherOrder.getItems()) {
            items.add(item);
        }
   }

}

