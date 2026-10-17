
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class Main extends JFrame {

    private BudgetManager manager;

    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel allowanceLabel;

    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<Category> categoryBox;

    private DefaultTableModel tableModel;
    private JTable transactionTable;

    public Main() {
        manager = BudgetManager.load();

        if (manager == null) {
            manager = createNewManager();

            if (manager == null) {
                System.exit(0);
                return;
            }
        }

        setTitle("Student Budget Tracker");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 58, 95));
        header.setBorder(
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        );

        JLabel title = new JLabel("Student Budget Tracker");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE);

        JLabel studentInfo = new JLabel(
            "Student: " + manager.getStudent().getName()
            + " | ID: " + manager.getStudent().getStudentId()
        );
        studentInfo.setForeground(Color.WHITE);

        JPanel heading = new JPanel(new GridLayout(2, 1, 4, 4));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(studentInfo);

        header.add(heading, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // SUMMARY CARDS
        JPanel summary = new JPanel(new GridLayout(1, 4, 10, 10));
        summary.setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        balanceLabel = new JLabel();
        allowanceLabel = new JLabel();
        incomeLabel = new JLabel();
        expenseLabel = new JLabel();

        summary.add(createCard("Remaining Balance", balanceLabel));
        summary.add(createCard("Starting Allowance", allowanceLabel));
        summary.add(createCard("Total Income", incomeLabel));
        summary.add(createCard("Total Expenses", expenseLabel));

        // TRANSACTION TABLE
        String[] columns = {
            "Date", "Type", "Description", "Category", "Amount"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(27);
        transactionTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        transactionTable.setAutoCreateRowSorter(true);

        JScrollPane tableScroll = new JScrollPane(transactionTable);

        // INPUT FIELDS
        descriptionField = new JTextField(12);
        amountField = new JTextField(8);
        categoryBox = new JComboBox<>();

        for (Category category : manager.getCategories()) {
            if (!category.getName().equalsIgnoreCase("Income")) {
                categoryBox.addItem(category);
            }
        }

        // BUTTONS
        JButton addIncomeButton = new JButton("Add Income");
        JButton addExpenseButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        addIncomeButton.setBackground(new Color(46, 139, 87));
        addIncomeButton.setForeground(Color.WHITE);

        addExpenseButton.setBackground(new Color(190, 65, 65));
        addExpenseButton.setForeground(Color.WHITE);

        // ROW 1: INPUT FIELDS
        JPanel inputPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 8, 8)
        );

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);

        // ROW 2: BUTTONS
        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 8)
        );

        buttonPanel.add(addIncomeButton);
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel form = new JPanel(new GridLayout(2, 1, 0, 5));
        form.add(inputPanel);
        form.add(buttonPanel);

        // MAIN CONTENT
        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(summary, BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);
        center.add(form, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addIncomeButton.addActionListener(e -> addTransaction(true));
        addExpenseButton.addActionListener(e -> addTransaction(false));
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());

        refreshDashboard();
    }

    // CREATE STUDENT PROFILE
    private BudgetManager createNewManager() {
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField allowanceField = new JTextField("0");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));

        form.add(new JLabel("Student Name:"));
        form.add(nameField);
        form.add(new JLabel("Student ID:"));
        form.add(idField);
        form.add(new JLabel("Starting Allowance (PHP):"));
        form.add(allowanceField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                null,
                form,
                "Student Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            try {
                String name = nameField.getText().trim();
                String id = idField.getText().trim();

                double allowance = Double.parseDouble(
                    allowanceField.getText().trim()
                );

                Student student = new Student(name, id, allowance);
                BudgetManager newManager = new BudgetManager(student);

                newManager.save();
                return newManager;

            } catch (IllegalArgumentException | IOException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please check your information.\n"
                    + ex.getMessage(),
                    "Invalid Information",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // SUMMARY CARD DESIGN
    private JPanel createCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
            )
        );

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.DARK_GRAY);

        value.setFont(new Font("Arial", Font.BOLD, 19));

        card.add(heading);
        card.add(value);

        return card;
    }

    // ADD INCOME OR EXPENSE
    private void addTransaction(boolean isIncome) {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();

        if (description.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Enter a description and amount."
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                JOptionPane.showMessageDialog(
                    this, "Amount must be greater than zero."
                );
                return;
            }

            Category category;

            if (isIncome) {
                category = manager.getCategory("Income");
            } else {
                category = (Category) categoryBox.getSelectedItem();
            }

            if (category == null) {
                JOptionPane.showMessageDialog(
                    this, "Please select a category."
                );
                return;
            }

            Transaction transaction;

            if (isIncome) {
                transaction = new Income(
                    description, amount, LocalDate.now(), category
                );
            } else {
                transaction = new Expense(
                    description, amount, LocalDate.now(), category
                );
            }

            manager.addTransaction(transaction);

            try {
                manager.save();
            } catch (IOException ex) {
                manager.removeTransaction(
                    manager.getTransactions().size() - 1
                );

                JOptionPane.showMessageDialog(
                    this,
                    "Could not save transaction: " + ex.getMessage()
                );
                return;
            }

            refreshDashboard();

            descriptionField.setText("");
            amountField.setText("");

            if (!isIncome) {
                String warning = manager.checkLimit(category);

                if (warning != null) {
                    JOptionPane.showMessageDialog(this, warning);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid amount, such as 150.00."
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // EDIT SELECTED TRANSACTION
    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this, "Select a transaction in the table first."
            );
            return;
        }

        int modelRow = transactionTable.convertRowIndexToModel(selectedRow);

        Transaction oldTransaction =
            manager.getTransactions().get(modelRow);

        JTextField descriptionInput =
            new JTextField(oldTransaction.getDescription());

        JTextField amountInput =
            new JTextField(String.valueOf(oldTransaction.getAmount()));

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Description:"));
        panel.add(descriptionInput);
        panel.add(new JLabel("Amount:"));
        panel.add(amountInput);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Transaction",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String description = descriptionInput.getText().trim();
            double amount = Double.parseDouble(
                amountInput.getText().trim()
            );

            if (description.isEmpty()
                    || !Double.isFinite(amount)
                    || amount <= 0) {
                throw new IllegalArgumentException(
                    "Enter a description and a positive amount."
                );
            }

            Transaction updated;

            if (oldTransaction instanceof Income) {
                updated = new Income(
                    description,
                    amount,
                    oldTransaction.getDate(),
                    oldTransaction.getCategory()
                );
            } else {
                updated = new Expense(
                    description,
                    amount,
                    oldTransaction.getDate(),
                    oldTransaction.getCategory()
                );
            }

            manager.removeTransaction(modelRow);
            manager.addTransactionAt(modelRow, updated);

            try {
                manager.save();
            } catch (IOException ex) {
                manager.removeTransaction(modelRow);
                manager.addTransactionAt(modelRow, oldTransaction);

                JOptionPane.showMessageDialog(
                    this,
                    "Could not save changes: " + ex.getMessage()
                );
                return;
            }

            refreshDashboard();

            JOptionPane.showMessageDialog(
                this, "Transaction updated successfully!"
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid amount."
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // DELETE SELECTED TRANSACTION
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this, "Select a transaction in the table first."
            );
            return;
        }

        int modelRow = transactionTable.convertRowIndexToModel(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete the selected transaction?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Transaction removed = manager.getTransactions().get(modelRow);

        if (manager.removeTransaction(modelRow)) {
            try {
                manager.save();
                refreshDashboard();
            } catch (IOException ex) {
                manager.addTransactionAt(modelRow, removed);

                JOptionPane.showMessageDialog(
                    this,
                    "Could not save changes: " + ex.getMessage()
                );
            }
        }
    }

    // REFRESH DASHBOARD
    private void refreshDashboard() {
        double balance = manager.getRemainingBalance();

        balanceLabel.setText(String.format("₱%,.2f", balance));

        allowanceLabel.setText(
            String.format(
                "₱%,.2f",
                manager.getStudent().getAllowance()
            )
        );

        incomeLabel.setText(
            String.format("₱%,.2f", manager.getTotalIncome())
        );

        expenseLabel.setText(
            String.format("₱%,.2f", manager.getTotalExpenses())
        );

        tableModel.setRowCount(0);

        for (Transaction transaction : manager.getTransactions()) {
            tableModel.addRow(new Object[] {
                transaction.getDate(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getCategory().getName(),
                transaction.getSignedAmount()
            });
        }
    }

    // APPLICATION START
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
