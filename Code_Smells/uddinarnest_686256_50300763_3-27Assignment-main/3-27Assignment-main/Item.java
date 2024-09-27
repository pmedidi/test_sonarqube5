class Item {
    private String name;
    private double price;
    private int quantity;
    private double discountAmount;
    private CalculateTotalPriceable calculationType;

    public Item(String name, double price, int quantity, double discountAmount, CalculateTotalPriceable calculationType) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discountAmount = discountAmount;
        this.calculationType = calculationType;
    }

    public double calculatePrice() {
        return calculationType.calculatePrice(this);
    }

    public boolean hasGiftCard() {
        if (this.name == "Gift Card") {
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}
