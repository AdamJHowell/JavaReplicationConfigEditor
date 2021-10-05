package com.faircom.replicationconfigeditor;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Started at 2021-07-27 at 09:55.
 * Pause at 11:25
 * Resume at 11:40
 * I think that I am done at 12:32.
 * Confirmed that the JAR file works at the Windows command line at 12:37.
 * Refactored individual configuration update functions into a single generic function at 12:57
 * This will enable the program to keep track of all ports, and warn of conflicts.
 */
public class Main
{
	private static final Logger mainLogger = Logger.getLogger( Main.class.getName() );
	private static final ConsoleHandler singleLine = new ConsoleHandler();
	private static final ConsoleHandler tripleLine = new ConsoleHandler();
	private static final String FILE_SEP = System.getProperty( "file.separator" );
	private static final String BUILD_TIME = "main() - build 2021-09-30 1742";
	private static final String UPDATING = "Updating ";
	private static final String UNABLE_TO_UPDATE = "Unable to update \"";


	public static void main( String[] args )
	{
		mainLogger.setUseParentHandlers( false );
		mainLogger.addHandler( singleLine );

		String logString = "Welcome to Java Replication Config Editor!";
		mainLogger.log( Level.INFO, logString );
		mainLogger.log( Level.INFO, BUILD_TIME );

		String configFileName = "config.json";
		if( args.length > 0 )
		{
			configFileName = args[0];
		}
		if( validateConfigFileName( configFileName ) )
		{
			Config[] configArray = loadConfig( configFileName );
			if( configArray.length == 0 )
			{
				exiting( "Unable to parse the JSON in \"" + configFileName + "\" into an Array of Config class objects.", -2 );
			}
			// Parse all configured nodes.
			for( Config configuredNode : configArray )
			{
				updateFiles( configuredNode );
			}
		}
		else
		{
			exiting( "Cannot locate the configuration file!", -3 );
		}
	} // End of main() method.


	/**
	 * updateFiles() will take a Config class object and change the files it has configured.
	 *
	 * @param configuredNode the Config class object to process.
	 * @see com.faircom.replicationconfigeditor.Config
	 */
	public static void updateFiles( Config configuredNode )
	{
		String logString = "updateFiles()";
		mainLogger.log( Level.FINE, logString );
		String configDirectory = configuredNode.getBaseDirectory() + FILE_SEP + configuredNode.getConfigDirectory();

		// ctsrvr.cfg section.
		String serverConfigFileName = configDirectory + FILE_SEP + configuredNode.getServerFileName();
		Map<String, Object> serverConfigMap = buildServerConfigMap( configuredNode );
		// Add a few blank lines before this next log.
		singleToTriple();
		logString = UPDATING + serverConfigFileName;
		mainLogger.log( Level.INFO, logString );
		// Revert to single line logging.
		tripleToSingle();
		if( updateConfig( serverConfigFileName, serverConfigMap, false ) )
		{
			// Remove comments from lines that load required plugins.
			clearComment( serverConfigFileName, "cthttpd." );
			clearComment( serverConfigFileName, "ctagent." );
		}
		else
			exiting( UNABLE_TO_UPDATE + serverConfigFileName + "\"", -4 );

		// cthttpd.json configuration section.
		String httpConfigFileName = configDirectory + FILE_SEP + configuredNode.getHttpFileName();
		Map<String, Object> httpConfigMap = buildHTTPConfigMap( configuredNode );
		logString = UPDATING + httpConfigFileName;
		mainLogger.log( Level.INFO, logString );
		if( !updateConfig( httpConfigFileName, httpConfigMap, false ) )
			exiting( UNABLE_TO_UPDATE + httpConfigFileName + "\"", -4 );

		// ctagent.json configuration section.
		String agentConfigFileName = configDirectory + FILE_SEP + configuredNode.getAgentFileName();
		Map<String, Object> agentConfigMap = buildAgentConfigMap( configuredNode );
		logString = UPDATING + agentConfigFileName;
		mainLogger.log( Level.INFO, logString );
		if( !updateConfig( agentConfigFileName, agentConfigMap, false ) )
			exiting( UNABLE_TO_UPDATE + agentConfigFileName + "\"", -4 );

		// Replication Manager is the only node that has ctReplicationManager.cfg.
		if( !configuredNode.getReplicationManagerFileName().isEmpty() )
		{
			String replicationManagerConfigFileName = configDirectory + FILE_SEP + configuredNode.getReplicationManagerFileName();
			Map<String, Object> replicationManagerConfigMap = new HashMap<String, Object>(){ };
			// Note that this uses the same port configured in the agent.json section.
			replicationManagerConfigMap.put( "MEMPHIS_SQL_PORT", configuredNode.getMemphisSqlPort().toString() );
			logString = UPDATING + replicationManagerConfigFileName;
			mainLogger.log( Level.INFO, logString );
			if( !updateConfig( replicationManagerConfigFileName, replicationManagerConfigMap, true ) )
				exiting( UNABLE_TO_UPDATE + replicationManagerConfigFileName + "\"", -4 );
		}
	} // End of updateFiles() method.


