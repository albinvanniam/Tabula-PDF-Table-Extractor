PDF Table Extractor
This project demonstrates how to extract table data from PDF files, clean it, and output it in different formats (CSV, TXT, and clean text). The code uses the Apache PDFBox library to process PDF files, applying custom logic to detect and extract table rows. The extracted data is then saved in CSV, tab-separated text, and clean text formats.


Project Description
The PDFTableExtractor program extracts tabular data from a given PDF file. It identifies rows containing numbers (likely corresponding to table data) and processes them into a cleaner format. The program also includes logic for handling headers, metadata, and common formatting issues found in PDF documents.

The extracted data is saved in multiple formats:

CSV: Comma-separated values, ideal for importing into spreadsheets or databases.

TXT: Tab-separated values, useful for simpler text processing.

Clean Text: Raw format with row-wise data, similar to the original content but without headers or footnotes.

Installation
1. Clone the Repository

git clone https://github.com/albinvanniam/Tabula-PDF-Table-Extractor
cd PDFTableExtractor

2. Set up Maven
Make sure you have Maven installed. Then, navigate to the project directory and run:


mvn clean install
This will download all the necessary dependencies, including Apache PDFBox.

3. Add Your PDF File
Place your PDF file inside the src/main/resources directory. Make sure the file name matches the one referenced in the code (e.g., testdata_industrieberichte.pdf).

Usage
1. Run the Program
In IntelliJ IDEA, right-click on PDFTableExtractor.java and select Run 'PDFTableExtractor.main()'.

Alternatively, you can run the program from the command line:


mvn exec:java -Dexec.mainClass="com.example.PDFTableExtractor"
2. Output
The program will extract the table data from the PDF and print it in a formatted form. The data will also be saved into the following files in the extracted_data/ directory:

table_data.csv

table_data.txt

table_data_clean.txt

The output in the console will look something like this:

python-repl
Copy
Formatted Extracted Table Data:

0 5 42 1 16 40 116 335 214 92 177 216
0 3 27 66 10 24 52 223 74 34 110 159
...
The files in the extracted_data folder will contain the same data in different formats:

CSV: Comma-separated values, ideal for use in spreadsheets.

TXT: Tab-separated data.

Clean Text: Plain text representation of the extracted rows.

Output Formats
1. CSV
The data will be saved in a CSV format where each row corresponds to a row in the table. The columns will be labeled Col1, Col2, Col3, ..., Col26.

Example CSV format:


Col1,Col2,Col3,Col4,Col5,Col6,Col7,Col8,Col9,Col10,Col11,Col12,Col13,Col14,Col15,Col16,Col17,Col18,Col19,Col20,Col21,Col22,Col23,Col24,Col25,Col26
0,5,42,1,16,40,116,335,214,92,177,216,...
2. TXT (Tab-Separated)
The data will also be saved in a tab-separated format.

Example TXT format:


Col1	Col2	Col3	Col4	Col5	Col6	Col7	Col8	Col9	Col10	Col11	Col12	Col13	Col14	Col15	Col16	Col17	Col18	Col19	Col20	Col21	Col22	Col23	Col24	Col25	Col26
0	5	42	1	16	40	116	335	214	92	177	216	...
3. Clean Text
The clean text file contains the extracted table data in its original form (minus headers and footnotes), but with each row displayed in a readable format.

Example Clean Text format:

sql
Copy
=== EXTRACTED TABLE DATA ===
Row 1: 0 5 42 1 16 40 116 335 214 92 177 216
Row 2: 0 3 27 66 10 24 52 223 74 34 110 159
...
