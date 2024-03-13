package edu.upenn.cis.nets2120.hw1.files;

import java.io.IOException;
import java.io.Reader;
import java.util.function.BiConsumer;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVParserBuilder;
import com.opencsv.exceptions.CsvValidationException;

import edu.upenn.cis.nets2120.config.Config;

public class Parser {
	
	public static interface Handler extends BiConsumer<String[], String[]> {
		
	};
	
	CSVReader reader;
	String[] headerLine;
	
	/**
	 * Initialize a reader for the CSV file
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public Parser(Reader reader) throws IOException {
		System.setProperty("file.encoding", "UTF-8");
		final CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();

		this.reader = new CSVReaderBuilder(reader).withCSVParser(parser).build(); 
		
		try {

			headerLine = this.reader.readNext();
			
		} catch (CsvValidationException e) {
			// This should never happen but Java thinks it could
			e.printStackTrace();
		}
	}

	/**
	 * Read talks, one at a time, from the input file.  Call
	 * the processTalk handler if the line is OK, or processError if
	 * the line isn't parseable.
	 * 
	 * @param processTalk Function that takes an array of info about the talk, plus
	 * a (parallel) array of column names.
	 * 
	 * @throws IOException I/O error reading the file.
	 */
	public void readIMDBData(BiConsumer<String[], String[]> processTalk) throws IOException {
		String [] nextLine;
		try {
			while ((nextLine = reader.readNext()) != null) {
				// nextLine[] is an array of values from the line
				// headerLine is the array of column names

				processTalk.accept(nextLine, headerLine);

			}
			
		} catch (CsvValidationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the reader
	 */
	public void shutdown() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}