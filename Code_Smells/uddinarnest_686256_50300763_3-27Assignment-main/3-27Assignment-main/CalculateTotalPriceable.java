public interface CalculateTotalPriceable {
    double calculatePrice(Item item);

    default double taxTheItem(Item item) {
        return 0.0; // Default implementation does nothing (returns 0)
    }
}
