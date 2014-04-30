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

		Option noindexOps = new Option("no_index",false,"index the data");
		Option noarrayOps = new Option("no_array",false,"enforces that no arrays are used as property values");
		Option labelOps = new Option("label",false,"provided values are used as labels for the node");
		Option nopropOps = new Option("no_prop",false,"values are not saved as properties (but as labels if <label> flag is set)");
		Option nonewOps = new Option("no_new_nodes",false,"unknown primary ids will NOT create new nodes");

		options.addOption(help);
		options.addOption(dbop);
		options.addOption(configOp);
		options.addOption(extraOps);

		options.addOption(noindexOps);
		options.addOption(noarrayOps);
		options.addOption(labelOps);
		options.addOption(nopropOps);
		options.addOption(nonewOps);

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

			boolean label = line.hasOption("label");
			boolean index = !line.hasOption("no_index");
			boolean array = !line.hasOption("no_array");
			boolean prop = !line.hasOption("no_prop");
			boolean noNew = line.hasOption("no_new_nodes");

			List<String> inputFiles = line.getArgList();

			ConfigManager confMgr = null;
			if(config != null){
				confMgr = new ConfigManager(config);
			}
			else {
				confMgr = new ConfigManager();
			}

			Primer p = new Primer(db,confMgr,inputFiles,extras,index,array,noNew,label);

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
