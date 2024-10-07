package Main;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.stream.Stream;

/**
 * Main class for the application.
 * Main class that handles the execution of the application, reading data files, 
 * and generating sales reports
 */
public class Main {
	/**
     * Main method to run the application.
     * 
     * @param args command line arguments (not used)
     */
	public static void main(String[] args) {
	    try {
	        List<Salesman> salesmen = readSalesmenFile("resources/salesmen.txt");
	        List<Product> products = readProductsFile("resources/products.txt");
	        List<Sale> sales = readSerializedSales("resources/sales_data.ser"); // Read serialized sales data

	        if (generateReports(salesmen, products, sales)) {
	            System.out.println("Report files successfully generated!");
	        } else {
	            System.err.println("Error generating report files.");
	        }
	    } catch (Exception e) {
	        System.err.println("Unexpected error: " + e.getMessage());
	        e.printStackTrace(); // Imprimir el stack trace para más detalles
	    }
	}

    /**
     * Reads serialized sales data from a specified file.
     *
     * @param filename the path of the serialized sales data file
     * @return a list of Sale objects deserialized from the file
     */
    @SuppressWarnings("unchecked")
    public static List<Sale> readSerializedSales(String filename) {
        List<Sale> sales = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            sales = (List<Sale>) ois.readObject();
            System.out.println("Sales data deserialized successfully.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sales;
    }
    /**
     * Reads the salesmen data from a specified file.
     *
     * @param filename the path of the salesmen data file
     * @return a list of Salesman objects read from the file
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
     * Reads the product data from a specified file.
     *
     * @param filename the path of the product data file
     * @return a list of Product objects read from the file
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
     * Reads all sales files from a specified directory.
     *
     * @param directoryPath the path of the directory containing sales files
     * @return a list of Sale objects read from all sales files in the directory
     */
    public static List<Sale> readAllSalesFiles(String directoryPath) {
        List<Sale> sales = new ArrayList<>();
        try {
            Files.list(Paths.get(directoryPath))
                 .filter(path -> path.getFileName().toString().startsWith("sales_") && path.getFileName().toString().endsWith(".txt"))
                 .forEach(path -> {
                     List<Sale> salesFromFile = readSalesFile(path.toString());
                     sales.addAll(salesFromFile);
                 });
        } catch (IOException e) {
            System.err.println("Error reading sales files: " + e.getMessage());
        }
        return sales;
    }

    /**
     * Reads a sales file and extracts sales data.
     *
     * @param filename the path of the sales data file
     * @return a list of Sale objects read from the file
     */
    public static List<Sale> readSalesFile(String filename) {
        List<Sale> sales = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filename))) {
            lines.skip(1) // Skip header
                 .forEach(line -> {
                     String[] parts = line.split(";");
                     if (parts.length < 3) {
                         System.err.println("Invalid line format: " + line);
                         return; // Skip invalid lines
                     }

                     try {
                         int productId = Integer.parseInt(parts[0].trim());
                         long salesmanId = Long.parseLong(parts[1].trim());
                         int quantity = Integer.parseInt(parts[2].trim());

                         if (salesmanId == 0) {
                             System.err.println("Invalid SalesmanId in line: " + line);
                             return;
                         }

                         sales.add(new Sale(productId, salesmanId, quantity));
                     } catch (NumberFormatException e) {
                         System.err.println("Invalid number format in line: " + line);
                     }
                 });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }
    
    /**
     * Generates reports based on the provided salesmen, products, and sales data.
     *
     * @param salesmen a list of Salesman objects
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @return true if reports are generated successfully, false otherwise
     */
    public static boolean generateReports(List<Salesman> salesmen, List<Product> products, List<Sale> sales) {
        try {
            // Generate current reports
            generateSalesReport(salesmen, products, sales);
            generateProductSalesReport(products, sales);
            
            generateSalesReportDeserialized(salesmen, products, sales);
            generateProductSalesReportDeserialized(products, sales);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating report files: " + e.getMessage());
            return false;
        }
    }
    /**
     * Generates a sales report in CSV format.
     *
     * @param salesmen a list of Salesman objects
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @throws IOException if an error occurs during file writing
     */
    @SuppressWarnings("deprecation")
	public static void generateSalesReport(List<Salesman> salesmen, List<Product> products, List<Sale> sales) throws IOException {
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

        List<Map.Entry<Long, Double>> sortedSalesmen = new ArrayList<>(salesBySalesman.entrySet());
        sortedSalesmen.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        try (FileWriter writer = new FileWriter("resources/sales_report.csv")) {
            writer.write("SalesmanId;Name;LastName;TotalSales\n");
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));

            for (Map.Entry<Long, Double> entry : sortedSalesmen) {
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
     * Generates a deserialized sales report in CSV format.
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
            writer.write("ProductId;Name;TotalSold\n");
            for (Product product : products) {
                int totalSold = salesByProduct.getOrDefault(product.getId(), 0);
                writer.write(product.getId() + ";Product " + product.getId() + ";" + totalSold + "\n");
            }
            System.out.println("Product sales report generated successfully.");
        }
    }
    /**
     * Generates a deserialized sales report in CSV format.
     * @param salesmen a list of Salesman objects
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @throws IOException if an error occurs during file writing
     */
    public static void generateSalesReportDeserialized(List<Salesman> salesmen, List<Product> products, List<Sale> sales) throws IOException {
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

        List<Map.Entry<Long, Double>> sortedSalesmen = new ArrayList<>(salesBySalesman.entrySet());
        sortedSalesmen.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        try (FileWriter writer = new FileWriter("resources/sales_report_deserialized.csv")) {
            writer.write("SalesmanId;Name;LastName;TotalSales\n");
            @SuppressWarnings("deprecation")
			NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));

            for (Map.Entry<Long, Double> entry : sortedSalesmen) {
                Salesman salesman = findSalesmanById(salesmen, entry.getKey());
                if (salesman != null) {
                    String totalSalesFormatted = numberFormat.format(entry.getValue());
                    writer.write(salesman.getId() + ";" + salesman.getName() + ";" + salesman.getLastName() + ";"
                            + totalSalesFormatted + "\n");
                } else {
                    System.err.println("Salesman with ID " + entry.getKey() + " not found. Skipping report entry.");
                }
            }
            System.out.println("Sales report deserialized generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing sales report: " + e.getMessage());
        }
    }


    /**
     * Generates a deserialized product sales report in CSV format.
     *
     * @param products a list of Product objects
     * @param sales a list of Sale objects
     * @throws IOException if an error occurs during file writing
     */
    public static void generateProductSalesReportDeserialized(List<Product> products, List<Sale> sales) throws IOException {
        Map<Integer, Integer> salesByProduct = new HashMap<>();

        // Calcular total de ventas
        for (Sale sale : sales) {
            salesByProduct.put(sale.getProductId(),
                    salesByProduct.getOrDefault(sale.getProductId(), 0) + sale.getQuantity());
        }

        // Generar archivo CSV
        try (FileWriter writer = new FileWriter("resources/product_sales_report_deserialized.csv")) {
            writer.write("ProductId;Name;TotalSold\n");
            for (Product product : products) {
                int totalSold = salesByProduct.getOrDefault(product.getId(), 0);
                writer.write(product.getId() + ";Product " + product.getId() + ";" + totalSold + "\n");
            }
            System.out.println("Product sales report (deserialized) generated successfully.");
        }
    }
    

    /**
     * Finds a Salesman by ID.
     *
     * @param salesmen a list of Salesman objects
     * @param id the ID of the Salesman to find
     * @return the Salesman object if found, null otherwise
     */
    public static Salesman findSalesmanById(List<Salesman> salesmen, long id) {
        for (Salesman salesman : salesmen) {
            if (salesman.getId() == id) {
                return salesman;
            }
        }
        return null; // Not found
    }

    /**
     * Finds a Product by ID.
     *
     * @param products a list of Product objects
     * @param id the ID of the Product to find
     * @return the Product object if found, null otherwise
     */
    public static Product findProductById(List<Product> products, int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null; // Not found
    }
}



