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
	 * Optional: The config directory, relative to the base directory.
	 * Defaults to 'config'.
	 */
	private final String configDirectory;


	/**
	 * Optional: The name of the server config file.
	 * Defaults to 'ctsrvr.cfg'.
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
	 * Optional: The name of the HTTP daemon config file.
	 * Defaults to 'cthttpd.json'.
	 */
	private final String httpFileName;
	/**
	 * The 'listening_http_port' value for 'cthttpd.json'.
	 */
	private final Integer listeningHttpPort;
	/**
	 * The 'listening_https_port' value for 'cthttpd.json'.
	 */
	private final Integer listeningHttpsPort;


	/**
	 * Optional: The name of the Agent config file.
	 * Defaults to 'ctagent.json'.
	 */
	private final String agentFileName;
	/**
	 * The 'memphis_server_name' value for 'ctagent.json'.
	 */
	private final String memphisServerName;
	/**
	 * The 'memphis_sql_port' value for 'ctagent.json'.
	 * This value will also be used to update ctReplicationManager.cfg.
	 */
	private final Integer memphisSqlPort;
	/**
	 * The 'memphis_host' value for 'ctagent.json'.
	 */
	private final String memphisHost;
	/**
	 * The 'memphis_database' value for 'ctagent.json'.
	 */
	private final String memphisDatabase;


	/**
	 * Optional: The name of the Agent config file.
	 * Defaults to 'ctReplicationManager.cfg'.
	 * This file only exists in the Replication Manager (Memphis) config directory.
	 * The 'MEMPHIS_SQL_PORT' value in this file will be updated with the value from memphisSqlPort.
	 */
	private final String replicationManagerFileName;


	/**
	 * The no-arg constructor prevents Gson from initializing missing values to null.
	 * These defaults are set to a typical Replication Manager setup.
	 */
	public Config()
	{
		this.baseDirectory = "";
		this.configDirectory = "config";
		this.serverFileName = "ctsrvr.cfg";
		this.serverName = "MEMPHIS";
		this.serverPort = "19991";
		this.readOnlyServer = "NO";
		this.sqlPort = "19991";
		this.httpPlugin = "";
		this.agentPlugin = "";
		this.httpFileName = "cthttpd.json";
		this.listeningHttpPort = 19993;
		this.listeningHttpsPort = 19992;
		this.agentFileName = "ctagent.json";
		this.memphisServerName = "\"MEMPHIS\"";
		this.memphisSqlPort = 19991;
		this.memphisHost = "\"127.0.0.1\"";
		this.memphisDatabase = "\"MEMPHIS\"";
		this.replicationManagerFileName = "ctReplicationManager.cfg";
	}


	public Config( String baseDirectory, String configDirectory,
		String serverFileName, String serverName, String serverPort, String readOnlyServer, String sqlPort, String httpPlugin, String agentPlugin,
		String httpFileName, Integer listeningHttpPort, Integer listeningHttpsPort,
		String agentFileName, String memphisServerName, Integer memphisSqlPort, String memphisHost, String memphisDatabase,
		String replicationManagerFileName )
	{
		this.baseDirectory = baseDirectory;
		this.configDirectory = configDirectory;
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
		this.replicationManagerFileName = replicationManagerFileName;
	}


	public String getBaseDirectory()
	{
		return baseDirectory;
	}


	public String getConfigDirectory()
	{
		return configDirectory;
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


	public Integer getListeningHttpPort()
	{
		return listeningHttpPort;
	}


	public Integer getListeningHttpsPort()
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


	public Integer getMemphisSqlPort()
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


	public String getReplicationManagerFileName()
	{
		return replicationManagerFileName;
	}
}
