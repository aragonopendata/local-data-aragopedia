package com.localidata.util;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;

public class Jena {

    public static final String SPARQL_ENDPOINT = "https://opendata.aragon.es/sparql";

    public static String getIne(String municipality) throws Exception {

        String query = "select ?ine \n" +
                "where {\n" +
                " ?uri <http://purl.org/dc/elements/1.1/title> \"" + municipality + "\" .\n" +
                " ?uri <http://purl.org/dc/elements/1.1/identifier> ?ine\n" +
                "}";
        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet result = qe.execSelect();

        Literal ine = null;

        for (; result.hasNext(); ) {
            QuerySolution soln = result.nextSolution();
            ine = soln.getLiteral("ine");
        }
        if (ine == null)
            throw new Exception("No existe ine para " + municipality);
        return ine.toString();

    }

    public static String getUri(String municipality) throws Exception {

        String query = "select ?uri \n" +
                "where {\n" +
                " ?uri <http://purl.org/dc/elements/1.1/title> \"" + municipality + "\" .\n" +
                " ?uri <http://purl.org/dc/elements/1.1/identifier> ?ine\n" +
                "}";
        QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        ResultSet result = qe.execSelect();

        Resource ine = null;

        for (; result.hasNext(); ) {
            QuerySolution soln = result.nextSolution();
            ine = soln.getResource("uri");
        }
        if (ine == null)
            throw new Exception("No existe uri para " + municipality);
        return ine.toString();

    }
}
