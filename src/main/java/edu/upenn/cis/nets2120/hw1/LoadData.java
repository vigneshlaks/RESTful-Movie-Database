package edu.upenn.cis.nets2120.hw1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import edu.upenn.cis.nets2120.config.Config;
import edu.upenn.cis.nets2120.hw1.files.Parser;
import edu.upenn.cis.nets2120.storage.DynamoConnector;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

/**
 * Data loader -- connect to DynamoDB, read IMDB Movies from file, load
 * into DynamoDB.
 */
public class LoadData {
	/**
	 * A logger is useful for writing different types of messages
	 * that can help with debugging and monitoring activity.  You create
	 * it and give it the associated class as a parameter -- so in the
	 * config file one can adjust what messages are sent for this class. 
	 */
	static Logger logger = LogManager.getLogger(LoadData.class);

	/**
	 * Connection to DynamoDB
	 */
	DynamoDB db;
	
	/**
	 * File reader
	 */
	BufferedReader movieReader;
	
	/**
	 * Parser for the IMDB Movie entries
	 */
	Parser reader;
	
	/**
	 * Handler for IMDB Movie entries, writes to index
	 */
	IndexMovies indexer;
	
	/**
	 * Path to CSV file
	 */
	final String path;
	
	/**
	 * Initialize with the default loader path
	 */
	public LoadData() {
		path = Config.IMDB_FILE_PATH;
		final File f = new File(path);
		
		if (!f.exists())
			throw new RuntimeException("Can't load without the title.basics.tsv file");
	}

	/**
	 * Initialize with manually specified loader path
	 * 
	 * @param path Path to title.basics.tsv
	 */
	public LoadData(final String path) {
		this.path = path;
		
		final File f = new File(path);
		if (!f.exists())
			throw new RuntimeException("Can't load without the title.basics.tsv file");
	}

	/**
	 * Initialize the database connection and open the file
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws DynamoDbException 
	 */
	public void initialize() throws IOException, DynamoDbException, InterruptedException {
		logger.info("Connecting to DynamoDB...");
		//System.out.println("URL:"+Config.DYNAMODB_URL);
		db = DynamoConnector.getConnection(Config.DYNAMODB_URL);
		logger.debug("Connected!");

		movieReader = new BufferedReader(new FileReader(Config.IMDB_FILE_PATH));
		reader = new Parser(movieReader);
		indexer = new IndexMovies(db);
	}

	/**
	 * Main functionality in the program: read and index talk descriptions,
	 * potentially error out
	 * 
	 * @throws IOException File read, network, and other errors
	 * @throws DynamoDbException DynamoDB is unhappy with something
	 * @throws InterruptedException User presses Ctrl-C
	 */
	public void run() throws IOException, DynamoDbException, InterruptedException {
		logger.info("Running");

		reader.readIMDBData(indexer);

		logger.info("*** Finished reading IMDB Movies! ***");
	}

	/**
	 * This function is used by JUnit tests to make sure we properly
	 * index content
	 * 
	 * @param str
	 */
	public void indexThisLine(final String[] csvRow, final String[] columnNames) {
		indexer.accept(csvRow, columnNames);
	}
	
	
	/**
	 * Graceful shutdown
	 */
	public void shutdown() {
		logger.info("Shutting down");
		try {
			movieReader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		DynamoConnector.shutdown();
	}

	public static void main(final String[] args) {
		final LoadData ld = new LoadData();
		try {
			ld.initialize();
			ld.run();
		} catch (final IOException ie) {
			logger.error("I/O error: ");
			ie.printStackTrace();
		} catch (final DynamoDbException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			ld.shutdown();
		}
	}
}
