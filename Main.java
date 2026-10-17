/*runs the application and handles transaction logic */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class Main extends JFrame {

    private DashboardPanel dashboardPanel;
    private TransactionPanel transactionPanel;
    private BudgetManager manager;

    public Main() {
        manager = BudgetManager.load();

        if (manager == null) {
            manager = createNewManager();

            if (manager == null) {
                System.exit(0);
                return;
            }
        }

        // WINDOW SETTINGS
        setTitle("Student Budget Tracker");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // DASHBOARD
        dashboardPanel = new DashboardPanel(
            () -> editStudentProfile()
        );

        updateStudentInfo();
        add(dashboardPanel, BorderLayout.CENTER);

        // TRANSACTION PANEL
        transactionPanel = new TransactionPanel(
            manager.getCategories(),
            () -> addTransaction(true),
            () -> addTransaction(false),
            () -> editTransaction(),
            () -> deleteTransaction()
        );

        add(transactionPanel, BorderLayout.SOUTH);

        // LOAD CURRENT DATA
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

                if (name.isEmpty() || id.isEmpty()
                        || !Double.isFinite(allowance)
                        || allowance < 0) {
                    throw new IllegalArgumentException(
                        "Enter a valid name, ID, and non-negative allowance."
                    );
                }

                Student student = new Student(name, id, allowance);
                BudgetManager newManager = new BudgetManager(student);
                newManager.save();

                return newManager;

            } catch (IllegalArgumentException | IOException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please check your information.\n" + ex.getMessage(),
                    "Invalid Information",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // UPDATE STUDENT INFORMATION
    private void updateStudentInfo() {
        dashboardPanel.studentInfoLabel.setText(
            "Student: " + manager.getStudent().getName()
            + " | ID: " + manager.getStudent().getStudentId()
        );
    }

    // EDIT STUDENT PROFILE
    private void editStudentProfile() {
        Student student = manager.getStudent();

        JTextField nameField = new JTextField(student.getName());
        JTextField idField = new JTextField(student.getStudentId());

        JTextField allowanceField = new JTextField(
            String.valueOf(student.getAllowance())
        );

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));

        panel.add(new JLabel("Student Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Student ID:"));
        panel.add(idField);
        panel.add(new JLabel("Starting Allowance (PHP):"));
        panel.add(allowanceField);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Student Profile",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newName = nameField.getText().trim();
        String newId = idField.getText().trim();
        double newAllowance;

        try {
            newAllowance = Double.parseDouble(
                allowanceField.getText().trim()
            );

            if (newName.isEmpty() || newId.isEmpty()) {
                throw new IllegalArgumentException(
                    "Name and student ID cannot be empty."
                );
            }

            if (!Double.isFinite(newAllowance) || newAllowance < 0) {
                throw new IllegalArgumentException(
                    "Allowance must be a valid, non-negative amount."
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid allowance amount.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
            return;

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String oldName = student.getName();
        String oldId = student.getStudentId();
        double oldAllowance = student.getAllowance();

        student.setName(newName);
        student.setStudentId(newId);
        student.setAllowance(newAllowance);

        try {
            manager.save();
            updateStudentInfo();
            refreshDashboard();

            JOptionPane.showMessageDialog(
                this,
                "Student profile updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {
            student.setName(oldName);
            student.setStudentId(oldId);
            student.setAllowance(oldAllowance);

            updateStudentInfo();
            refreshDashboard();

            JOptionPane.showMessageDialog(
                this,
                "Could not save your profile.\n" + ex.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ADD INCOME OR EXPENSE
    private void addTransaction(boolean isIncome) {
        String description =
            transactionPanel.descriptionField.getText().trim();

        String amountText =
            transactionPanel.amountField.getText().trim();

        if (description.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a description and amount."
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Amount must be greater than zero."
                );
                return;
            }

            Category category;

            if (isIncome) {
                category = manager.getCategory("Income");
            } else {
                category = (Category)
                    transactionPanel.categoryBox.getSelectedItem();
            }

            if (category == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a category."
                );
                return;
            }

            Transaction transaction;

            if (isIncome) {
                transaction = new Income(
                    description,
                    amount,
                    LocalDate.now(),
                    category
                );
            } else {
                transaction = new Expense(
                    description,
                    amount,
                    LocalDate.now(),
                    category
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

            transactionPanel.descriptionField.setText("");
            transactionPanel.amountField.setText("");

            if (!isIncome) {
                String warning = manager.checkLimit(category);

                if (warning != null) {
                    JOptionPane.showMessageDialog(this, warning);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid amount, such as 150.00."
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // EDIT SELECTED TRANSACTION
    private void editTransaction() {
        JTable transactionTable = dashboardPanel.transactionTable;

        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Select a transaction in the table first."
            );
            return;
        }

        int modelRow =
            transactionTable.convertRowIndexToModel(selectedRow);

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
                    || !Double.isFinite(amount) || amount <= 0) {
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
                this,
                "Transaction updated successfully!"
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid amount."
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // DELETE SELECTED TRANSACTION
    private void deleteTransaction() {
        JTable transactionTable = dashboardPanel.transactionTable;

        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Select a transaction in the table first."
            );
            return;
        }

        int modelRow =
            transactionTable.convertRowIndexToModel(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete the selected transaction?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Transaction removed =
            manager.getTransactions().get(modelRow);

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
        dashboardPanel.balanceLabel.setText(
            String.format("₱%,.2f", manager.getRemainingBalance())
        );

        dashboardPanel.allowanceLabel.setText(
            String.format(
                "₱%,.2f",
                manager.getStudent().getAllowance()
            )
        );

        dashboardPanel.incomeLabel.setText(
            String.format("₱%,.2f", manager.getTotalIncome())
        );

        dashboardPanel.expenseLabel.setText(
            String.format("₱%,.2f", manager.getTotalExpenses())
        );

        DefaultTableModel tableModel = dashboardPanel.tableModel;
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
