package com.devellop.mts.settlement.data;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;






public class PgAgent {


	protected Connection connection;
	protected Statement query;

	public static Logger logger = Logger.getLogger(PgAgent.class);




	public PgAgent(Properties properties) throws DatabaseException, SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new DatabaseException(
					"Couldn't find a postgresql driver. Probably missing a classpath.");
		}




		connection = DriverManager.getConnection("jdbc:postgresql:" +
				properties.getProperty(
						"postgres.db.url"),
				properties.getProperty("postgres.db.username"),
				properties.getProperty("postgres.db.password"));
		createStatement();
	}
	
	public PgAgent(String connection_url, String username, String password) throws DatabaseException, SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new DatabaseException(
					"Couldn't find a postgresql driver. Probably missing a classpath.");
		}




		connection = DriverManager.getConnection("jdbc:postgresql:" +
				connection_url,
				username,
				password);
		createStatement();
	}

	public void createStatement() throws SQLException {

		logger.debug("--> createStatement");

		query = connection.createStatement();

		logger.debug("<-- createStatement");

	}


	public void createSettlementFile(String from, String to, String transaction_prefix, String sn_prefix, String file_name) throws SQLException, DatabaseException, IOException{
		logger.debug("--> createSettlementFile, from = " + from + ", to = " + to);
		String query = "select substring(msisdn from 2) as msisdn, amount * 100 as amount, '"+transaction_prefix+"' || TO_CHAR(platform_transaction_id, 'fm00000000000') as tr_id,"
				+ " stop_time, '"+sn_prefix+"' || serial_number as sn from payment p JOIN transaction t on t.id = p.transaction_id JOIN post po on po.id = t.post_id "
				+ "where t.status_id in ( 2, 4, 10 ) and mob_network_id = 1 and stop_time > '"+from+"' and stop_time < '"+to+"' order by t.id";

		logger.info("query = " + query);

		PreparedStatement ps = connection.prepareStatement(query);


		ResultSet rs = ps.executeQuery();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file_name));
			while(rs.next()) {

				writer.write(rs.getString("msisdn") + "," + rs.getString("amount")  + "," + rs.getString("tr_id") +
						"," + rs.getString("stop_time") + "," + rs.getString("sn") + "\n");

			}


		}finally{
			if(writer != null)writer.close();
		}

	}
	
	public void createCentrosinergijaTransactionsSettlementFile(String from, String to, String transaction_prefix, String sn_prefix, String file_name) throws SQLException, DatabaseException, IOException{
		logger.debug("--> createSettlementFile, from = " + from + ", to = " + to);
		String query = "select substring(msisdn from 2) as msisdn, amount * 100 as amount, '"+transaction_prefix+"' || TO_CHAR(platform_transaction_id, 'fm00000000000') as tr_id,"
				+ " stop_time, '"+sn_prefix+"' || serial_number as sn from payment p JOIN transaction t on t.id = p.transaction_id JOIN post po on po.id = t.post_id "
				+ "where t.status_id in ( 2, 4, 10 ) and mob_network_id = 1 and stop_time > '"+from+"' and stop_time < '"+to+"' order by t.id";

		logger.info("query = " + query);

		PreparedStatement ps = connection.prepareStatement(query);


		ResultSet rs = ps.executeQuery();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file_name));
			while(rs.next()) {

				writer.write(rs.getString("msisdn") + "," + rs.getString("amount")  + "," + rs.getString("tr_id") +
						"," + rs.getString("stop_time") + "," + rs.getString("sn") + "\n");

			}


		}finally{
			if(writer != null)writer.close();
		}

	}
	
	public void appendCentrosinergijaTransactionsSettlementFile(String from, String to, String transaction_prefix, String sn_prefix, String file_name) throws SQLException, DatabaseException, IOException{
		logger.debug("--> createSettlementFile, from = " + from + ", to = " + to);
		String query = "select substring(msisdn from 2) as msisdn, amount * 100 as amount, '"+transaction_prefix+"' || TO_CHAR(platform_transaction_id, 'fm00000000000') as tr_id,"
				+ " stop_time, '"+sn_prefix+"' || serial_number as sn from payment p JOIN transaction t on t.id = p.transaction_id JOIN post po on po.id = t.post_id "
				+ "where t.status_id in ( 2, 4, 10 ) and mob_network_id = 1 and stop_time > '"+from+"' and stop_time < '"+to+"' order by t.id";

		logger.info("query = " + query);

		PreparedStatement ps = connection.prepareStatement(query);


		ResultSet rs = ps.executeQuery();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file_name, true));
			while(rs.next()) {

				writer.write(rs.getString("msisdn") + "," + rs.getString("amount")  + "," + rs.getString("tr_id") +
						"," + rs.getString("stop_time") + "," + rs.getString("sn") + "\n");

			}


		}finally{
			if(writer != null)writer.close();
		}

	}
	
	public static void main(String[] args) throws Exception{
		PropertyConfigurator.configure("log4j.properties");
		
		try {
            FileInputStream stream = new FileInputStream(
                    "application.properties");
            Properties properties = new Properties();
            properties.load(stream);
            //INTERNET
            //setISPProperties(properties);
            stream.close();
            
            String file_name = properties.getProperty("settlemt.file.name");
            String from = "2024-05-28";
            from = from.replace("-", "");
            file_name = file_name.replace("<from>", from);
            new PgAgent(properties).createSettlementFile("2024-05-28", "2024-05-29", properties.getProperty("transaction.prefix"), 
            		properties.getProperty("serial.number.prefix"), file_name);
        } catch (Exception fnfe) {
            if (logger.isDebugEnabled()) {
                logger.error("'FileNotFound' exception caught while trying to open application property file - " +
                             fnfe.getMessage());
            }
        }
	}

}
