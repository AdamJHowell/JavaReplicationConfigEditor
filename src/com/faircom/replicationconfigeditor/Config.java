package com.faircom.replicationconfigeditor;


/**
 * This class represents the configuration for this program.<br>
 * It contains cTree JDBC, MQTT server, and test parameters.
 */
class Config
{
	/**
	 * The directory where this node is located.
	 */
	private final String baseDirectory;
	/**
	 * The config directory, relative to the base directory.
	 */
	private final String configDirectory;
	/**
	 * The data directory, relative to the base directory.
	 */
	private final String dataDirectory;


	/**
	 * The name of the server config file, typically 'ctsrvr.cfg'.
	 */
	private final String serverFileName;
	/**
	 * The 'SERVER_NAME' value for 'ctsrvr.cfg'.
	 */
	private final String serverName;
	/**
	 * The 'SERVER_PORT' value for 'ctsrvr.cfg'.
	 */
	private final String serverPort;
	/**
	 * The 'READONLY_SERVER' value for 'ctsrvr.cfg'.
	 * Valid values are 'YES' and 'NO'.
	 */
	private final String readOnlyServer;
	/**
	 * The 'SQL_PORT' value for 'ctsrvr.cfg'.
	 */
	private final String sqlPort;
	/**
	 * The 'httpPlugin' value for 'ctsrvr.cfg'.
	 * Note: This library name is OS specific (Windows/Linux/Mac)!
	 */
	private final String httpPlugin;
	/**
	 * The 'agentPlugin' value for 'ctsrvr.cfg'.
	 * Note: This library name is OS specific (Windows/Linux/Mac)!
	 */
	private final String agentPlugin;


	/**
	 * The name of the HTTP daemon config file, typically 'cthttpd.json'.
	 */
	private final String httpFileName;
	/**
	 * The 'listening_http_port' value for 'cthttpd.json'.
	 */
	private final String listeningHttpPort;
	/**
	 * The 'listening_https_port' value for 'cthttpd.json'.
	 */
	private final String listeningHttpsPort;


	/**
	 * The name of the Agent config file, typically 'ctagent.json'.
	 */
	private final String agentFileName;
	/**
	 * The 'memphis_server_name' value for 'ctagent.json'.
	 */
	private final String memphisServerName;
	/**
	 * The 'memphis_sql_port' value for 'ctagent.json'.
	 */
	private final String memphisSqlPort;
	/**
	 * The 'memphis_host' value for 'ctagent.json'.
	 */
	private final String memphisHost;
	/**
	 * The 'memphis_database' value for 'ctagent.json'.
	 */
	private final String memphisDatabase;


	public Config( String baseDirectory, String configDirectory, String dataDirectory,
		String serverFileName, String serverName, String serverPort, String readOnlyServer, String sqlPort, String httpPlugin, String agentPlugin,
		String httpFileName, String listeningHttpPort, String listeningHttpsPort,
		String agentFileName, String memphisServerName, String memphisSqlPort, String memphisHost, String memphisDatabase )
	{
		this.baseDirectory = baseDirectory;
		this.configDirectory = configDirectory;
		this.dataDirectory = dataDirectory;
		this.serverFileName = serverFileName;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.readOnlyServer = readOnlyServer;
		this.sqlPort = sqlPort;
		this.httpPlugin = httpPlugin;
		this.agentPlugin = agentPlugin;
		this.httpFileName = httpFileName;
		this.listeningHttpPort = listeningHttpPort;
		this.listeningHttpsPort = listeningHttpsPort;
		this.agentFileName = agentFileName;
		this.memphisServerName = memphisServerName;
		this.memphisSqlPort = memphisSqlPort;
		this.memphisHost = memphisHost;
		this.memphisDatabase = memphisDatabase;
	}


	public String getBaseDirectory()
	{
		return baseDirectory;
	}


	public String getConfigDirectory()
	{
		return configDirectory;
	}


	public String getDataDirectory()
	{
		return dataDirectory;
	}


	public String getServerFileName()
	{
		return serverFileName;
	}


	public String getServerName()
	{
		return serverName;
	}


	public String getServerPort()
	{
		return serverPort;
	}


	public String getReadOnlyServer()
	{
		return readOnlyServer;
	}


	public String getSqlPort()
	{
		return sqlPort;
	}


	public String getHttpPlugin()
	{
		return httpPlugin;
	}


	public String getAgentPlugin()
	{
		return agentPlugin;
	}


	public String getHttpFileName()
	{
		return httpFileName;
	}


	public String getListeningHttpPort()
	{
		return listeningHttpPort;
	}


	public String getListeningHttpsPort()
	{
		return listeningHttpsPort;
	}


	public String getAgentFileName()
	{
		return agentFileName;
	}


	public String getMemphisServerName()
	{
		return memphisServerName;
	}


	public String getMemphisSqlPort()
	{
		return memphisSqlPort;
	}


	public String getMemphisHost()
	{
		return memphisHost;
	}


	public String getMemphisDatabase()
	{
		return memphisDatabase;
	}
}
