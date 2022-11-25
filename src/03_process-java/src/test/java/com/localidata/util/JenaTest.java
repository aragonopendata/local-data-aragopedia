package com.localidata.util;

import junit.framework.TestCase;


public class JenaTest extends TestCase {


    public void testGetIneTeruel() throws Exception {
        String input = "Teruel";
        String expect = "44216";

        assertEquals(expect, Jena.getIne(input));
    }

    public void testGetUriTeruel() throws Exception {
        String input = "Teruel";
        String expect = "http://opendata.aragon.es/recurso/sector-publico/organizacion/municipio/44216";

        assertEquals(expect, Jena.getUri(input));
    }

}
