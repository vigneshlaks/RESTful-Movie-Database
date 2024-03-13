import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.document.Item;

import edu.upenn.cis.nets2120.config.Config;
import edu.upenn.cis.nets2120.storage.DynamoConnector;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class SampleLoaderTest {
    private DynamoDB db;

    @Before
    public void setUp() {
        // Initialize connection to DynamoDB
        db = DynamoConnector.getConnection(Config.DYNAMODB_URL);
    }

    @Test
    public void sampleTest() {

        Table table = db.getTable(Config.TABLE_NAME);
        // Filter for items where startYear is 1945
        ScanSpec scanSpec = new ScanSpec().withFilterExpression("startYear = :year and isAdult = :adult")
                .withValueMap(new ValueMap().withNumber(":year", 1911)
                        .withBoolean(":adult", false));
        int count = 0;

        try {
            // Perform the scan with the specified filter
            for (Item item : table.scan(scanSpec)) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue("testMoviesnonAdultFrom1911", count == 8);
    }

    @Test
    public void testTvMiniSeriesDocumentary2003() {
        Table table = db.getTable(Config.TABLE_NAME);
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("titleType = :titleType AND contains(genres, :genre) AND startYear = :startYear")
                .withValueMap(new ValueMap().withString(":titleType", "tvMiniSeries")
                        .withString(":genre", "Documentary")
                        .withInt(":startYear", 2003));
        int count = 0;
        try {
            for (Item item : table.scan(scanSpec)) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue("testTvMiniSeriesDocumentary2003", count == 2);
    }

    @Test
    public void testDifferentTitles() {
        Table table = db.getTable(Config.TABLE_NAME);
        ScanSpec scanSpec = new ScanSpec().withFilterExpression("primaryTitle <> originalTitle");

        int count = 0;
        try {
            for (Item item : table.scan(scanSpec)) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Count is " + count);

        assertTrue("testDifferentTitles", count == 240);
    }

}
