// Package declaration
package com.salesprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Class that represents a salesperson
class Salesperson {
    private String name;
    private String region;
    private double sales;
    private String name1;
    // Constructor
    public Salesperson(String name, String region, double sales) {
        this.name = name;
        this.region = region;
        this.sales = sales;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public double getSales() {
        return sales;
    }

    @Override
    public String toString() {
        return "Salesperson [Name=" + name + ", Region=" + region + ", Sales=" + sales + "]";
    }
}

// Class with utility methods for processing CSV files
public class SalesFileProcessor {

    /**
     * This method processes a CSV file containing sales information.
     * Each line of the file should contain the following columns: name, region, sales.
     * 
     * @param filePath Path to the CSV file
     * @return List of Salesperson objects
     * @throws IOException if an I/O error occurs reading the file
     */
    public static List<Salesperson> processSalesFile(String filePath) throws IOException {
        List<Salesperson> salespeople = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String name = values[0];
                String region = values[1];
                double sales = Double.parseDouble(values[2]);
                Salesperson salesperson = new Salesperson(name, region, sales);
                salespeople.add(salesperson);
            }
        }
        return salespeople;
    }

    // Main method to demonstrate the usage of processSalesFile
    public static void main(String[] args) {
        String filePath = "sales_data.csv"; // Replace with the actual path to your CSV file
        try {
            List<Salesperson> salespeople = processSalesFile(filePath);
            // Print out each salesperson's information
            for (Salesperson sp : salespeople) {
                System.out.println(sp);
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}