	/**
	 * buildServerConfigMap() builds a Map of values from the contents of the Config class.
	 *
	 * @param config the configuration read in from file.
	 * @return a Map that contains the keys and values from the configuration file.
	 */
	public static Map<String, Object> buildServerConfigMap( Config config )
	{
		String logString = "buildServerConfigMap()";
		mainLogger.log( Level.FINE, logString );
		Map<String, Object> serverConfigMap = new HashMap<String, Object>(){ };
		serverConfigMap.put( "SERVER_NAME", config.getServerName() );
		serverConfigMap.put( "SERVER_PORT", config.getServerPort() );
		serverConfigMap.put( "READONLY_SERVER", config.getReadOnlyServer() );
		serverConfigMap.put( "SQL_PORT", config.getSqlPort() );
		return serverConfigMap;
	} // End of buildServerConfigMap() method.


	/**
	 * buildHTTPConfigMap() builds a Map of values from the contents of the Config class.
	 *
	 * @param config the configuration read in from file.
	 * @return a Map that contains the keys and values from the configuration file.
	 */
	public static Map<String, Object> buildHTTPConfigMap( Config config )
	{
		String logString = "buildHTTPConfigMap()";
		mainLogger.log( Level.FINE, logString );
		Map<String, Object> httpConfigMap = new HashMap<String, Object>(){ };
		httpConfigMap.put( "\"listening_http_port\":", config.getListeningHttpPort().toString() );
		httpConfigMap.put( "\"listening_https_port\":", config.getListeningHttpsPort().toString() );
		// Replication Manager does not have settings for MQTT ports, so Gson will set these to null if they are absent from the configuration file.
		if( config.getMqttListeningPort() != null )
			httpConfigMap.put( "\"mqtt_listening_port\":", config.getMqttListeningPort().toString() );
		if( config.getMqttWebsocketPort() != null )
			httpConfigMap.put( "\"mqtt_websocket_port\":", config.getMqttWebsocketPort().toString() );
		return httpConfigMap;
	} // End of buildHTTPConfigMap() method.


	/**
	 * buildAgentConfigMap() builds a Map of values from the contents of the Config class.
	 *
	 * @param config the configuration read in from file.
	 * @return a Map that contains the keys and values from the configuration file.
	 */
	public static Map<String, Object> buildAgentConfigMap( Config config )
	{
		String logString = "buildAgentConfigMap()";
		mainLogger.log( Level.FINE, logString );
		Map<String, Object> agentConfigMap = new HashMap<String, Object>(){ };
		agentConfigMap.put( "\"memphis_server_name\":", config.getMemphisServerName() );
		agentConfigMap.put( "\"memphis_sql_port\":", config.getMemphisSqlPort().toString() );
		agentConfigMap.put( "\"memphis_host\":", config.getMemphisHost() );
		agentConfigMap.put( "\"memphis_database\":", config.getMemphisDatabase() );
		return agentConfigMap;
	} // End of buildAgentConfigMap() method.


	/**
	 * updateConfig() will update a configuration file to properly set specific values.
	 * It does this by opening the file, parsing every line, and writing and updated file.
	 * Lines are updated by the fixLine() method.
	 *
	 * @param configFileName the file to open and parse.
	 * @param configMap      a map containing keys to search for and values to append to those keys.
	 * @param startsWith     flag to indicate the line should start with the key, instead of just containing the key.
	 */
	private static boolean updateConfig( String configFileName, Map<String, Object> configMap, boolean startsWith )
	{
		String logString = "updateConfig()";
		mainLogger.log( Level.FINE, logString );

		// Open the server config file.
		File configFile = new File( configFileName );
		String suffix = "";
		// JSON lines need to end with a comma.  This will cause problems if it is the last element in an object or array.
		if( configFileName.endsWith( "json" ) )
			suffix = ",";

		if( configFile.exists() && configFile.isFile() )
		{
			List<String> fileLinesList = readFileToList( configFileName );

			for( int i = 0; i < fileLinesList.size(); i++ )
			{
				// Set this entry of the list to the fixed line.
				fileLinesList.set( i, fixLine( fileLinesList.get( i ), configMap, suffix, startsWith ) );
			}
			writeListToFile( configFileName, fileLinesList );
		}
		else
		{
			logString = "Unable to find the configuration file: " + configFileName;
			mainLogger.log( Level.INFO, logString );
			return false;
		}
		return true;
	} // End of updateConfig() method.


