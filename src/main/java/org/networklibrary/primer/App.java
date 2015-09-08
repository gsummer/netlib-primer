package org.networklibrary.primer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.networklibrary.core.config.ConfigManager;
import org.networklibrary.primer.config.PrimerConfigManager;

/**
 * Hello world!
 *
 */
public class App 
{
	protected static final Logger log = Logger.getLogger(App.class.getName());
	
	public static void main( String[] args )
	{
		Options options = new Options();
		Option help = OptionBuilder.withDescription("Help message").create("help");
		Option dbop = OptionBuilder.withArgName("[URL]").hasArg().withDescription("Neo4j instance to prime").withLongOpt("target").withType(String.class).create("db");
		Option typeop = OptionBuilder.withArgName("[TYPE]").hasArg().withDescription("Types available:").withType(String.class).create("t");
		Option extraOps = OptionBuilder.hasArgs().withDescription("Extra configuration parameters for the import").withType(String.class).create("x");

		Option noindexOps = new Option("no_index",false,"index the data");
		Option noarrayOps = new Option("no_array",false,"enforces that no arrays are used as property values");
		Option labelOps = new Option("label",false,"provided values are used as labels for the node");
		Option nopropOps = new Option("no_prop",false,"values are not saved as properties (but as labels if <label> flag is set)");
		Option nonewOps = new Option("no_new_nodes",false,"unknown primary ids will NOT create new nodes");
		Option allowMultiOps = new Option("allow_multi",false, "allow for multiple nodes to be returned on searching");
		
		Option configOp = OptionBuilder.hasArg().withDescription("Alternative config file").withLongOpt("config").withType(String.class).create("c");
		Option dictionaryOp = OptionBuilder.hasArg().withDescription("Dictionary file to use").withLongOpt("dictionary").withType(String.class).create("d");
		
		
		options.addOption(help);
		options.addOption(dbop);
		options.addOption(typeop);
		options.addOption(extraOps);

		options.addOption(noindexOps);
		options.addOption(noarrayOps);
		options.addOption(labelOps);
		options.addOption(nopropOps);
		options.addOption(nonewOps);
		options.addOption(allowMultiOps);

		options.addOption(configOp);
		options.addOption(dictionaryOp);
		
		
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
			
			String dictionary = null;
			if(line.hasOption("d")){
				dictionary = line.getOptionValue("d");
			}

			List<String> extras = null;
			if(line.hasOption("x")){
				extras = Arrays.asList(line.getOptionValues("x"));
			}
			
			String type = "TAB";
			if(line.hasOption("t")){
				type = line.getOptionValue("t","TAB");
			}
			
			boolean label = line.hasOption("label");
			boolean index = !line.hasOption("no_index");
			boolean array = !line.hasOption("no_array");
			boolean prop = !line.hasOption("no_prop");
			boolean newNodes = !line.hasOption("no_new_nodes");
			boolean allowMulti = line.hasOption("allow_multi");

			List<String> inputFiles = line.getArgList();

			if(config != null && !config.isEmpty()){
				log.info("user-supplied config file used: " + config);
			}
			
			PrimerConfigManager confMgr = new PrimerConfigManager(config,dictionary,type,label,index,array,prop,newNodes,allowMulti);
//			#confMgr.dumpConfig();
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
