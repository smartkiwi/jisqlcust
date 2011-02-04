package com.xigole.util.sql.outputformatter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


/**
 * This is the default formatter for Jisql.  It outputs data in a &quot;normal&quot;
 * format that is similar to most other database command line formatters.
 *
 */
public class DefaultFormatterTwo implements JisqlFormatter {
    private boolean trimColumns = false;
    private int columnWidth = 0;
    private char spacer = ' ';
    private boolean printNull = true;
    private boolean leftJustify = false;
    private boolean printHeader = true;
    private boolean debug = false;
    
    private String outputfile = "outfile.log";
    private Boolean useroutputfile = false;
    private PrintStream secondStream;
    
    /**
     * opens BufferedWriter to file
     */
    public void openOutFile() {
        try {
        	System.out.println("using output file: "+outputfile);
        	FileOutputStream fwo = new FileOutputStream( outputfile, true );
        	BufferedOutputStream bwo = new BufferedOutputStream( fwo );
        	secondStream = new PrintStream( bwo );
        } catch( IOException ioexception )
        {
            ioexception.printStackTrace( );
        }
    }

    /**
     * flushes and closes BufferedWriter to file
     */
    public void closeOutFile() throws IOException {
    	secondStream.flush();
    	secondStream.close();
    }

    /**
     * writes message into out file
     * 
     * @param str - String message to write into file
     * 
     */
    public void writeToOut(String str) throws IOException
    {
    	if (useroutputfile)
    		secondStream.print(str);
    }

    
    public void writeToOutLn(String str) throws IOException
    {
    	if (useroutputfile)
    		secondStream.println(str);
    }

    
    
    /**
     * Sets second stream to be used for simultaneous output
     * 
     * @param ps PrintStream
     * 
     */
    

    
    /**
     * Sets a the option list for this formatter.  This formatter accepts the
     * following options:
     * 
     * <li><b>-noheader</b> do not print the header column info.</li>
     * <li><b>-spacer</b> The character to use for &quot;empty&quot; space.  This
     * defaults to the space character.  From mrider - &quot;I added the ability to
     * specify the spacer for columns - which used to be the single char ' '. I did
     * this because of brain-dead Windows' command line copy/paste. It seems that when
     * a line of text ends in space, copy does not copy that space. Which makes it
     * difficult to copy/paste into another program. This can probably be ignored
     * most of the time.&quot;</li>
     * <li><b>-delimiter</b> Specify a single character delimiter for columns.</li>
     * <li><b>-trim</b> trim the spaces from columns.</li>
     * <li><b>-nonull</b> print an empty string instead of the word &quot;NULL&quot;
     * when there is a null value.<li>
     * <li><b>-left</b> left justify the output</li>
     * <li><b>-debug</b> add debug headers to the output</li>
     * </ul>
     * 
     * @param parser the OptionParser to use.
     * 
     */
    public void setSupportedOptions( OptionParser parser ) {
        parser.accepts( "trim" );
        parser.accepts( "w" ).withRequiredArg().ofType( Integer.class );
        parser.accepts( "spacer" );
        parser.accepts( "left" );
        parser.accepts( "nonull" );
        parser.accepts( "noheader" );
        parser.accepts( "debug" );        
        parser.accepts("outfile").withRequiredArg().ofType(String.class);
    }

    /**
     * Consumes any options that were specified on the command line.
     *
     * @param options the OptionSet that the main driver is using.  
     *
     * @throws Exception if there is a problem parsing the command line arguments.
     *
     */
    public void consumeOptions( OptionSet options ) throws Exception {
        if( options.has( "trim" ) )
            trimColumns = true;

        if( options.has( "w" ) )
            columnWidth = Integer.parseInt( (String)options.valueOf( "w" ) );

        if( options.has( "spacer" ) )
            spacer = ((String)(options.valueOf( "spacer" ))).charAt( 0 );

        if( options.has( "left" ) )
            leftJustify = true;

        if( options.has( "nonull" ) )
            printNull = false;

        if( options.has( "noheader" ) )
            printHeader = true;
        
        if( options.has( "debug" ) )
            debug = true;
        
        if (options.has("outfile")) {
            useroutputfile = true;
        	outputfile = (String) options.valueOf("outfile");
        	openOutFile();
        }

        
    }
    


    /**
     * Called to output a usage message to the command line window.  This
     * message should contain information on how to call the formatter.
     *
     * @param out the stream to print the output on
     * 
     */
    public void usage( PrintStream out ) {
        out.println("\t-w specifies the maximum field width for a column.  The default is to output the full width of the column");
        out.println("\t-spacer changes the spacer between columns from a single space to the first character of the argument");
        out.println("\t-noheader do not print any header columns");
        out.println("\t-left left justify the output");
        out.println("\t-trim trim the data output.  This is useful when specifying a delimiter.");
        out.println("\t-nonull print the empty string instead of the word \"NULL\" for null values.");
        out.println("\t-debug shows extra information about the output." );
        out.println("\t-outfile <filename> output file to dump log" );

    }

