package com.devellop.mts.settlement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public class HttpClient {

	private static Logger logger = Logger.getLogger(HttpClient.class);

	public void recharge(String msisdn, String transaction_id, double amount) throws Exception {



		int platformTransactionId = Integer.parseInt(transaction_id);

		Hashtable<String, String> parameters = new Hashtable<String, String>();
		parameters.put("RequestType", "rechargeAmount");
		parameters.put("TransactionId",
				"186" +
						new DecimalFormat("00000000000").format(platformTransactionId));
		parameters.put("ReqCred.Role", "4");
		parameters.put("ReqCred.UserId", "StampasistemP");
		parameters.put("ReqCred.PIN", "St@mp@Sistem.123!");
		parameters.put("AccessFrontendId", "ST" + "800406");

		parameters.put("ConsumerId", msisdn);
		parameters.put("ConsumerAccountId", "0");
		parameters.put("ConsumerPIN", "");

		try {
			parameters.put("Purpose", URLEncoder.encode("stampasistem", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			if (logger.isDebugEnabled()) {
				logger.error(
						"UnsupportedEncodingException , details : " +
								e.getMessage(), e);

			}

			throw new Exception(
					"Couldn't access PaymentPluginServlet.rechargeAmount (urlencode)");
		}
		parameters.put("Money.Currency", "DIN");
		parameters.put("Money.Amount", "" + new Double(amount * 100000).intValue());
		//      builder.add("ExpiryDate", "-1");
		parameters.put("ExpiryDate", "15552000000");
		try {

			StringBuilder builder = new StringBuilder();
			Enumeration keys = parameters.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				builder.append(key + "=" + parameters.get(key));
				if (keys.hasMoreElements())
					builder.append('&');
			}



			String result = callServlet(builder.toString(),
							10000, "http://10.1.71.46/Siemens.EVoucher.POS.WebApp/Default.aspx");

			System.out.println(
					"Result from calling content provider service = " +
							result);



			boolean is_valid = result.substring(result.indexOf("ExecutionStatus=") + 16).equals("1") && 
					result.length() == result.indexOf("ExecutionStatus=") + 17;



			System.out.println(
					"is_valid = " + is_valid );
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String timestamp = sdf.format(new java.util.Date());
			System.out.println("za settlement: 381640596018,20000,1860000000" + platformTransactionId + "," +
					timestamp + ",ST800406");





		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.error("PaymentException , details : " +
						e.getMessage(), e);

			}

			throw new Exception(
					"Couldn't access PaymentPluginServlet.rechargeAmount" +
					platformTransactionId);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("<--");

		}


	}


	protected String callServlet(String queryString, int timeout, String url) throws Exception
	{
		try
		{


			if (logger.isDebugEnabled()) {
				logger.info("TELEKOM SEND query string = " +
						queryString);
			}

			URLConnection connection = getConnection(queryString, url);
			setTimeout(connection, timeout);
			setParameters(connection, queryString);
			System.out.println("send telekom request");
			//			System.out.println(connection.getURL().getPath());
			//			System.out.println(queryString);
			//			System.out.println("timeout (ms): " + connection.getReadTimeout());

			String toReturn = getRawResult(connection);
			if (logger.isDebugEnabled()) {
				logger.info("TELEKOM RECEIVED");
			}

			return toReturn;
		}
		catch (IOException e)
		{
			if (logger.isDebugEnabled()) {
				logger.error(
						"IOException when try to connect to telekom, details: " +
								e.getMessage(), e);
			}

			e.printStackTrace();
			throw new Exception("Couldn't get response.");
		}
	}

	protected URLConnection getConnection(String queryString, String url_string) throws
	Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("--> queryString = " + queryString);

		}

		URLConnection connection = null;
		//"https://pcoosite/PaymentCoordinator/PaymentCoordinator.aspx"
		try {
			URL url = new URL(url_string);
			connection = url.openConnection();
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
		} catch (MalformedURLException e) {
			if (logger.isDebugEnabled()) {
				logger.error("MalformedURLException , details : " +
						e.getMessage(), e);

			}

			throw new Exception("Malformed url");
		} catch (IOException e) {
			if (logger.isDebugEnabled()) {
				logger.error("IOException , details : " +
						e.getMessage(), e);

			}

			throw new Exception("Couldn't open url");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("<--");

		}

		return connection;
	}
	
	private String getRawResult(URLConnection connection) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		return reader.readLine();
	}
	
    protected void setTimeout(URLConnection connection, int timeout) {
        
        connection.setReadTimeout(timeout);
    }

    protected final void setParameters(URLConnection connection,
    		String queryString) throws IOException {
    	connection.setDoOutput(true);
    	OutputStreamWriter writer = new OutputStreamWriter(connection.
    			getOutputStream(), "ASCII");
    	writer.write(queryString);
    	writer.flush();
    	writer.close();
    }

}
