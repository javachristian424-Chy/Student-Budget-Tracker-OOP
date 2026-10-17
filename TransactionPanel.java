/*- displays the input fields and transaction buttons. */
import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class TransactionPanel extends JPanel {

    public JTextField descriptionField;
    public JTextField amountField;
    public JComboBox<Category> categoryBox;

    public TransactionPanel(
            Collection<Category> categories,
            Runnable addIncomeAction,
            Runnable addExpenseAction,
            Runnable editAction,
            Runnable deleteAction) {

        setLayout(new GridLayout(2, 1, 0, 5));

        // INPUT FIELDS
        descriptionField = new JTextField(12);
        amountField = new JTextField(8);
        categoryBox = new JComboBox<>();

        for (Category category : categories) {
            if (!category.getName().equalsIgnoreCase("Income")) {
                categoryBox.addItem(category);
            }
        }

        JPanel inputPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 8, 8)
        );

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);

        // TRANSACTION BUTTONS
        JButton addIncomeButton = new JButton("Add Income");
        JButton addExpenseButton = new JButton("Add Expense");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        addIncomeButton.setBackground(new Color(46, 139, 87));
        addIncomeButton.setForeground(Color.WHITE);

        addExpenseButton.setBackground(new Color(190, 65, 65));
        addExpenseButton.setForeground(Color.WHITE);

        addIncomeButton.addActionListener(
            e -> addIncomeAction.run()
        );

        addExpenseButton.addActionListener(
            e -> addExpenseAction.run()
        );

        editButton.addActionListener(
            e -> editAction.run()
        );

        deleteButton.addActionListener(
            e -> deleteAction.run()
        );

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 8)
        );

        buttonPanel.add(addIncomeButton);
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(inputPanel);
        add(buttonPanel);
    }
}

