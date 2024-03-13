package edu.upenn.cis.nets2120.hw1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import edu.upenn.cis.nets2120.config.Config;
import edu.upenn.cis.nets2120.hw1.files.Parser.Handler;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class IndexMovies implements Handler {
    static Logger logger = LogManager.getLogger(Handler.class);

    DynamoDB db;
    Table moviesTable;

    public IndexMovies(final DynamoDB db) throws DynamoDbException, InterruptedException {
        this.db = db;
        initializeTables();
    }

    /* TODO: Implement this function */
    /**
     * Accept a row from the dataset and index it in DynamoDB
     * 
     * @param csvRow
     * @param columnNames
     */
    @Override
    public void accept(final String[] csvRow, final String[] columnNames) {
        if (csvRow.length != columnNames.length) {
            logger.error("Names and Values not same length");
            return;
        }

        Item item = new Item();
        item.withPrimaryKey(columnNames[0], csvRow[0]);

        for (int i = 1; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            String columnValue = csvRow[i];

            switch (columnName) {
                case "titleType":
                case "primaryTitle":
                case "originalTitle":
                    item = item.withString(columnName, columnValue);
                    break;
                case "isAdult":
                    boolean newVal;
                    if (columnValue.equals("0")) {
                        newVal = false;
                    } else {
                        newVal = true;
                    }
                    item = item.withBoolean(columnName, newVal);
                    break;
                case "startYear":
                case "endYear":
                case "runtimeMinutes":
                    if (columnValue.equals("\\N") || columnValue.equals("N")) {
                        item = item.withNull(columnName);
                    } else {
                        item = item.withNumber(columnName, Integer.parseInt(columnValue));
                    }
                    break;
                case "genres":
                    String[] genres = columnValue.split(",");
                    List<String> genreList = Arrays.asList(genres);
                    item = item.withList(columnName, genreList);
                    break;
                default:
                    logger.warn("impossible case");
                    break;
            }
        }

        try {
            moviesTable.putItem(item);
            logger.info("Item added");
        } catch (Exception e) {
            logger.error("Error adding item");
        }
    }

    /**
     * Initialize the DynamoDB table
     * 
     * @throws DynamoDbException
     * @throws InterruptedException
     * 
     */
    private void initializeTables() throws DynamoDbException, InterruptedException {
        long readCapacityUnits = 10;
        long writeCapacityUnits = 10;
        if (Config.IS_AUTOGRADER) {
            readCapacityUnits = 500;
            writeCapacityUnits = 500;
        }
        try {
            moviesTable = db.createTable(Config.TABLE_NAME,
                    Arrays.asList(new KeySchemaElement("tconst", KeyType.HASH)), // Primary key
                    Arrays.asList(new AttributeDefinition("tconst", ScalarAttributeType.S)),
                    new ProvisionedThroughput(readCapacityUnits, writeCapacityUnits));
            moviesTable.waitForActive();
        } catch (final ResourceInUseException exists) {
            moviesTable = db.getTable(Config.TABLE_NAME);
        }
    }
}