    /**
     * Outputs a header for a query.  For the DefaultFormatter the data is output
     * by default unless the &quot;noheader&quot; option is specified.
     *
     * @param out - a PrintStream to send any output to.
     * @param metaData - the ResultSetMetaData for the output.
     *
     */
    public void formatHeader( PrintStream out, ResultSetMetaData metaData ) throws Exception {
        if( printHeader ) {
            int numColumns = metaData.getColumnCount();

            if( debug ) {
            	for (int i = 1; i <= numColumns; i++) {
            		String item = formatLabel( metaData.getColumnTypeName(i),metaData.getColumnDisplaySize(i));
                    out.print(item  );
                    out.print(" | ");
            		
                    writeToOut(item +" | ");
            	}
            }
            //
            // output the column names
            //
            for (int i = 1; i <= numColumns; i++) {
            	String item = formatLabel( metaData.getColumnName(i),
                        metaData.getColumnDisplaySize(i)); 
                out.print(item );
                out.print(" | ");
                
                writeToOut(item +" | ");
            }

            out.println();
            writeToOutLn("");

            //
            // output pretty dividers
            //
            for (int i = 1; i <= numColumns; i++) {
            	String item = formatSeparator( metaData.getColumnName(i),
                        metaData.getColumnDisplaySize(i));
                out.print( item );
                writeToOut(item );
                if (i == numColumns) {
                    out.print("-|");
                    writeToOut("-|");
                } else {
                    out.print("-+-");
                    writeToOut("-+-");
                }
            }

            out.println();
            writeToOutLn("");
        }
    }


    /**
     * Called to output the data.
     *
     * @param out the PrintStream to output data to.
     * @param resultSet the ResultSet for the row.
     * @param metaData the ResultSetMetaData for the row.
     *
     *
     */
    public void formatData( PrintStream out, ResultSet resultSet, ResultSetMetaData metaData ) throws Exception {

        while( resultSet.next() ) {
            int numColumns = metaData.getColumnCount();

            for (int i = 1; i <= numColumns; i++) {
            	String item = formatValue( metaData.getColumnName(i),
                        resultSet.getString(i),
                        metaData.getColumnDisplaySize(i));
            	
                out.print(item + " | ");
                writeToOut(item + " | ");
                
            }

            out.println();
            writeToOutLn("");
        }
    }


    /**
     * Outputs a footer for a query.  This is called after all data has been
     * exhausted.  This method isn't used in the DefaultFormatter.
     *
     * @param out the PrintStream to output data to.
     * @param metaData the ResultSetMetaData for the output.
     *
     */
    public void formatFooter( PrintStream out, ResultSetMetaData metaData ) throws Exception {
    }



    /**
     * Formats a label for output.
     * 
     * @param s - the label to format
     * @param width - the width of the field
     * 
     * @return the formated label
     * 
     */
	private String formatLabel(String s, int width) {
		if (s == null)
			s = "NULL";

		if (columnWidth != 0) {
			if (width > columnWidth)
				width = columnWidth;
		}

		if (width < s.length())
			width = s.length();

		int len = s.length();

		if (len >= width)
			return s.substring(0, width);

		int fillWidth = width - len;
		StringBuffer fill = new StringBuffer(fillWidth);
		for (int i = 0; i < fillWidth; ++i)
			fill.append(spacer);
		if (leftJustify)
			return s + fill;
		else if (s.startsWith("-"))
			return "-" + fill + s.substring(1);
		else
			return fill + s;
	}

	/**
	 * Formats a separator for display.
	 * 
	 * @param s - the field for which the separator is being generated
	 * @param width - the width of the field
	 * 
	 * @return the formated separator
	 * 
	 */
	private String formatSeparator(String s, int width) {
	    s = "NULL";

		if (columnWidth != 0) {
			if (width > columnWidth)
				width = columnWidth;
		}

		if (width < s.length())
			width = s.length();

		int len = s.length();

		if (len >= width)
			width = len;

		StringBuffer fill = new StringBuffer(width);
		for (int i = 0; i < width; ++i)
			fill.append('-');

        if( trimColumns )
		    return fill.toString().trim();
        else
		    return fill.toString();
	}

	/**
	 * Formats a value for display.
	 * 
	 * @param label the label associated with the value (for width purposes)
	 * @param s - the value to format
	 * @param width - the width of the field from the db.
	 * 
	 * @return the formatted field.
	 * 
	 */
	private String formatValue(String label, String s, int width) {
		if (s == null) {
            if( printNull )
			    s = "NULL";
            else
                s = "";
        }

		if (columnWidth != 0) {
			if (width > columnWidth)
				width = columnWidth;
		}

		if (width < label.length())
			width = label.length();

		int len = s.length();

		if (len >= width) {
                if( trimColumns )
			        return s.substring(0, width).trim();
                else
			        return s.substring(0, width);
        }

		int fillWidth = width - len;
		StringBuffer fill = new StringBuffer(fillWidth);
		for (int i = 0; i < fillWidth; ++i)
			fill.append(spacer);

        StringBuilder returnValue = new StringBuilder();

		if (leftJustify)
			returnValue.append( s + fill );
		else if (s.startsWith("-"))
			returnValue.append( "-" + fill + s.substring(1) );
		else {
		    returnValue.append( fill + s );
        }

        if( trimColumns ) {
            return returnValue.toString().trim();
        }
        else {
            return returnValue.toString();
        }
	}

	@Override
	public void formatString(PrintStream out, String str) throws Exception {
		writeToOutLn(str);
		out.println(str);
		
	}

	@Override
	public void doClose() throws Exception {
		if (useroutputfile)
			closeOutFile();		
	}
}
