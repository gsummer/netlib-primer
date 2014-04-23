package org.networklibrary.primer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.networklibrary.core.config.ConfigManager;

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
		Option configOp = OptionBuilder.hasArg().withDescription("Alternative config file").withLongOpt("config").withType(String.class).create("c");
		Option extraOps = OptionBuilder.hasArgs().withDescription("Extra configuration parameters for the import").withType(String.class).create("x");

		options.addOption(help);
		options.addOption(dbop);
		options.addOption(configOp);
		options.addOption(extraOps);

		CommandLineParser parser = new GnuParser();
		try {
			CommandLine line = parser.parse( options, args );

			if(line.hasOption("help") || args.length == 0){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "netlibprimer [OPTIONS] [FILES]", options );
				return;
			}

			String db = null;
			if(line.hasOption("db")){
				db = line.getOptionValue("db");
			}
			
			String config = null;
            if(line.hasOption("c")){
            	config = line.getOptionValue("c");
            }
            
            List<String> extras = null;
            if(line.hasOption("x")){
            	extras = Arrays.asList(line.getOptionValues("x"));
            }

			List<String> inputFiles = line.getArgList();

			ConfigManager confMgr = null;
            if(config != null){
            	confMgr = new ConfigManager(config);
            }
            else {
            	confMgr = new ConfigManager();
            }
			
			Primer p = new Primer(db,confMgr,inputFiles,extras);

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
