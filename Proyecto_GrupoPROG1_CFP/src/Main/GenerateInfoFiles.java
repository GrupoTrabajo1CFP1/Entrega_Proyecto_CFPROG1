package Main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;

/**
 * Constructs a GenerateInfoFiles instance.
 * The GenerateInfoFiles class is responsible for generating and managing files related to
 * salesmen, products, and sales. It also includes methods for deleting old sales files and
 * serializing sales data.
 */
public class GenerateInfoFiles {
	/**
	 * Main method to create salesmen, products, delete old sales files, and generate new sales files.
	 * 
	 * @param args Command-line arguments (not used).
	 */
    public static void main(String[] args) {
        List<Long> salesmanIds = createSalesManInfoFile(10);
        createProductsFile(20);
        deleteOldSalesFiles("resources"); // Call the method to delete old sales file
        createSalesFiles(salesmanIds, 10); // Create new sales files
    }

    /**
     * Deletes old sales files from the specified directory.
     * 
     * @param directoryPath The path of the directory where old sales files are located.
     */
    public static void deleteOldSalesFiles(String directoryPath) {
        try {
       
            Files.list(Paths.get(directoryPath))
                .filter(path -> path.getFileName().toString().startsWith("sales_"))
                .forEach(path -> {
                    try {
                        Files.delete(path);// Delete the file
                    } catch (IOException e) {
                        System.err.println("Error deleting file: " + path.getFileName());
                    }
                });

            System.out.println("Old sales files deleted successfully.");
        } catch (IOException e) {
            System.err.println("Error accessing the directory: " + e.getMessage());
        }
    }


/**
 * Creates a file with information about salesmen and returns a list of their IDs.
 * 
 * @param salesmanCount The number of salesmen to create.
 * @return A list of salesmen IDs.
 */
    public static List<Long> createSalesManInfoFile(int salesmanCount) {
        List<Long> salesmanIds = new ArrayList<>();
        
        try {
            FileWriter writer = new FileWriter("resources/salesmen.txt", false); /// Overwrite if exists
            writer.write("Code;ID;Name;LastName\n");
            
            Random rand = new Random();

            for (int i = 0; i < salesmanCount; i++) {
                String idType = "CC";
                long idNumber = 100000000 + rand.nextInt(900000000);  // ID between 100000000 and 99999999
                String firstName = "Name " + (i + 1);
                String lastName = "LastName " + (i + 1);

                // Save the salesman ID
                salesmanIds.add(idNumber);

                writer.write(idType + ";" + idNumber + ";" + firstName + ";" + lastName + "\n");
            }

            writer.close();
            System.out.println("Salesmen info file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return salesmanIds;// Return the list of generated IDs
    }

    /**
     * Creates a file with product information, including IDs, names, and prices.
     * 
     * @param productsCount The number of products to create.
     */
    public static void createProductsFile(int productsCount) {
        try {
            FileWriter writer = new FileWriter("resources/products.txt", false); // Overwrite if exists
            writer.write("ID;Name;Price\n");
            Random rand = new Random();
            for (int i = 0; i < productsCount; i++) {
                int productID = i + 1;
                String productName = "Product " + productID;
                double price = Math.abs(rand.nextDouble() * 100);  // Price between 0 and 100

                // Validate that the price is not negative
                if (price < 0) {
                    System.err.println("Invalid price generated for Product ID " + productID + ": Price cannot be negative.");
                    continue; // Skip this product if invalid
                }

                writer.write(productID + ";" + productName + ";" + String.format("%.2f", price).replace(",", ".") + "\n");
            }

            writer.close();
            System.out.println("Products file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes the sales data into a file.
     * 
     * @param sales A list of sales to serialize.
     */
    public static void serializeSales(List<Sale> sales) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("resources/sales_data.ser", false))) {
            oos.writeObject(sales);  // Overwrite the sales_data.ser file with the new list of sales
            System.out.println("Sales data serialized and overwritten successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates sales files for salesmen and adds sales records to each file.
     * A maximum of 10 sales files are created, reusing salesman IDs if necessary.
     * 
     * @param salesmanIds A list of salesmen IDs to associate with sales.
     * @param randomSalesCount The number of sales to generate randomly.
     */
    public static void createSalesFiles(List<Long> salesmanIds, int randomSalesCount) {
        List<Sale> allSales = new ArrayList<>();
        int maxFiles = 10; // Overwrite if exists

        try {
            for (int i = 0; i < salesmanIds.size(); i++) {
                Long salesmanId = salesmanIds.get(i % maxFiles); // Cycle through salesman IDs when limit is reached
                FileWriter writer = new FileWriter("resources/sales_" + salesmanId + ".txt", false);  // Overwrite if exists
                Random rand = new Random();

                writer.write("ProductId;SalesmanId;Quantity\n");

                for (int j = 0; j < randomSalesCount; j++) {
                    int productID = rand.nextInt(20) + 1;// Assuming you have 20 products
                    int quantity = rand.nextInt(10) + 1;

                    if (quantity <= 0) {
                        quantity = 1;
                    }

                    if (productID < 1 || productID > 20) {
                        System.err.println("Invalid Sale: Product ID " + productID + " is out of range.");
                        continue;
                    }

                    writer.write(productID + ";" + salesmanId + ";" + quantity + "\n");

                    // Add the sale to the list for serialization
                    allSales.add(new Sale(productID, salesmanId, quantity));
                }

                writer.close();
                System.out.println("Sales file for salesman " + salesmanId + " created/overwritten successfully.");
            }

            // Serialize the sales data after creating all files
            serializeSales(allSales);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
