package com.graph.rdfdemo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping("/rdf/graph")
public class RDFGraphController {

    @Autowired
    private RDFFileResourceService rdfFileResourceService;

    @Autowired
    private SaveGraphService saveGraphService;

    @Autowired
    private RdfToVisJsConverter rdfToVisJsConverter;



    @GetMapping("/display")
    public String displayGraph() {
        try {
            // Get all RDF files as resources from the specified directory
            List<Resource> filesAsResources = rdfFileResourceService.getFilesAsResources();

            // Create a new JSONObject to hold the combined Vis.js data
            JSONObject visJsData = new JSONObject();
            visJsData.put("nodes", new JSONArray());
            visJsData.put("edges", new JSONArray());

            // Process each RDF file
            filesAsResources.forEach(rdfFileResource -> {
                try {
                    rdfToVisJsConverter.convertRdfToVisJs(rdfFileResource, visJsData);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing file: " + rdfFileResource.getFilename(), e);
                }
            });

            // Return the combined Vis.js data as a JSON string
            return visJsData.toString();

        } catch (Exception e) {
            // Handle any errors that occur during processing
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/load")
    public void loadGraph(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        saveGraphService.saveGraph(stackTrace);
    }

}

