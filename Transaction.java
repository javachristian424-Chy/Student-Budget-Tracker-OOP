
import java.time.LocalDate;

public abstract class Transaction {
    private String description;
    private double amount;
    private LocalDate date;
    private Category category;

    public Transaction(String description, double amount,
                       LocalDate date, Category category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public abstract double applyToBalance(double balance);

    public abstract String getType();

    public abstract String getSignedAmount();
}
