package com.graph.rdfdemo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SaveGraphService {

    @Value("${graph.files.directory}")
    private String outputDirectory;
    public void saveGraph(StackTraceElement[] stackTrace) {
        try {
            // Process stack trace
            Model model = processStackTrace(stackTrace);
            Path outputPath = Paths.get(outputDirectory, "stacktraceVisu.rdf");
            File outputFile = outputPath.toFile();
            outputFile.getParentFile().mkdirs();

            // Write the RDF model to a file
            model.write(System.out, "TURTLE");
            model.write(new PrintWriter(outputFile), "TURTLE");
        } catch (Exception e) {
            System.out.println("saveGraph exception: " + e.getMessage());
        }
    }

    private Model processStackTrace(StackTraceElement[] stackTrace) {
        Model model = ModelFactory.createDefaultModel();
        String baseUri = "";

        for (StackTraceElement element : stackTrace) {
            // Extract relevant information from each element
            String packageName = element.getClassName().substring(0,element.getClassName().lastIndexOf("."));;
            String className = element.getClassName().substring(element.getClassName().lastIndexOf(".")+1);
            String methodName = element.getMethodName();
            int lineNumber = element.getLineNumber();

            // Example: Print the stack trace element details
            System.out.println("Package: " + packageName);
            System.out.println("Class: " + className);
            System.out.println("Method: " + methodName);
            System.out.println("Line Number: " + lineNumber);
            System.out.println("-----------------------------");
            // Example line: at com.example.MyClass.myMethod(MyClass.java:10)

            // Create class, method, and component nodes
            Resource classNode = model.createResource()
                    .addProperty(RDF.type, "Class")
                    .addLiteral(model.createProperty("className"), className)
                    .addLiteral(model.createProperty("methodName"), methodName)
                    .addLiteral(model.createProperty("lineNumber"), String.valueOf(lineNumber));


            Resource methodNode = model.createResource()
                    .addProperty(RDF.type, "Method")
                    .addLiteral(model.createProperty("methodName"), methodName)
                    .addLiteral(model.createProperty("lineNumber"), String.valueOf(lineNumber))
                    .addProperty(model.createProperty("belongsTo"), classNode);

            Resource componentNode = model.createResource()
                    .addProperty(RDF.type, "Component")
                    .addLiteral(model.createProperty("Component"), packageName);
                   // .addProperty(RDFS.label, packageName)
                   // .addProperty(RDFS.comment, "Component: " + packageName);

            // Link method to class and class to component
            classNode.addProperty(model.createProperty("memberOf"), componentNode);
        }
        return model;
    }
}