	/**
	 * clearComment() will remove a comment character from a file.
	 * This method will search through a file identified by configFileName, searching each line for textToFind.
	 * If a line contains textToFind, whitespace will be removed from the beginning and ending of the line.
	 * If the resulting line begins with ';', that first semicolon will be removed.
	 * This will not remove more than one semicolon per line.
	 * A line that begins with two semicolons will only have one removed.
	 * A line like this: ';;SETENV JVM_LIB=/usr/java/jdk1.7.0_75/jre/lib/i386/server/libjvm.so'
	 * Will end up like this: ';SETENV JVM_LIB=/usr/java/jdk1.7.0_75/jre/lib/i386/server/libjvm.so'
	 * Running the program a second time will clear lines that are double-commented.
	 * This will not remove a semicolon that comes after any character other than whitespace.
	 * A line like this: 'PLUGIN cthttpd;./web/cthttpd.dll', will not be changed.
	 * A line like this: ';PLUGIN ctagent;./agent/ctagent.dll', will have only the first semicolon removed.
	 *
	 * @param configFileName the file to search through.
	 * @param textToFind     a unique String identifying the line to uncomment.
	 */
	static void clearComment( String configFileName, String textToFind )
	{
		String logString = "clearComment()";
		mainLogger.log( Level.FINE, logString );

		// Open the server config file.
		File configFile = new File( configFileName );

		if( configFile.exists() && configFile.isFile() )
		{
			List<String> fileLinesList = readFileToList( configFileName );

			for( int i = 0; i < fileLinesList.size(); i++ )
			{
				String line = fileLinesList.get( i );
				// Search for the text, and see if the line begins with a semicolon.
				if( line.contains( textToFind ) && line.trim().startsWith( ";" ) )
				{
					line = line.replaceFirst( ";", "" );
					// Set this entry of the list to the fixed line.
					fileLinesList.set( i, line );
					logString = "\tclearComment() is updating this line: '" + line + "'";
					mainLogger.log( Level.INFO, logString );
				}
			}
			writeListToFile( configFileName, fileLinesList );
		}
		else
		{
			logString = "Unable to find the configuration file: " + configFileName;
			mainLogger.log( Level.INFO, logString );
		}
	} // End of clearComment() method.


	/**
	 * fixLine() takes a String and searches it for every key in a HashMap.<br>
	 * If the key is found, the method returns the key and value from the HashMap.<br>
	 * If the String does not contain any of the keys in the HashMap, no changes will be made to that line.<br>
	 * <br>
	 * The startsWith boolean is set to true in cases where a comment line also contains the key we are searching for.<br>
	 * This prevents the method from uncommenting comment lines, which would happen in 'ctsrvr.cfg' and 'ctReplicationManager.cfg'<br>
	 * If startsWith is set to true, and a file has lines that need to be uncommented, this method will not uncomment them.<br>
	 * The startsWith value should not be set to true for formatted JSON, because it will skip indented lines.<br>
	 *
	 * @param line       the String to search through.
	 * @param myMap      a HashMap of keys to search for and values to append.
	 * @param suffix     a suffix to add to the end of the line.
	 * @param startsWith flag to indicate the line should start with the key, instead of just containing the key.
	 * @return a String containing the key and value.
	 */
	static String fixLine( String line, Map<String, Object> myMap, String suffix, boolean startsWith )
	{
		String logString = "fixLine()";
		mainLogger.log( Level.FINE, logString );

		for( Map.Entry<String, Object> entry : myMap.entrySet() )
		{
			String key = entry.getKey();
			Object value = entry.getValue();

			if( line.contains( key ) )
			{
				// Values may need to be quoted if this is a JSON file.
				if( suffix.equals( "," ) )
					value = quoteIfNeeded( value );
				// This block will uncomment lines and update the contents of the line.
				if( !startsWith )
				{
					// Try to preserve indentation.
					String indentation = line.substring( 0, line.indexOf( key ) );
					// Remove the comment character used in ctsrvr.cfg.
					if( indentation.contains( ";" ) )
					{
						indentation = indentation.replaceFirst( ";", "" );
					}
					// Build the line with the indentation, the key, a tab, the value, and the suffix.
					line = indentation + key + '\t' + value + suffix;
					logString = "\tfixLine() is updating this line: '" + line + "'";
					mainLogger.log( Level.INFO, logString );
				}
				// This block will not alter commented lines.
				else if( line.startsWith( key ) )
				{
					// Try to preserve indentation.
					String indentation = line.substring( 0, line.indexOf( key ) );
					line = indentation + key + '\t' + value + suffix;
					logString = "\tfixLine() is updating this line: '" + line + "'";
					mainLogger.log( Level.INFO, logString );
				}
			}
		}
		return line;
	} // End of fixLine() method.


