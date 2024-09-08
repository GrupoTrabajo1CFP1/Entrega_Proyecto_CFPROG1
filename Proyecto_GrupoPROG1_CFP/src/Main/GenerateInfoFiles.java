package Main;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    // Main method to execute the file generation
    public static void main(String[] args) {
        // Generate example files for testing
        createSalesMenFile(10, "John Doe", 123456789L);
        createProductsFile(20);
        createSalesManInfoFile(10);
    }

    // Method to create a sales file for a specific salesman
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        try {
            // FileWriter object to write to a file
            FileWriter writer = new FileWriter("resources/sales_" + id + ".txt");
            
            Random rand = new Random();
            
            for (int i = 0; i < randomSalesCount; i++) {
                // Generate random product IDs and quantities
                int productID = rand.nextInt(100) + 1;
                int quantity = rand.nextInt(10) + 1;
                
                // Write the sales data to the file
                writer.write("CC;" + id + "\n");
                writer.write(productID + ";" + quantity + "\n");
            }
            
            writer.close();
            System.out.println("Sales file for " + name + " created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a file with product information
    public static void createProductsFile(int productsCount) {
        try {
            // FileWriter object to write to a file
            FileWriter writer = new FileWriter("resources/products.txt");
            
            Random rand = new Random();
            
            for (int i = 0; i < productsCount; i++) {
                // Generate random product details
                int productID = i + 1;
                String productName = "Product" + productID;
                double price = (rand.nextInt(10000) + 100) / 100.0;
                
                // Write the product data to the file
                writer.write(productID + ";" + productName + ";" + price + "\n");
            }
            
            writer.close();
            System.out.println("Products file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a file with salesman information
    public static void createSalesManInfoFile(int salesmanCount) {
        try {
            // FileWriter object to write to a file
            FileWriter writer = new FileWriter("resources/salesmen.txt");
            
            Random rand = new Random();
            
            for (int i = 0; i < salesmanCount; i++) {
                // Generate random salesman details
                String idType = "CC";
                long idNumber = rand.nextLong(999999999L) + 100000000L;
                String firstName = "Name" + (i + 1);
                String lastName = "LastName" + (i + 1);
                
                // Write the salesman data to the file
                writer.write(idType + ";" + idNumber + ";" + firstName + ";" + lastName + "\n");
            }
            
            writer.close();
            System.out.println("Salesmen info file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}