/**
 * Represents a salesman with an ID, name, and last name.
 */
class Salesman implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private String lastName;

    /**
     * Constructs a Salesman instance with the specified ID, name, and last name.
     * 
     * @param id        the ID of the salesman
     * @param name      the name of the salesman
     * @param lastName  the last name of the salesman
     */
    public Salesman(long id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    /**
     * Gets the ID of the salesman.
     * 
     * @return the ID of the salesman
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the name of the salesman.
     * 
     * @return the name of the salesman
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the last name of the salesman.
     * 
     * @return the last name of the salesman
     */
    public String getLastName() {
        return lastName;
    }
}

/**
 * Represents a product with an ID, name, and price.
 */
class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double price;

    /**
     * Constructs a Product instance with the specified ID, name, and price.
     * 
     * @param id    the ID of the product
     * @param name  the name of the product
     * @param price the price of the product
     */
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Gets the ID of the product.
     * 
     * @return the ID of the product
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of the product.
     * 
     * @return the name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the price of the product.
     * 
     * @return the price of the product
     */
    public double getPrice() {
        return price;
    }
}

/**
 * Represents a sale made by a salesman for a specific product with a quantity.
 */
class Sale implements Serializable {
    private static final long serialVersionUID = 1L; // Adding serialVersionUID

    private int productId;
    private long salesmanId;
    private int quantity;

    /**
     * Constructs a Sale instance with the specified product ID, salesman ID, and quantity.
     * 
     * @param productId  the ID of the product sold
     * @param salesmanId the ID of the salesman making the sale
     * @param quantity   the quantity of the product sold
     */
    public Sale(int productId, long salesmanId, int quantity) {
        this.productId = productId;
        this.salesmanId = salesmanId;
        this.quantity = quantity;
    }

    /**
     * Gets the product ID of the sale.
     * 
     * @return the product ID of the sale
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Gets the salesman ID of the sale.
     * 
     * @return the salesman ID of the sale
     */
    public long getSalesmanId() {
        return salesmanId;
    }

    /**
     * Gets the quantity of the product sold in the sale.
     * 
     * @return the quantity of the product sold
     */
    public int getQuantity() {
        return quantity;
    }
}