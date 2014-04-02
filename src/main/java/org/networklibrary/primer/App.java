package org.networklibrary.primer;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
	{

		Options options = new Options();
		Option help = OptionBuilder.withDescription("Help message").create("help");
		Option dbop = OptionBuilder.withArgName("[URL]").hasArg().withDescription("Neo4j instance to prime").withLongOpt("target").withType(String.class).create("db");
		//    	Option type = OptionBuilder...

		options.addOption(help);
		options.addOption(dbop);

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse( options, args );

			if(line.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "netlibprimer [OPTIONS] [FILES]", options );
			}

			String db = null;
			if(line.hasOption("db")){
				db = line.getOptionValue("db");
			}

			List<String> inputFiles = line.getArgList();

			Primer p = new Primer(db,inputFiles);

			try {
				p.prime();
			} catch (IOException e) {
				System.err.println("parsing failed" + e.getMessage());
				e.printStackTrace();
			}

		}
		catch( ParseException exp ) {
			// oops, something went wrong
			System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
		}
	}
}
