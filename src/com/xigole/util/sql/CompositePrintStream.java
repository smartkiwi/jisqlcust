package com.xigole.util.sql;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * PrintStreams collection - class to multiplex interactions with one stream 
 * into multiple different streams
 * 
 * @author vvlad
 *
 */
public class CompositePrintStream extends OutputStream {
	
	List<PrintStream> streamsList = new ArrayList<PrintStream>();
	
	/**
	 * Constructor - creates PrintStreams Collection with default stream - System.out
	 * 
	 */
	CompositePrintStream() {
		addStream(System.out);
	}

	/**
	 * Constructor - creates PrintStreams Collection with stream only
	 * @param stream PrintStream
	 */
	CompositePrintStream(PrintStream stream) {
		addStream(stream);
	}
	
	/**
	 * adds stream into collection
	 * 
	 * @param stream PrintStream
	 */
	public void addStream(PrintStream stream) {
		streamsList.add(stream);
	}
	
	/**
	 * print String str (without new line char) to all streams
	 * @param str String
	 */	
	public void print(String str) {
		for (PrintStream stream : streamsList) {
			stream.print(str);
		}		
	}

	/**
	 * println String str (with new line char) to all streams
	 * @param str String
	 */
	public void println(String str) {
		for (PrintStream stream : streamsList) {
			stream.println(str);
		}				
	}	

	/**
	 * println new line char to all streams
	 * @param str String
	 */
	public void println() {
		for (PrintStream stream : streamsList) {
			stream.println();
		}				
	}	
	
	
	/**
	 * Close all streams in collection
	 * 
	 */
	public void close() {
		for (PrintStream stream : streamsList) {
			stream.close();
		}				
	}

	//@Override
	public void write(int arg0) throws IOException {
		// TODO Auto-generated method stub
		//stub
		
	}

	public void print(char delimiter) {
		for (PrintStream stream : streamsList) {
			stream.print(delimiter);
		}		
	}	
	
	
}

