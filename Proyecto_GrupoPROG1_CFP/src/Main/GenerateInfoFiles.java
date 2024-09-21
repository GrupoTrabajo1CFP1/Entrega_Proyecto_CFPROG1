package Main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateInfoFiles {

    // Main method to execute the file generation
    public static void main(String[] args) {
        List<Long> salesmanIds = createSalesManInfoFile(10);
        createProductsFile(20);
        createSalesMenFile(10, salesmanIds);
    }

    // Método para crear un archivo de ventas
    public static void createSalesMenFile(int randomSalesCount, List<Long> salesmanIds) {
        try (FileWriter writer = new FileWriter("resources/sales.txt")) {
            Random rand = new Random();

            // Write header to the sales file
            writer.write("ProductId;SalesmanId;Quantity\n");

            for (int i = 0; i < randomSalesCount; i++) {
                // Generar IDs de productos dentro del rango válido
                int productID = rand.nextInt(20) + 1; // Suponiendo que tienes 20 productos
                int quantity = rand.nextInt(10) + 1;

                // Elegir un SalesmanId aleatorio de la lista
                long salesmanId = salesmanIds.get(rand.nextInt(salesmanIds.size()));

                // Escribir los datos de venta en el archivo
                writer.write(productID + ";" + salesmanId + ";" + quantity + "\n");
            }

            System.out.println("Sales file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a file with product information
    public static void createProductsFile(int productsCount) {
        try {
            FileWriter writer = new FileWriter("resources/products.txt");
            writer.write("ID;Name;Price\n");

            for (int i = 0; i < productsCount; i++) {
                int productID = i + 1;
                String productName = "Product" + productID;
                double price = (Math.random() * 100 + 1); // Price between 1 and 100

                writer.write(productID + ";" + productName + ";" + price + "\n");
            }

            writer.close();
            System.out.println("Products file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create a file with salesman information and return their IDs
    public static List<Long> createSalesManInfoFile(int salesmanCount) {
        List<Long> salesmanIds = new ArrayList<>();
        try {
            FileWriter writer = new FileWriter("resources/salesmen.txt");
            writer.write("Code;ID;Name;LastName\n");

            Random rand = new Random();

            for (int i = 0; i < salesmanCount; i++) {
                String idType = "CC";
                long idNumber = 100000000 + rand.nextInt(900000000); // ID between 100000000 and 999999999
                String firstName = "Name" + (i + 1);
                String lastName = "LastName" + (i + 1);

                // Guardar el ID de vendedor
                salesmanIds.add(idNumber);

                writer.write(idType + ";" + idNumber + ";" + firstName + ";" + lastName + "\n");
            }

            writer.close();
            System.out.println("Salesmen info file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return salesmanIds; // Devolver la lista de IDs generados
    }
}