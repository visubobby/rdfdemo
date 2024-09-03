package com.graph.rdfdemo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class RdfToVisJsConverter {

    public JSONObject convertRdfToVisJs(Resource rdfResource, JSONObject visJsData) throws Exception {
        Model model = readRdf(rdfResource.getInputStream());

        JSONArray nodes = visJsData.getJSONArray("nodes");
        JSONArray edges = visJsData.getJSONArray("edges");
        Set<String> addedNodes = new HashSet<>();

        model.listStatements().forEachRemaining(statement -> {
            String subject = statement.getSubject().toString();
            String object = statement.getObject().toString();
            String predicate = statement.getPredicate().getLocalName();

            if (addedNodes.add(subject)) {
                JSONObject node = new JSONObject();
                node.put("id", subject);
                node.put("label", subject);
                nodes.put(node);
            }

            if (addedNodes.add(object)) {
                JSONObject node = new JSONObject();
                node.put("id", object);
                node.put("label", object);
                nodes.put(node);
            }

            JSONObject edge = new JSONObject();
            edge.put("from", subject);
            edge.put("to", object);
            edge.put("label", predicate);
            edges.put(edge);
        });

        return visJsData;
    }

    private Model readRdf(InputStream inputStream) {
        Model model = ModelFactory.createDefaultModel();
        model.read(inputStream, null, "TTL");
        return model;
    }
}

