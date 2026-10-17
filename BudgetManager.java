
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class BudgetManager {

    private static final String DATA_FILE = "budget_data.csv";

    private Student student;
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public BudgetManager(Student student) {
        this.student = student;

        addCategory(new Category("Income", 0));
        addCategory(new Category("Food", 1500));
        addCategory(new Category("Transport", 800));
        addCategory(new Category("School", 1000));
        addCategory(new Category("Utilities", 500));
        addCategory(new Category("Others", 0));
    }

    public Student getStudent() {
        return student;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public Collection<Category> getCategories() {
        return categories.values();
    }

    public Category getCategory(String name) {
        return categories.get(name);
    }

    public void addCategory(Category category) {
        categories.put(category.getName(), category);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean removeTransaction(int index) {
        if (index < 0 || index >= transactions.size()) {
            return false;
        }

        transactions.remove(index);
        return true;
    }

    public double getRemainingBalance() {
        double balance = student.getAllowance();

        for (Transaction transaction : transactions) {
            balance = transaction.applyToBalance(balance);
        }

        return balance;
    }

    public double getTotalIncome() {
        return total("Income");
    }

    public double getTotalExpenses() {
        return total("Expense");
    }

    private double total(String type) {
        double sum = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(type)) {
                sum += transaction.getAmount();
            }
        }

        return sum;
    }

    public double getSpentInCategory(Category category) {
        double sum = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("Expense")
                    && transaction.getCategory().getName()
                    .equals(category.getName())) {
                sum += transaction.getAmount();
            }
        }

        return sum;
    }

    public int getPercentUsed() {
        double available = student.getAllowance() + getTotalIncome();

        if (available <= 0) {
            return 0;
        }

        return (int) Math.min(
            100,
            Math.round(getTotalExpenses() / available * 100)
        );
    }

    public String checkLimit(Category category) {
        if (!category.hasLimit()) {
            return null;
        }

        double spent = getSpentInCategory(category);

        if (spent > category.getLimit()) {
            return String.format(
                "You are over your %s budget by P %,.2f.",
                category.getName(),
                spent - category.getLimit()
            );
        }

        if (spent >= category.getLimit() * 0.8) {
            return String.format(
                "You have used %d%% of your %s budget.",
                Math.round(spent / category.getLimit() * 100),
                category.getName()
            );
        }

        return null;
    }

    public void save() throws IOException {
        try (PrintWriter out = new PrintWriter(
                new FileWriter(DATA_FILE))) {

            out.println(
                student.getName().replace(",", " ") + ","
                + student.getStudentId().replace(",", " ") + ","
                + student.getAllowance()
            );

            for (Transaction transaction : transactions) {
                out.println(
                    transaction.getType() + ","
                    + transaction.getDescription() + ","
                    + transaction.getAmount() + ","
                    + transaction.getDate() + ","
                    + transaction.getCategory().getName()
                );
            }
        }
    }

    public static BudgetManager load() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader in = new BufferedReader(
                new FileReader(file))) {

            String firstLine = in.readLine();

            if (firstLine == null) {
                return null;
            }

            String[] studentData = firstLine.split(",");

            BudgetManager manager = new BudgetManager(
                new Student(
                    studentData[0],
                    studentData[1],
                    Double.parseDouble(studentData[2])
                )
            );

            String line;

            while ((line = in.readLine()) != null) {
                String[] data = line.split(",");

                Category category = manager.getCategory(data[4]);

                if (category == null) {
                    category = new Category(data[4], 0);
                    manager.addCategory(category);
                }

                double amount = Double.parseDouble(data[2]);
                LocalDate date = LocalDate.parse(data[3]);

                Transaction transaction;

                if (data[0].equals("Income")) {
                    transaction = new Income(
                        data[1], amount, date, category
                    );
                } else {
                    transaction = new Expense(
                        data[1], amount, date, category
                    );
                }

                manager.addTransaction(transaction);
            }

            return manager;

        } catch (Exception e) {
            System.err.println(
                "Could not load saved budget data: " + e.getMessage()
            );
            return null;
        }
    }
    public void addTransactionAt(int index, Transaction transaction) {
    transactions.add(index, transaction);
   }
}
