package com.faircom.replicationconfigeditor;


/**
 * This class represents the configuration for this program.<br>
 * It should be able to edit any file that it has write access to (CIFS, SAN, etc.).<br>
 * It contains cTree JDBC, MQTT server, and test parameters.
 */
@SuppressWarnings( "squid:S116" )
class Config
{
	/**
	 * The directory where this node is located.
	 * Defaults to an empty String.
	 */
	private final String baseDirectory;
	/**
	 * Optional: The config directory, relative to the base directory.
	 * Defaults to "config".
	 */
	private final String configDirectory;


	/**
	 * Optional: The name of the server config file.
	 * Defaults to "ctsrvr.cfg".
	 */
	private final String serverFileName;
	/**
	 * The 'SERVER_NAME' value for 'ctsrvr.cfg'.
	 * This setting should typically be exclusive with serverPort.
	 * The SERVER_PORT setting overrides the SERVER_NAME setting in FairCom servers.
	 * Defaults to an empty String.
	 */
	private final String serverName;
	/**
	 * The 'SERVER_PORT' value for 'ctsrvr.cfg'.
	 * This setting should typically be exclusive with serverName.
	 * The SERVER_PORT setting overrides the SERVER_NAME setting in FairCom servers.
	 * Defaults to an empty String.
	 */
	private final String serverPort;
	/**
	 * The 'READONLY_SERVER' value for 'ctsrvr.cfg'.
	 * Valid values are 'YES' and 'NO'.
	 * Defaults to "NO".
	 */
	private final String readOnlyServer;
	/**
	 * The 'SQL_PORT' value for 'ctsrvr.cfg'.
	 * Defaults to "19991".
	 */
	private final String sqlPort;


	/**
	 * Optional: The name of the HTTP daemon config file.
	 * Defaults to "cthttpd.json".
	 */
	private final String httpFileName;
	/**
	 * The 'listening_http_port' value for 'cthttpd.json'.
	 * Defaults to 19993.
	 */
	private final Integer listeningHttpPort;
	private final Integer http_port;
	/**
	 * The 'listening_https_port' value for 'cthttpd.json'.
	 * Defaults to 19992.
	 */
	private final Integer listeningHttpsPort;
	private final Integer https_port;
	/**
	 * The 'mqtt_listening_port' value for 'cthttpd.json'.
	 * Defaults to null.
	 */
	private final Integer mqttListeningPort;
	private final Integer mqtt_port;
	/**
	 * The 'mqtt_websocket_port' value for 'cthttpd.json'.
	 * Defaults to null.
	 */
	private final Integer mqttWebsocketPort;
	private final Integer websocket_port;


	/**
	 * Optional: The name of the Agent config file.
	 * Defaults to "ctagent.json".
	 */
	private final String agentFileName;
	/**
	 * The 'memphis_server_name' value for 'ctagent.json'.
	 * Defaults to a "MEMPHIS".
	 */
	private final String memphisServerName;
	private final String memphis_server_name;
	/**
	 * The 'memphis_sql_port' value for 'ctagent.json'.
	 * This value will also be used to update ctReplicationManager.cfg.
	 */
	private final Integer memphisSqlPort;
	private final Integer memphis_sql_port;
	/**
	 * The 'memphis_host' value for 'ctagent.json'.
	 * Defaults to "127.0.0.1".
	 */
	private final String memphisHost;
	private final String memphis_host;
	/**
	 * The 'memphis_database' value for 'ctagent.json'.
	 * Defaults to a "MEMPHIS".
	 */
	private final String memphisDatabase;
	private final String memphis_database;


	/**
	 * Optional: The name of the Agent config file.
	 * Defaults to 'ctReplicationManager.cfg'.
	 * This file only exists in the Replication Manager (Memphis) config directory.
	 * The 'MEMPHIS_SERVER_NAME', 'MEMPHIS_SQL_PORT', 'MEMPHIS_HOST', and 'MEMPHIS_DATABASE' value in this file will be updated with the values from ctagent.
	 * Defaults to an empty String.
	 */
	private final String replicationManagerFileName;


