package com.prodyna.pac.vote.service.monitoring;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Create an CSV-String from java pojos.
 *
 * @param <T> java model for row.
 */
public class CSVBuilder<T> {

    private String delimiter = ";";

    private final List<Tuple<String, Function<T, String>>> functions = new ArrayList<>();

    /**
     * create new Builder with ";" as delimiter.
     */
    public CSVBuilder() {
        this(";");
    }

    /**
     * @param delimiter for csv.
     */
    public CSVBuilder(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @param header headline for column.
     * @param f      function to create the String value for the column from java model.
     * @return the builder to add more columns.
     */
    public CSVBuilder<T> addColumn(String header, Function<T, String> f) {
        this.functions.add(new Tuple<>(header, f));
        return this;
    }

    /**
     * @param rows pojos for rows.
     * @return csv formatted String.
     */
    public String build(Collection<T> rows) {
        final StringBuilder sb = new StringBuilder();
        for (final Tuple<String, Function<T, String>> column : this.functions) {
            sb.append(column.getKey());
            sb.append(this.delimiter);
        }
        sb.append("\n");

        for (final T row : rows) {
            for (final Tuple<String, Function<T, String>> column : this.functions) {
                sb.append(column.getValue().apply(row));
                sb.append(this.delimiter);
            }
            sb.append("\n");
        }
        return sb.toString();
    }



}