	/**
	 * quoteIfNeeded()
	 *
	 * Wrap the value in quotes if it is a String.
	 */
	@SuppressWarnings( "squid:S106" )
	private static Object quoteIfNeeded( Object value )
	{
		String logString = "quoteIfNeeded() is checking \"" + value + "\"";
		mainLogger.log( Level.FINE, logString );

		// Exit if the value is already quoted.
		if( value.toString().startsWith( "\"" ) && value.toString().endsWith( "\"" ) )
		{
			logString = "quoteIfNeeded() is skipping " + value + " because it is already quoted.";
			mainLogger.log( Level.FINE, logString );
			return value;
		}

		String ipAddressPatternString = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
		String digitPatternString = "\\d+";
		String wordPatternString = "\\w+";

		Pattern ipAddressPattern = Pattern.compile( ipAddressPatternString );
		Matcher ipAddressMatcher = ipAddressPattern.matcher( value.toString() );
		if( ipAddressMatcher.matches() )
		{
			// Wrap the IP address in quotes.
			value = "\"" + value + "\"";
			return value;
		}
		Pattern digitPattern = Pattern.compile( digitPatternString );
		Matcher digitMatcher = digitPattern.matcher( value.toString() );
		if( digitMatcher.matches() )
		{
			return value;
		}
		Pattern wordPattern = Pattern.compile( wordPatternString );
		Matcher wordMatcher = wordPattern.matcher( value.toString() );
		if( wordMatcher.matches() )
		{
			// Wrap the word in quotes.
			value = "\"" + value + "\"";
			return value;
		}

		logString = "Don't know what to do with: " + value;
		mainLogger.log( Level.WARNING, logString );
		return value;
	} // End of quoteIfNeeded() method.


	/**
	 * getConfig() will validate that the input file name exists on the filesystem and is not a directory.
	 *
	 * @param configFileName the name of the configuration file to load.
	 * @return true if the file exists on the filesystem and is not a directory.
	 */
	static boolean validateConfigFileName( String configFileName )
	{
		String logString = "validateConfigFileName() is reading in \"" + configFileName + "\"";
		mainLogger.log( Level.FINE, logString );

		// Create a File object using configFileName.
		File configFile = new File( configFileName );

		// Ensure the file exists and is not a directory.
		return configFile.exists() || !configFile.isDirectory();
	} // End of validateConfigFileName() method.


	/**
	 * loadConfig() will read in the configuration file and return a Config class object.
	 *
	 * @param configFileName the name of the file to open.
	 * @return an Array of Config class objects, or null if JSON is null or empty.
	 */
	static Config[] loadConfig( String configFileName )
	{
		String logString = "getConfig()";
		mainLogger.log( Level.FINE, logString );

		Gson gson = new Gson();
		Config[] loadConfig = new Config[]{};
		try
		{
			// Read configFile into Gson to create a Config class object.
			loadConfig = gson.fromJson( new BufferedReader( new FileReader( configFileName ) ), Config[].class );
		}
		catch( FileNotFoundException fileNotFoundException )
		{
			mainLogger.log( Level.SEVERE, fileNotFoundException.getLocalizedMessage(), fileNotFoundException );
			// Exit if there are any file IO issues.
			exiting( fileNotFoundException.getLocalizedMessage(), -5 );
		}
		catch( JsonSyntaxException jsonSyntaxException )
		{
			logString = "The configuration file is malformed and unusable!";
			mainLogger.log( Level.SEVERE, logString );
			// Exit if the JSON was malformed.
			exiting( jsonSyntaxException.getLocalizedMessage(), -6 );
		}
		// This should be unreachable because of the exiting() function in the catch block above.
		return loadConfig;
	} // End of loadConfig() method.