	/**
	 * The no-arg constructor prevents Gson from initializing missing values to null.
	 * Some values are deliberately set to null.  This is used as a way to handle JSON property name changes in 12.5.
	 * These defaults are set to a typical Replication Manager setup.
	 */
	public Config()
	{
		this.baseDirectory = "";
		this.configDirectory = "config";
		this.serverFileName = "ctsrvr.cfg";
		this.serverName = "";
		this.serverPort = "";
		this.readOnlyServer = "NO";
		this.sqlPort = "19991";
		this.httpFileName = "cthttpd.json";
		this.listeningHttpPort = null;
		this.http_port = null;
		this.listeningHttpsPort = null;
		this.https_port = null;
		this.mqttListeningPort = null;
		this.mqtt_port = null;
		this.mqttWebsocketPort = null;
		this.websocket_port = null;
		this.agentFileName = "ctagent.json";
		this.memphis_server_name = "";
		this.memphisServerName = "";
		this.memphisSqlPort = null;
		this.memphis_sql_port = null;
		this.memphisHost = "";
		this.memphis_host = "";
		this.memphisDatabase = "";
		this.memphis_database = "";
		this.replicationManagerFileName = "";
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


	public String getHttpFileName()
	{
		return httpFileName;
	}


	public Integer getListeningHttpPort()
	{
		if( http_port == null )
			return listeningHttpPort;
		return http_port;
	}


	public Integer getListeningHttpsPort()
	{
		if( https_port == null )
			return listeningHttpsPort;
		return https_port;
	}


	public Integer getMqttListeningPort()
	{
		if( mqtt_port == null )
			return mqttListeningPort;
		return mqtt_port;
	}


	public Integer getMqttWebsocketPort()
	{
		if( websocket_port == null )
			return mqttWebsocketPort;
		return websocket_port;
	}


	public String getAgentFileName()
	{
		return agentFileName;
	}


	public String getMemphisServerName()
	{
		if( memphis_server_name.isEmpty() )
			return memphisServerName;
		return memphis_server_name;
	}


	public Integer getMemphisSqlPort()
	{
		if( memphis_sql_port == null )
			return memphisSqlPort;
		return memphis_sql_port;
	}


	public String getMemphisHost()
	{
		if( memphis_host.isEmpty() )
			return memphisHost;
		return memphis_host;
	}


	public String getMemphisDatabase()
	{
		if( memphis_database.isEmpty() )
			return memphisDatabase;
		return memphis_database;
	}


	public String getReplicationManagerFileName()
	{
		return replicationManagerFileName;
	}


	@Override public String toString()
	{
		return "Config{" +
		       "\"baseDirectory\":\"" + baseDirectory + '\"' +
		       ", \"configDirectory\":\"" + configDirectory + '\"' +
		       ", \"serverFileName\":\"" + serverFileName + '\"' +
		       ", \"serverName\":\"" + serverName + '\"' +
		       ", \"serverPort\":\"" + serverPort + '\"' +
		       ", \"readOnlyServer\":\"" + readOnlyServer + '\"' +
		       ", \"sqlPort\":\"" + sqlPort + '\"' +
		       ", \"httpFileName\":\"" + httpFileName + '\"' +
		       ", \"listeningHttpPort\":" + listeningHttpPort +
		       ", \"listeningHttpsPort\":" + listeningHttpsPort +
		       ", \"mqttListeningPort\":" + mqttListeningPort +
		       ", \"mqttWebsocketPort\":" + mqttWebsocketPort +
		       ", \"agentFileName\":\"" + agentFileName + '\"' +
		       ", \"memphisServerName\":\"" + memphisServerName + '\"' +
		       ", \"memphisSqlPort\":" + memphisSqlPort +
		       ", \"memphisHost\":\"" + memphisHost + '\"' +
		       ", \"memphisDatabase\":\"" + memphisDatabase + '\"' +
		       ", \"replicationManagerFileName\":\"" + replicationManagerFileName + '\"' +
		       '}';
	}
}
