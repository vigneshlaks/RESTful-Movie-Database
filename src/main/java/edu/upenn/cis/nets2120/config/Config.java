package edu.upenn.cis.nets2120.config;

/**
 * Global configuration for NETS 212 homeworks.
 * 
 * A better version of this would read a config file from the resources,
 * such as a YAML file. But our first version is designed to be simple
 * and minimal.
 * 
 * @author zives
 *
 */
public class Config {

	/**
	 * If we set up a local DynamoDB server, where does it listen?
	 */
	public static int DYNAMODB_LOCAL_PORT = 9000;

	/**
	 * This is the connection to the DynamoDB server. When testing with local
	 * dynamodb, use http://localhost:9000; When connecting with AWS, you should
	 * replace it
	 * with https://dynamodb.us-east-1.amazonaws.com.
	 */
	public static String DYNAMODB_URL = "https://dynamodb.us-east-1.amazonaws.com";
	// "https://dynamodb.us-east-1.amazonaws.com";

	/**
	 * Do we want to use the local DynamoDB instance or a remote one?
	 * 
	 * If we are local, performance is really slow - so you should switch
	 * to the real thing as soon as basic functionality is in place.
	 */
	public static Boolean LOCAL_DB = false;

	public static String IMDB_FILE_PATH = "data/title.basics.sfw.tsv";

	public static String TABLE_NAME = "movies";

	public static boolean IS_AUTOGRADER = false;

}
