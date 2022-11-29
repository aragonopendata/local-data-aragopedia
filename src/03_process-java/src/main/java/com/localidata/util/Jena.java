package com.localidata.util;

import java.text.Normalizer;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

public class Jena {

    public static final String SPARQL_ENDPOINT = "https://opendata.aragon.es/sparql";

    private static String normalize(String input) {
        return input == null ? null : Normalizer.normalize(input, Normalizer.Form.NFKD);
    }

    public static String queryComarca(String name) {
        String nameNorm = normalize(name).replaceAll("\\p{M}", "");
        String query ="SELECT ?name ?ine ?uri \n" +
            "WHERE {\n" +
            "?uri <http://purl.org/dc/elements/1.1/title> ?name.\n" +
            "?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .\n" +
            "?uri <http://purl.org/dc/elements/1.1/identifier> ?ine.\n" +
            "FILTER regex( ?uri, \"" +"/comarca/\"" + ",\"" + "i\""+" )\n" +
            "FILTER (?name IN (\"" + name + "\"" +", \"" + nameNorm.toUpperCase() + "\""+", \"" + name.toLowerCase() + "\""+" ) )\n" +
            " }" ;

        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet results = qe.execSelect();
        Resource uri = null;

        for (; results.hasNext(); ) {
            QuerySolution result = results.nextSolution();
            uri = result.getResource("uri");
        }
        if (uri == null)
            return null;
        
        return uri.toString();

    }
    
    public static String queryMunicipio(String name) {
        String nameNorm = normalize(name).replaceAll("\\p{M}", "");
        String query ="SELECT ?name ?ine ?uri \n" +
            "WHERE {\n" +
            "?uri <http://purl.org/dc/elements/1.1/title> ?name.\n" +
            "?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .\n" +
            "?uri <http://purl.org/dc/elements/1.1/identifier> ?ine.\n" +
            "FILTER regex( ?uri, \"" +"/municipio/\"" + ",\"" + "i\""+" )\n" +
            "FILTER (?name IN (\"" + name + "\"" +", \"" + nameNorm.toUpperCase() + "\""+", \"" + name.toLowerCase() + "\""+" ) )\n" +
            " }" ;

        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet results = qe.execSelect();
        Resource uri = null;

        for (; results.hasNext(); ) {
            QuerySolution result = results.nextSolution();
            uri = result.getResource("uri");
        }
        if (uri == null)
            return null;
        
        return uri.toString();

    }

    public static String queryProvincia(String name) {
        String nameNorm = normalize(name).replaceAll("\\p{M}", "");
        String query ="SELECT ?name ?ine ?uri \n" +
            "WHERE {\n" +
            "?uri <http://purl.org/dc/elements/1.1/title> ?name.\n" +
            "?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .\n" +
            "?uri <http://purl.org/dc/elements/1.1/identifier> ?ine.\n" +
            "FILTER regex( ?uri, \"" +"/diputacion/\"" + ",\"" + "i\""+" )\n" +
            "FILTER regex( ?name, \"" + nameNorm.toUpperCase() + "\")\n" +
            " }" ;

        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet results = qe.execSelect();

        Resource uri = null;

        for (; results.hasNext(); ) {
            QuerySolution result = results.nextSolution();
            uri = result.getResource("uri");
        }
        if (uri == null)
            return null;
        
        return uri.toString();

    }

    public static String queryComunidad(String name) {
        String nameNorm = normalize(name).replaceAll("\\p{M}", "");
        String query ="SELECT ?name ?ine ?uri \n" +
            "WHERE {\n" +
            "?uri <http://purl.org/dc/elements/1.1/title> ?name.\n" +
            "?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/org#Organization> .\n" +
            "?uri <http://purl.org/dc/elements/1.1/identifier> ?ine.\n" +
            "FILTER regex( ?uri, \"" +"/comunidad/\"" + ",\"" + "i\""+" )\n" +
            "FILTER (?name IN (\"" + name + "\"" +", \"" + nameNorm.toUpperCase() + "\""+", \"" + name.toLowerCase() + "\""+" ) )\n" +
            " }" ;

        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet results = qe.execSelect();
        Resource uri = null;

        for (; results.hasNext(); ) {
            QuerySolution result = results.nextSolution();
            uri = result.getResource("uri");
        }
        if (uri == null)
            return null;
        
        return uri.toString();

    }
    
}