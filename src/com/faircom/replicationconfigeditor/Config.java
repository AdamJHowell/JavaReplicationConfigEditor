package com.faircom.replicationconfigeditor;


/**
 * This class represents the configuration for this program.<br>
 * It should be able to edit any file that it has write access to (CIFS, SAN, etc.).<br>
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
	 * The 'mqtt_listening_port' value for 'cthttpd.json'.
	 */
	private final Integer mqttListeningPort;
	/**
	 * The 'mqtt_websocket_port' value for 'cthttpd.json'.
	 */
	private final Integer mqttWebsocketPort;


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
		String memphis = "MEMPHIS";
		this.baseDirectory = "";
		this.configDirectory = "config";
		this.serverFileName = "ctsrvr.cfg";
		this.serverName = memphis;
		this.serverPort = "19991";
		this.readOnlyServer = "NO";
		this.sqlPort = "19991";
		this.httpFileName = "cthttpd.json";
		this.listeningHttpPort = 19993;
		this.listeningHttpsPort = 19992;
		this.mqttListeningPort = null;
		this.mqttWebsocketPort = null;
		this.agentFileName = "ctagent.json";
		this.memphisServerName = memphis;
		this.memphisSqlPort = 19991;
		this.memphisHost = "127.0.0.1";
		this.memphisDatabase = memphis;
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
		return listeningHttpPort;
	}


	public Integer getListeningHttpsPort()
	{
		return listeningHttpsPort;
	}


	public Integer getMqttListeningPort()
	{
		return mqttListeningPort;
	}


	public Integer getMqttWebsocketPort()
	{
		return mqttWebsocketPort;
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
