
public class Category {
    private String name;
    private double limit;

    public Category(String name, double limit) {
        this.name = name;
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public double getLimit() {
        return limit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public boolean hasLimit() {
        return limit > 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
