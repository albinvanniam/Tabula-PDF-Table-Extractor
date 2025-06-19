package com.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class PDFTableExtractor {

    public static void main(String[] args) {
        try {
            // Load the PDF document
            File file = new File("src/main/resources/testdata_industrieberichte.pdf");
            PDDocument document = PDDocument.load(file);

            // Initialize the PDFTextStripper to extract text
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            // Extract text from the first two pages
            stripper.setStartPage(1);
            stripper.setEndPage(2);
            String text = stripper.getText(document);

            // Print raw extracted text for debugging
            System.out.println("=== RAW EXTRACTED TEXT ===");
            System.out.println(text);
            System.out.println("=== END RAW TEXT ===\n");

            // Process the extracted text to find table data
            String[] lines = text.split("\n");
            List<String> tableRows = new ArrayList<>();

            // Pattern to identify lines that likely contain table data
            // This pattern looks for lines with multiple numbers separated by spaces or tabs
            Pattern tableRowPattern = Pattern.compile(".*\\d+.*\\s+\\d+.*");

            System.out.println("=== PROCESSING LINES ===");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                System.out.println("Line " + i + ": " + line);

                // Check if line contains multiple numbers (likely a table row)
                if (hasMultipleNumbers(line)) {
                    System.out.println("  -> Identified as table row");
                    tableRows.add(line);
                } else {
                    System.out.println("  -> Skipped (not a table row)");
                }
            }

            // Clean and filter the table data
            List<String> cleanedTableRows = cleanTableData(tableRows);

            // Print the extracted table data
            System.out.println("\n=== EXTRACTED TABLE DATA ===");
            if (cleanedTableRows.isEmpty()) {
                System.out.println("No table rows found. The PDF structure might be different than expected.");
                System.out.println("Please check the raw text above to understand the format.");
            } else {
                for (int i = 0; i < cleanedTableRows.size(); i++) {
                    System.out.println("Row " + (i + 1) + ": " + cleanedTableRows.get(i));
                }

                // Save to files
                saveToFiles(cleanedTableRows);
            }

            // Alternative approach: Try to extract based on column positions
            System.out.println("\n=== ALTERNATIVE: COLUMN-BASED EXTRACTION ===");
            extractByColumnPositions(lines);

            document.close();

        } catch (IOException e) {
            System.err.println("Error processing PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save extracted table data to files
     */
    private static void saveToFiles(List<String> tableRows) {
        try {
            // Create output directory if it doesn't exist
            File outputDir = new File("extracted_data");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Save as CSV
            saveAsCSV(tableRows, "extracted_data/table_data.csv");

            // Save as TXT (tab-separated)
            saveAsTXT(tableRows, "extracted_data/table_data.txt");

            // Save as clean text (space-separated, original format)
            saveAsCleanText(tableRows, "extracted_data/table_data_clean.txt");

        } catch (IOException e) {
            System.err.println("Error saving files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save table data as CSV format
     */
    private static void saveAsCSV(List<String> tableRows, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write CSV header (columns 1-26)
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= 26; i++) {
                header.append("Col").append(i);
                if (i < 26) header.append(",");
            }
            writer.write(header.toString());
            writer.newLine();

            // Write data rows
            for (String row : tableRows) {
                String csvRow = convertToCSV(row);
                writer.write(csvRow);
                writer.newLine();
            }
        }
        System.out.println("✓ Saved CSV file: " + filename);
    }

    /**
     * Save table data as tab-separated text
     */
    private static void saveAsTXT(List<String> tableRows, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= 26; i++) {
                header.append("Col").append(i);
                if (i < 26) header.append("\t");
            }
            writer.write(header.toString());
            writer.newLine();

            // Write data rows
            for (String row : tableRows) {
                String txtRow = convertToTabSeparated(row);
                writer.write(txtRow);
                writer.newLine();
            }
        }
        System.out.println("✓ Saved TXT file: " + filename);
    }

    /**
     * Save table data as clean text (original format)
     */
    private static void saveAsCleanText(List<String> tableRows, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("=== EXTRACTED TABLE DATA ===");
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < tableRows.size(); i++) {
                writer.write("Row " + (i + 1) + ": " + tableRows.get(i));
                writer.newLine();
            }
        }
        System.out.println("✓ Saved clean text file: " + filename);
    }

    /**
     * Convert a row to CSV format
     */
    private static String convertToCSV(String row) {
        // Split the row into parts and handle city names with spaces
        String[] parts = row.trim().split("\\s+");
        List<String> csvParts = new ArrayList<>();

        // First part is usually the row number, second might be city name
        StringBuilder cityName = new StringBuilder();
        boolean collectingCity = false;
        int numberCount = 0;

        for (String part : parts) {
            // Check if this part is a number
            if (isNumeric(part) || part.equals("-") || part.equals("....")) {
                if (collectingCity && cityName.length() > 0) {
                    // Finished collecting city name
                    csvParts.add("\"" + cityName.toString().trim() + "\"");
                    collectingCity = false;
                    cityName = new StringBuilder();
                }
                csvParts.add(part);
                numberCount++;
            } else {
                // This is likely part of a city name
                if (cityName.length() > 0) {
                    cityName.append(" ");
                }
                cityName.append(part);
                collectingCity = true;
            }
        }

        // Handle any remaining city name
        if (collectingCity && cityName.length() > 0) {
            csvParts.add("\"" + cityName.toString().trim() + "\"");
        }

        return String.join(",", csvParts);
    }

    /**
     * Convert a row to tab-separated format
     */
    private static String convertToTabSeparated(String row) {
        // Similar to CSV but use tabs
        String csvRow = convertToCSV(row);
        return csvRow.replace(",", "\t").replace("\"", "");
    }

    /**
     * Check if a string is numeric
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Clean the extracted table data by removing headers and unwanted rows
     */
    private static List<String> cleanTableData(List<String> rawTableRows) {
        List<String> cleanedRows = new ArrayList<>();

        for (String row : rawTableRows) {
            // Skip rows that contain obvious headers or metadata
            if (isHeaderOrMetadata(row)) {
                System.out.println("  -> Skipping header/metadata: " + row);
                continue;
            }

            // Skip column number headers (like "1 2 3 4 5 6 7 8 9 10 11 12 13")
            if (isColumnHeader(row)) {
                System.out.println("  -> Skipping column header: " + row);
                continue;
            }

            cleanedRows.add(row);
        }

        return cleanedRows;
    }

    /**
     * Check if a row contains header or metadata information
     */
    private static boolean isHeaderOrMetadata(String row) {
        // Look for common header patterns
        String lowerRow = row.toLowerCase();

        // Skip rows with underscores (often used in headers)
        if (row.contains("___") || row.contains("---")) {
            return true;
        }

        // Skip rows that look like encoded text or special characters
        if (row.contains("_r") && row.contains("_d_u_s_t")) {
            return true;
        }

        // Skip rows that are mostly special characters
        long specialCharCount = row.chars().filter(c -> !Character.isLetterOrDigit(c) && c != ' ').count();
        if (specialCharCount > row.length() * 0.5) {
            return true;
        }

        return false;
    }

    /**
     * Check if a row is a column header (sequential numbers)
     */
    private static boolean isColumnHeader(String row) {
        // Check if the row is just sequential numbers (like "1 2 3 4 5 6 7 8 9 10 11 12 13")
        String[] parts = row.trim().split("\\s+");

        // If it has many parts and they're all single/double digit numbers in sequence
        if (parts.length >= 5) {
            boolean isSequential = true;
            for (int i = 0; i < Math.min(parts.length, 13); i++) {
                try {
                    int num = Integer.parseInt(parts[i]);
                    if (num != i + 1) {
                        isSequential = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    isSequential = false;
                    break;
                }
            }
            if (isSequential) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a line contains multiple numbers (indicating it's likely a table row)
     */
    private static boolean hasMultipleNumbers(String line) {
        // Count sequences of digits
        int numberCount = 0;
        boolean inNumber = false;

        for (char c : line.toCharArray()) {
            if (Character.isDigit(c)) {
                if (!inNumber) {
                    numberCount++;
                    inNumber = true;
                }
            } else {
                inNumber = false;
            }
        }

        // Consider it a table row if it has at least 3 numbers
        return numberCount >= 3;
    }

    /**
     * Alternative extraction method based on column positions
     */
    private static void extractByColumnPositions(String[] lines) {
        List<String> potentialTableLines = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            // Skip empty lines and lines that are too short
            if (line.length() < 10) {
                continue;
            }

            // Look for lines with a good distribution of characters (suggesting columnar data)
            if (hasColumnStructure(line)) {
                potentialTableLines.add(line);
            }
        }

        if (potentialTableLines.isEmpty()) {
            System.out.println("No columnar data found.");
        } else {
            System.out.println("Found " + potentialTableLines.size() + " potential table lines:");
            for (int i = 0; i < potentialTableLines.size(); i++) {
                System.out.println("Line " + (i + 1) + ": " + potentialTableLines.get(i));
            }
        }
    }

    /**
     * Check if a line has a column-like structure
     */
    private static boolean hasColumnStructure(String line) {
        // Look for patterns that suggest columnar data:
        // - Multiple spaces between content (indicating columns)
        // - Mix of numbers and possible text
        // - Reasonable length

        String[] parts = line.split("\\s{2,}"); // Split on 2+ spaces
        return parts.length >= 3 && line.matches(".*\\d.*");
    }
}