public class PercentageCalculateDiscount implements CalculateTotalPriceable {

    @Override
    public double calculatePrice(Item item) {
        double price = item.getPrice();
        price -= item.getDiscountAmount() * price;
        price += price * item.getQuantity();
        price += calculateTax(item);
        return price;
    }

    private double calculateTax(Item item) {
        if (item instanceof TaxableItem) {
            TaxableItem taxableItem = (TaxableItem) item;
            return taxableItem.getTaxRate() / 100.0 * item.getPrice();
        }
        return 0.0;
    }
}
