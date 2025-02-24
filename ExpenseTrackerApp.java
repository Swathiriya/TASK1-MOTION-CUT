import java.io.*;
import java.time.LocalDate;
import java.util.*;

class Expense {
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Expense(double amount, String category, String description, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return date + " | " + category + " | $" + amount + " | " + description;
    }
}

class ExpenseManager {
    private List<Expense> expenses;
    private static final String FILE_NAME = "expenses.txt";

    public ExpenseManager() {
        expenses = new ArrayList<>();
        loadExpensesFromFile();
    }

    public void addExpense(double amount, String category, String description) {
        Expense expense = new Expense(amount, category, description, LocalDate.now());
        expenses.add(expense);
        saveExpensesToFile();
        System.out.println("Expense added successfully!");
    }

    public void displayExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        System.out.println("\n--- All Expenses ---");
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }

    public void displayTotalByPeriod(String period) {
        LocalDate now = LocalDate.now();
        double total = expenses.stream()
                .filter(e -> {
                    if (period.equalsIgnoreCase("day")) return e.getDate().equals(now);
                    if (period.equalsIgnoreCase("week")) return e.getDate().isAfter(now.minusDays(7));
                    if (period.equalsIgnoreCase("month")) return e.getDate().getMonth() == now.getMonth();
                    return false;
                })
                .mapToDouble(Expense::getAmount)
                .sum();

        System.out.println("Total expenses for the " + period + ": $" + total);
    }

    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                writer.println(e.getDate() + "," + e.getCategory() + "," + e.getAmount() + "," + e.getDescription());
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses.");
        }
    }

    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String description = parts[3];
                    expenses.add(new Expense(amount, category, description, date));
                }
            }
        } catch (IOException e) {
            System.out.println("No previous expenses found.");
        }
    }
}

public class ExpenseTrackerApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();

        while (true) {
            System.out.println("\n--- Daily Expense Tracker ---");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Summary (Day/Week/Month)");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine();

                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();

                    manager.addExpense(amount, category, description);
                    break;

                case 2:
                    manager.displayExpenses();
                    break;

                case 3:
                    System.out.print("Enter period (day/week/month): ");
                    String period = scanner.nextLine();
                    manager.displayTotalByPeriod(period);
                    break;

                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
