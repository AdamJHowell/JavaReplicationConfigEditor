package com.faircom.replicationconfigeditor;


import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;


/**
 * Started at 2021-07-27 at 09:55.
 * Pause at 11:25
 * Resume at 11:40
 * I think that I am done at 12:32.
 * Confirmed that the JAR file works at the Windows command line at 12:37.
 * Refactored individual configuration update functions into a single generic function at 12:57
 */
public class Main
{
	private static final Logger mainLogger = Logger.getLogger( Main.class.getName() );
	private static final ConsoleHandler singleLine = new ConsoleHandler();
	private static final String FILE_SEP = System.getProperty( "file.separator" );
	private static final String BUILD_TIME = "main() - build 2021-07-278 1552";
	private static final String UPDATING = "Updating ";


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
		Config config = validateConfigFileName( configFileName );

		// ctsrvr.cfg section.
		String serverConfigFileName = config.getBaseDirectory() + FILE_SEP + config.getConfigDirectory() + FILE_SEP + config.getServerFileName();
		Map<String, String> serverConfigMap = new HashMap<String, String>(){ };
		serverConfigMap.put( "SERVER_NAME", config.getServerName() );
		serverConfigMap.put( "SERVER_PORT", config.getServerPort() );
		serverConfigMap.put( "READONLY_SERVER", config.getReadOnlyServer() );
		serverConfigMap.put( "SQL_PORT", config.getSqlPort() );
		logString = UPDATING + serverConfigFileName;
		mainLogger.log( Level.INFO, logString );
		updateConfig( serverConfigFileName, serverConfigMap, false );
		// Remove comments from lines that load required plugins.
		clearComment( serverConfigFileName, "cthttpd." );
		clearComment( serverConfigFileName, "ctagent." );

		// cthttpd.json configuration section.
		String httpConfigFileName = config.getBaseDirectory() + FILE_SEP + config.getConfigDirectory() + FILE_SEP + config.getHttpFileName();
		Map<String, String> httpConfigMap = new HashMap<String, String>(){ };
		httpConfigMap.put( "\"listening_http_port\":", config.getListeningHttpPort().toString() );
		httpConfigMap.put( "\"listening_https_port\":", config.getListeningHttpsPort().toString() );
		// Replication Manager does not have settings for MQTT ports.
		if( config.getMqttListeningPort() != null )
			httpConfigMap.put( "\"mqtt_listening_port\":", config.getMqttListeningPort().toString() );
		if( config.getMqttWebsocketPort() != null )
			httpConfigMap.put( "\"mqtt_websocket_port\":", config.getMqttWebsocketPort().toString() );
		logString = UPDATING + httpConfigFileName;
		mainLogger.log( Level.INFO, logString );
		updateConfig( httpConfigFileName, httpConfigMap, false );

		// ctagent.json configuration section.
		String agentConfigFileName = config.getBaseDirectory() + FILE_SEP + config.getConfigDirectory() + FILE_SEP + config.getAgentFileName();
		Map<String, String> agentConfigMap = new HashMap<String, String>(){ };
		agentConfigMap.put( "\"memphis_server_name\":", config.getMemphisServerName() );
		agentConfigMap.put( "\"memphis_sql_port\":", config.getMemphisSqlPort().toString() );
		agentConfigMap.put( "\"memphis_host\":", config.getMemphisHost() );
		agentConfigMap.put( "\"memphis_database\":", config.getMemphisDatabase() );
		logString = UPDATING + agentConfigFileName;
		mainLogger.log( Level.INFO, logString );
		updateConfig( agentConfigFileName, agentConfigMap, false );