	/**
	 * readFileToList() will take a file name and return a List of Strings.
	 *
	 * @param inFileName the file to read.
	 * @return a List of Strings representing the input file.
	 */
	public static List<String> readFileToList( String inFileName )
	{
		List<String> outList = new ArrayList<>();
		try
		{
			outList = Files.readAllLines( Paths.get( inFileName ), StandardCharsets.ISO_8859_1 );
		}
		catch( IOException ioException )
		{
			ioException.printStackTrace();
		}
		// Return the ArrayList.
		return outList;
	} // End of readFileToList() method.


	/**
	 * writeListToFile() will parse a List of Strings and pass them as one String to writeStringToFile().
	 *
	 * @param outFileName a String representing the file name to write to.
	 * @param inList      a List to process and write to file.
	 */
	public static void writeListToFile( String outFileName, List<String> inList )
	{
		StringBuilder mySB = new StringBuilder();
		boolean first = true;
		// Convert a List<String> to one single String.
		for( String line : inList )
		{
			// This prevents the addition of an unwanted extra \n at the end of the file.
			if( !first )
				mySB.append( '\n' );
			mySB.append( line );
			first = false;
		}
		// Write the String to a file.
		writeStringToFile( outFileName, mySB.toString() );
	} // End of writeListToFile() method.


	/**
	 * writeStringToFile() will write a String to a file.
	 *
	 * @param outFileName the file name to write to.
	 * @param data        a String to write to the output file.
	 */
	public static void writeStringToFile( String outFileName, String data )
	{
		String logString = "writeStringToFile()";
		mainLogger.log( Level.FINE, logString );

		try
		{
			// Create a file if it does not exist, and truncate any existing file before writing to it.
			Files.write( Paths.get( outFileName ), Collections.singletonList( data ), StandardCharsets.ISO_8859_1 );
		}
		catch( IOException ioException )
		{
			logString = "Unable to write to the output file: " + outFileName;
			mainLogger.log( Level.SEVERE, logString );
			logString = ioException.getLocalizedMessage();
			mainLogger.log( Level.SEVERE, logString );
			ioException.printStackTrace();
		}
	} // End of writeStringToFile() method.


	// Set up the default format for the console logger.
	static
	{
		singleLine.setFormatter( new SimpleFormatter()
		{
			private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";


			@Override
			public synchronized String format( LogRecord logRecord )
			{
				return String.format( FORMAT,
					new Date( logRecord.getMillis() ),
					logRecord.getLevel().getLocalizedName(),
					logRecord.getMessage()
				);
			}
		} );
	} // End of static block.

	/**
	 * singleToTriple() will switch the logging from single-line, to triple-line output.
	 */
	static void singleToTriple()
	{
		mainLogger.addHandler( tripleLine );
		mainLogger.removeHandler( singleLine );
	} // End of singleToTriple() method.


	/**
	 * tripleToSingle() will switch the logging from triple-line, to single-line output.
	 */
	static void tripleToSingle()
	{
		mainLogger.addHandler( singleLine );
		mainLogger.removeHandler( tripleLine );
	} // End of tripleToSingle() method.


	// Set up the default format for the console logger.
	static
	{
		singleLine.setFormatter( new SimpleFormatter()
		{
			private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";


			@Override
			public synchronized String format( LogRecord logRecord )
			{
				return String.format( FORMAT,
					new Date( logRecord.getMillis() ),
					logRecord.getLevel().getLocalizedName(),
					logRecord.getMessage()
				);
			}
		} );
	} // End of static block.


	// Set up the format for the console logger that has extra blank lines prepended.
	static
	{
		tripleLine.setFormatter( new SimpleFormatter()
		{
			private static final String FORMAT = "\n\n[%1$tF %1$tT] [%2$-7s] %3$s %n";


			@Override
			public synchronized String format( LogRecord logRecord )
			{
				return String.format( FORMAT,
					new Date( logRecord.getMillis() ),
					logRecord.getLevel().getLocalizedName(),
					logRecord.getMessage()
				);
			}
		} );
	} // End of static block.

	/**
	 * exiting() will print an error message and return an exit code to the JVM.
	 *
	 * @param message  the text to display.
	 * @param exitCode the exit code to return to the JVM.
	 */
	static void exiting( String message, int exitCode )
	{
		mainLogger.log( Level.SEVERE, message );
		mainLogger.log( Level.INFO, "Exiting..." );
		System.exit( exitCode );
	} // End of exiting() method.
}
