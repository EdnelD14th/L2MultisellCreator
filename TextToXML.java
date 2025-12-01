import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextToXML {

    public static void main(String[] args) {
        try {
            // Path to the input text file with the ID data
            String inputFilePath = "input.txt";  // Change this to the path of your text file
            // File path with the names of the items and their IDs
            String namesFilePath = "item_names.txt";  // Change this to the file path with the names

            // Crear el documento XML
            Document document = createXMLDocument(inputFilePath, namesFilePath);

            // Convert the XML document to a file
            String outputXMLPath = "output.xml";
            transformToXML(document, outputXMLPath);

            System.out.println("XML successfully generated in: " + outputXMLPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read the text file and create an XML document
    public static Document createXMLDocument(String inputFilePath, String namesFilePath) throws Exception {
        // Create the XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Create the root element <list> and add the necessary attributes
        Element root = document.createElement("list");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:noNamespaceSchemaLocation", "../xsd/multisell.xsd");
        document.appendChild(root);

        // Load the item names from the file namesFilePath
        Map<String, String[]> itemNames = loadItemNames(namesFilePath);

        // Read the text file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process each line of the file
                String[] parts = line.split("\t"); // Separate with tabs

                // Create the <item> element
                Element item = document.createElement("item");
                root.appendChild(item);

                // Get the item name and the additional name using the Item ID
                String ingredientId = "57";
                String[] ingredientItemNameInfo = itemNames.get(ingredientId);

                if (ingredientItemNameInfo != null) {
                    String itemName = ingredientItemNameInfo[0]; // The name of the item
                    String additionalName = ingredientItemNameInfo[1]; // The additional name

                    // Generate the comment for the XML
                    String commentText = additionalName.isEmpty() ?
                        itemName : itemName + " - " + additionalName;

                    // Create a comment with the name and additional name
                    Comment comment = document.createComment(" " + commentText + " ");
                    
                    // Insert the comment after production (or ingredient)
                    item.appendChild(comment);
                }

                // Create the <ingredient> element and add it to the item
                Element ingredient = document.createElement("ingredient");
                ingredient.setAttribute("count", "1");  // We use a fixed value of 1 for the count
                ingredient.setAttribute("id", "57");  // We use the third value (index 2) as the id
                item.appendChild(ingredient);

                // Get the item name and the additional name using the ID from the third column
                String productionId = parts[2];
                String[] productionItemNameInfo = itemNames.get(productionId);

                if (productionItemNameInfo != null) {
                    String itemName = productionItemNameInfo[0]; // The name of the item
                    String additionalName = productionItemNameInfo[1]; // The additional name

                    // Generate the comment for the XML
                    String commentText = additionalName.isEmpty() ?
                        itemName : itemName + " - " + additionalName;

                    // Create a comment with the name and additional name
                    Comment comment = document.createComment(" " + commentText + " ");
                    
                    // Insert the comment after production (or ingredient)
                    item.appendChild(comment);
                }

                // Create the <production> element and add it to the item
                Element production = document.createElement("production");
                production.setAttribute("count", "1");  // We use a fixed value of 1 for the count
                production.setAttribute("id", parts[2]);  // We use the third value (index 2) as the id
                item.appendChild(production);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error reading the text file.");
        }

        return document;
    }

    // Load the item names from the file
    public static Map<String, String[]> loadItemNames(String namesFilePath) throws IOException {
        Map<String, String[]> itemNames = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(namesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process the line to extract the ID, the name, and the additional name
                String[] parts = line.split("\t");
                String id = parts[1].split("=")[1];  // Get the ID
                String name = parts[2].split("=")[1].replace("[", "").replace("]", "");  // Get the name
                String additionalName = parts[3].split("=")[1].replace("[", "").replace("]", "");  // Get the additional name
                itemNames.put(id, new String[]{name, additionalName});  // Save the id, name, and additional name in the map
            }
        }
        return itemNames;
    }

    // Transform the XML document and save it to a file
    public static void transformToXML(Document document, String outputXMLPath) throws Exception {
        // Use Transformer to convert the document into XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Set the output properties (for example, for indentation)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // Create a DOM source for the XML document
        DOMSource source = new DOMSource(document);

        // Create an output result (file where the XML will be saved)
        StreamResult result = new StreamResult(new File(outputXMLPath));

        // Perform the transformation and save the XML
        transformer.transform(source, result);
    }
}