		// ctReplicationManager.cfg configuration section.
		if( !config.getReplicationManagerFileName().isEmpty() )
		{
			String replicationManagerConfigFileName = config.getBaseDirectory() + FILE_SEP + config.getConfigDirectory() + FILE_SEP + config.getReplicationManagerFileName();
			Map<String, String> replicationManagerConfigMap = new HashMap<String, String>(){ };
			// Note that this uses the same port configured in the agent.json section.
			replicationManagerConfigMap.put( "MEMPHIS_SQL_PORT", config.getMemphisSqlPort().toString() );
			logString = UPDATING + replicationManagerConfigFileName;
			mainLogger.log( Level.INFO, logString );
			updateConfig( replicationManagerConfigFileName, replicationManagerConfigMap, true );
		}
	} // End of main() method.


	/**
	 * updateConfig() will update a configuration file to properly set specific values.
	 * It does this by opening the file, parsing every line, and writing and updated file.
	 * Lines are updated by the fixLine() method.
	 *
	 * @param configFileName the file to open and parse.
	 * @param configMap      a map containing keys to search for and values to append to those keys.
	 * @param startsWith     flag to indicate the line should start with the key, instead of just containing the key.
	 */
	private static void updateConfig( String configFileName, Map<String, String> configMap, boolean startsWith )
	{
		String logString = "updateConfig()";
		mainLogger.log( Level.FINE, logString );

		// Open the server config file.
		File configFile = new File( configFileName );
		String suffix = "";
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
		}
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
	 * This prevents the method from uncommenting comment lines.  This is typically used for 'ctReplicationManager.cfg'<br>
	 * If startsWith is set to true, and a file has lines that need to be uncommented, this method will not uncomment them.<br>
	 * The startsWith value should not be set to true for formatted JSON, because it will skip indented lines.<br>
	 *
	 * @param line       the String to search through.
	 * @param myMap      a HashMap of keys to search for and values to append.
	 * @param suffix     a suffix to add to the end of the line.
	 * @param startsWith flag to indicate the line should start with the key, instead of just containing the key.
	 * @return a String containing the key and value.
	 */
	static String fixLine( String line, Map<String, String> myMap, String suffix, boolean startsWith )
	{
		String logString = "fixLine()";
		mainLogger.log( Level.FINE, logString );

		for( Map.Entry<String, String> entry : myMap.entrySet() )
		{
			String key = entry.getKey();
			Object value = entry.getValue();

			if( line.contains( key ) )
			{
				// This block will uncomment lines.
				if( !startsWith )
				{
					// Try to preserve indentation.
					String indentation = line.substring( 0, line.indexOf( key ) );
					// Remove the comment character used in ctsrvr.cfg.
					if( indentation.contains( ";" ) )
					{
						indentation = indentation.replaceFirst( ";", "" );
					}
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
	 * getConfig() will validate the configuration file name and call loadConfig().
	 *
	 * @param configFileName the name of the configuration file to load.
	 * @return a Config class object representing the configuration file.
	 */
	static Config validateConfigFileName( String configFileName )
	{
		String logString = "validateConfigFileName()";
		mainLogger.log( Level.FINE, logString );

		// Create a File object using configFileName.
		File configFile = new File( configFileName );

		// Ensure the file exists and is not a directory.
		if( !configFile.exists() || configFile.isDirectory() )
		{
			exiting( "Invalid configuration file!", -5 );
		}
		// Read configFile into Gson to create a Config class object.
		Config config = loadConfig( configFile );
		if( config == null )
		{
			// Exit the program if there was any JSON error.
			exiting( "Unable to load the configuration file " + configFileName, -6 );
		}
		assert config != null;
		return config;
	} // End of validateConfigFileName() method.


	/**
	 * loadConfig() will read in the configuration file and return a Config class object.
	 * This method will not return null.  See below.
	 *
	 * @param configFile the File to open.
	 * @return a Config class object, or null if JSON is null or empty.
	 */
	static Config loadConfig( File configFile )
	{
		String logString = "getConfig()";
		mainLogger.log( Level.FINE, logString );

		Gson gson = new Gson();
		try
		{
			// Read configFile into Gson to create a Config class object.
			return gson.fromJson( new BufferedReader( new FileReader( configFile ) ), Config.class );
		}
		catch( FileNotFoundException fileNotFoundException )
		{
			mainLogger.log( Level.SEVERE, fileNotFoundException.getLocalizedMessage(), fileNotFoundException );
			// Exit if there are any file IO issues.
			exiting( fileNotFoundException.getLocalizedMessage(), -7 );
		}
		// This should be unreachable because of the exiting() function in the catch block above.
		return null;
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
