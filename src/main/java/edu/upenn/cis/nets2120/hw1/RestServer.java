package edu.upenn.cis.nets2120.hw1;

import static org.mockito.Mockito.inOrder;
import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import edu.upenn.cis.nets2120.config.Config;
import edu.upenn.cis.nets2120.storage.DynamoConnector;

public class RestServer {

    private static DynamoDB dynamoDB;
    private static Table table;
    private static final String tableName = Config.TABLE_NAME;

    public static void main(String[] args) {
        port(4567);

        try {
            initializeDynamoDB();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        get("/", (request, response) -> {
            return "Hello world!";
        });

        get("/query/title/:titleValue", (request, response) -> {
            String titleValue = request.params(":titleValue");
            return queryByTitle(titleValue);
        });

        get("/query/genres", (request, response) -> {
            String[] genres = request.queryParams("value").split(",");
            return queryByGenres(genres);
        });

        get("/query/years", (request, response) -> {
            String startYear = request.queryParams("start");
            String endYear = request.queryParams("end");
            return queryByYearRange(startYear, endYear);
        });
    }

    private static void initializeDynamoDB() throws Exception {
        dynamoDB = DynamoConnector.getConnection(Config.DYNAMODB_URL);
        table = dynamoDB.getTable(tableName);
    }

    private static String queryByTitle(String titleValue) {

        String filterExpression = "contains(primaryTitle, :titleValue)";

        ValueMap valueMap = new ValueMap().withString(":titleValue", titleValue);

        ScanSpec scanSpec = new ScanSpec().withFilterExpression(filterExpression).withValueMap(valueMap);

        ItemCollection<ScanOutcome> items = table.scan(scanSpec);

        // List<String> jsonItems = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            sb.append(itemIterator.next().toJSON());
            if (itemIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String queryByGenres(String[] genres) {
        List<String> conditions = new ArrayList<>();
        ValueMap valueMap = new ValueMap();

        for (int i = 0; i < genres.length; i++) {
            String paramName = ":genre" + i;
            conditions.add("contains(genres, " + paramName + ")");
            valueMap.withString(paramName, genres[i]);
        }

        String filterExpression = String.join(" AND ", conditions);

        ScanSpec scanSpec = new ScanSpec().withFilterExpression(filterExpression).withValueMap(valueMap);

        ItemCollection<ScanOutcome> items = table.scan(scanSpec);

        // List<String> jsonItems = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            sb.append(itemIterator.next().toJSON());
            if (itemIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();

        /*
         * for (Item item : items) {
         * String jsonItem = item.toJSON();
         * jsonItems.add(jsonItem);
         * }
         */

        // return jsonItems;
    }

    private static String queryByYearRange(String startYear, String endYear) {
        System.out.println(startYear);
        System.out.println(endYear);
        ScanSpec scanSpec = new ScanSpec();

        if (endYear != null && !endYear.isEmpty()) {
            int startYearInt = Integer.parseInt(startYear);
            int endYearInt = Integer.parseInt(endYear);
            if (endYearInt < startYearInt) {
                return "[]";
            }
            System.out.println(endYear);
            scanSpec.withFilterExpression("startYear >= :startYear AND endYear <= :endYear")
                    .withValueMap(new ValueMap().withInt(":startYear", startYearInt).withInt(":endYear", endYearInt));
        } else {
            int startYearInt = Integer.parseInt(startYear);
            scanSpec.withFilterExpression("startYear = :startYear")
                    .withValueMap(new ValueMap().withInt(":startYear", startYearInt));
        }

        ItemCollection<ScanOutcome> items = (table.scan(scanSpec));

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()) {
            sb.append(itemIterator.next().toJSON());
            if (itemIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
