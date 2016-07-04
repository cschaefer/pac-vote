package com.prodyna.pac.vote.util;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

public class GenerateSchema {

    public static void main(String[] args) {

        try {
            final Map<String, Object> properties = new HashMap<>();

            properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
            properties.put("javax.persistence.schema-generation.scripts.create-target", "target/create.sql");
            properties.put("javax.persistence.schema-generation.scripts.drop-target", "target/drop.sql");

            Persistence.generateSchema("script", properties);

            System.exit(0);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}