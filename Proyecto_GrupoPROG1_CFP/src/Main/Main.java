package Main;

import java.io.*;
import java.util.*;
import java.text.NumberFormat;

// Main class that handles the core functionalities of the program
public class Main {

    public static void main(String[] args) {
        try {
            // Read the real data from the provided files
            List<Salesman> salesmen = readSalesmenFile("resources/salesmen.txt");
            List<Product> products = readProductsFile("resources/products.txt");
            List<Sale> sales = readSalesFile("resources/sales.txt");

            // Generate reports based on the real data
            if (generateReports(salesmen, products, sales)) {
                System.out.println("Report files successfully generated!");
            } else {
                System.err.println("Error generating report files.");
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Reads the salesmen data from a file.
     * 
     * @param filename the file path to read the salesmen data from
     * @return a list of Salesman objects
     */
    public static List<Salesman> readSalesmenFile(String filename) {
        List<Salesman> salesmen = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(";");
                if (parts.length < 4) {
                    System.err.println("Invalid line format: " + line);
                    continue; // Skip invalid lines
                }

                try {
                    long id = Long.parseLong(parts[1].trim());
                    String name = parts[2].trim();
                    String lastName = parts[3].trim();
                    salesmen.add(new Salesman(id, name, lastName));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return salesmen;
    }

    /**
     * Reads the product data from a file.
     * 
     * @param filename the file path to read the products data from
     * @return a list of Product objects
     */
    public static List<Product> readProductsFile(String filename) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(";");
                if (parts.length < 3) {
                    System.err.println("Invalid line format: " + line);
                    continue; // Skip invalid lines
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    products.add(new Product(id, name, price));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Reads the sales data from a file.
     * 
     * @param filename the file path to read the sales data from
     * @return a list of Sale objects
     */
    public static List<Sale> readSalesFile(String filename) {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(";");
                if (parts.length < 3) {
                    System.err.println("Invalid line format: " + line);
                    continue; // Skip invalid lines
                }

                try {
                    int productId = Integer.parseInt(parts[0].trim());
                    long salesmanId = Long.parseLong(parts[1].trim());
                    int quantity = Integer.parseInt(parts[2].trim());

                    // Skip sales with invalid SalesmanId
                    if (salesmanId == 0) {
                        System.err.println("Invalid SalesmanId in line: " + line);
                        continue;
                    }

                    sales.add(new Sale(productId, salesmanId, quantity));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

    /**
     * Generates the sales reports.
     * 
     * @param salesmen a list of Salesman objects
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @return true if reports were generated successfully, false otherwise
     */
    public static boolean generateReports(List<Salesman> salesmen, List<Product> products, List<Sale> sales) {
        try {
            generateSalesReport(salesmen, products, sales);
            generateProductSalesReport(products, sales);
            return true;
        } catch (IOException e) {
            System.err.println("Error generating reports: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a sales report and writes it to a CSV file.
     * 
     * @param salesmen a list of Salesman objects
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @throws IOException if an error occurs during file writing
     */
    @SuppressWarnings("deprecation")
	public static void generateSalesReport(List<Salesman> salesmen, List<Product> products, List<Sale> sales)
            throws IOException {
        Map<Long, Double> salesBySalesman = new HashMap<>();

        for (Sale sale : sales) {
            Product product = findProductById(products, sale.getProductId());
            if (product == null) {
                System.err.println("Product with ID " + sale.getProductId() + " not found. Skipping sale.");
                continue;
            }
            double totalSaleAmount = product.getPrice() * sale.getQuantity();
            salesBySalesman.put(sale.getSalesmanId(),
                    salesBySalesman.getOrDefault(sale.getSalesmanId(), 0.0) + totalSaleAmount);
        }

        try (FileWriter writer = new FileWriter("resources/sales_report.csv")) {
            writer.write("SalesmanId;Name;LastName;TotalSales\n");
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));

            for (Map.Entry<Long, Double> entry : salesBySalesman.entrySet()) {
                Salesman salesman = findSalesmanById(salesmen, entry.getKey());
                if (salesman != null) {
                    String totalSalesFormatted = numberFormat.format(entry.getValue());
                    writer.write(salesman.getId() + ";" + salesman.getName() + ";" + salesman.getLastName() + ";"
                            + totalSalesFormatted + "\n");
                } else {
                    System.err.println("Salesman with ID " + entry.getKey() + " not found. Skipping report entry.");
                }
            }
            System.out.println("Sales report generated successfully.");
        }
    }

    /**
     * Generates a product sales report and writes it to a CSV file.
     * 
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @throws IOException if an error occurs during file writing
     */
    public static void generateProductSalesReport(List<Product> products, List<Sale> sales) throws IOException {
        Map<Integer, Integer> salesByProduct = new HashMap<>();

        for (Sale sale : sales) {
            salesByProduct.put(sale.getProductId(),
                    salesByProduct.getOrDefault(sale.getProductId(), 0) + sale.getQuantity());
        }

        try (FileWriter writer = new FileWriter("resources/product_sales_report.csv")) {
            writer.write("Product;TotalQuantity\n");

            salesByProduct.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .forEach(entry -> {
                        Product product = findProductById(products, entry.getKey());
                        if (product == null) {
                            System.err.println("Product with ID " + entry.getKey() + " not found. Skipping report entry.");
                            return;
                        }
                        try {
                            writer.write(product.getName() + ";" + entry.getValue() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            System.out.println("Product sales report generated successfully.");
        }
    }

    public static Product findProductById(List<Product> products, int productId) {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null;
    }

    public static Salesman findSalesmanById(List<Salesman> salesmen, long salesmanId) {
        for (Salesman salesman : salesmen) {
            if (salesman.getId() == salesmanId) {
                return salesman;
            }
        }
        return null;
    }
}

// Salesman class representing the sellers
class Salesman {
    private long id;
    private String name;
    private String lastName;

    public Salesman(long id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }
}

// Product class representing the products
class Product {
    private int id;
    private String name;
    private double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// Sale class representing the sales data
class Sale {
    private int productId;
    private long salesmanId;
    private int quantity;

    public Sale(int productId, long salesmanId, int quantity) {
        this.productId = productId;
        this.salesmanId = salesmanId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public long getSalesmanId() {
        return salesmanId;
    }

    public int getQuantity() {
        return quantity;
    }
}
