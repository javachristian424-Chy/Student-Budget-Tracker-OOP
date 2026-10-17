/*displays the header, profile button, summary cards, and transaction table. */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public JLabel balanceLabel;
    public JLabel incomeLabel;
    public JLabel expenseLabel;
    public JLabel allowanceLabel;
    public JLabel studentInfoLabel;

    public DefaultTableModel tableModel;
    public JTable transactionTable;

    public DashboardPanel(Runnable editProfileAction) {
        setLayout(new BorderLayout(5, 5));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 58, 95));
        header.setBorder(
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        );

        JLabel titleLabel = new JLabel("Student Budget Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);

        studentInfoLabel = new JLabel();
        studentInfoLabel.setForeground(Color.WHITE);

        JPanel heading = new JPanel(new GridLayout(2, 1, 4, 4));
        heading.setOpaque(false);
        heading.add(titleLabel);
        heading.add(studentInfoLabel);

        JButton profileButton =
            new JButton("Edit Student Profile");

        profileButton.setPreferredSize(new Dimension(180, 30));
        profileButton.addActionListener(e -> editProfileAction.run());

        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(
            new BoxLayout(studentPanel, BoxLayout.Y_AXIS)
        );
        studentPanel.setOpaque(false);

        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        studentPanel.add(heading);
        studentPanel.add(Box.createVerticalStrut(8));
        studentPanel.add(profileButton);

        header.add(studentPanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // SUMMARY CARDS
        JPanel summary = new JPanel(
            new GridLayout(1, 4, 10, 10)
        );
        summary.setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        balanceLabel = new JLabel("₱0.00");
        allowanceLabel = new JLabel("₱0.00");
        incomeLabel = new JLabel("₱0.00");
        expenseLabel = new JLabel("₱0.00");

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

        JScrollPane tableScroll =
            new JScrollPane(transactionTable);

        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.add(summary, BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
            )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.DARK_GRAY);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 19));

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }
}
