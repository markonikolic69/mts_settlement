package com.devellop.mts.settlement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.devellop.mts.settlement.data.PgAgent;
import com.devellop.mts.settlement.util.FTPUtil;
import com.devellop.mts.settlement.util.MailUtil;

/**
 * Hello world!
 *
 */
public class App 
{

	private static Logger logger = Logger.getLogger(App.class);

	private Properties _properties =null;

	private String _operation = "upload";

	public App() {
		init();
		_operation = _properties.getProperty("oparation");
	}


	private void init() {
		PropertyConfigurator.configure("log4j.properties");

		try {
			FileInputStream stream = new FileInputStream(
					"application.properties");
			_properties = new Properties();
			_properties.load(stream);
			//INTERNET
			//setISPProperties(properties);
			stream.close();

		} catch (Exception fnfe) {
			if (logger.isDebugEnabled()) {
				logger.error("'FileNotFound' exception caught while trying to open application property file - " +
						fnfe.getMessage());
			}
		}
	}


	public void doCentrosinergijaUpload() {
		try {

			String file_name = _properties.getProperty("cs.settlemet.file.name");
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String to = sdf1.format(today.getTime());
			today.add(Calendar.DAY_OF_YEAR, -1);
			String from = sdf1.format(today.getTime());
			if(_properties.getProperty("settlement.from") != null) {
				from = _properties.getProperty("settlement.from");
			}
			if(_properties.getProperty("settlement.to") != null) {
				to = _properties.getProperty("settlement.to");
			}
			String from_suffix = from.replace("-", "");
			new PgAgent(_properties.getProperty("cs_telekom.postgres.db.url"),_properties.getProperty("cs_telekom.postgres.db.username")
					,_properties.getProperty("cs_telekom.postgres.db.password")).createCentrosinergijaTransactionsSettlementFile(from, to, _properties.getProperty("cs_telekom.transaction.prefix"), 
							_properties.getProperty("cs.serial.number.prefix"), file_name.replace("<from>", from_suffix));

			new PgAgent(_properties.getProperty("cs_pht.postgres.db.url"),_properties.getProperty("cs_telekom.postgres.db.username")
					,_properties.getProperty("cs_pht.postgres.db.password")).appendCentrosinergijaTransactionsSettlementFile(from, to, _properties.getProperty("cs_pht.transaction.prefix"), 
							_properties.getProperty("cs.serial.number.prefix"), file_name.replace("<from>", from_suffix));

			new FTPUtil(_properties.getProperty("cs.ftp.server"), _properties.getProperty("cs.ftp.username"), 
					_properties.getProperty("cs.ftp.password"),  _properties.getProperty("cs.ftp.upload.dir.remote") , 
					_properties.getProperty("cs.ftp.upload.dir.local")).upload(file_name.replace("<from>", from_suffix));
		} catch (Exception fnfe) {
			if (logger.isDebugEnabled()) {
				logger.error("'FileNotFound' exception caught while trying to open application property file - " +
						fnfe.getMessage());
			}
		}
	}



	public void doIt() {
		if(_operation.equals("stampa_mail")) {
			doStampaTransactionFileMail();
		}
		else{
			if(_operation.equals("centrosinergija")) {

				doCentrosinergijaUpload();
			}else {
				if(_operation.equals("upload")) {
					doUpload();
				}else {
					doDownload();
				}
			}
		}

	}

	private void doUpload() {
		try {

			String file_name = _properties.getProperty("settlemet.file.name");
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String to = sdf1.format(today.getTime());
			today.add(Calendar.DAY_OF_YEAR, -1);
			String from = sdf1.format(today.getTime());
			if(_properties.getProperty("settlement.from") != null) {
				from = _properties.getProperty("settlement.from");
			}
			if(_properties.getProperty("settlement.to") != null) {
				to = _properties.getProperty("settlement.to");
			}
			String from_suffix = from.replace("-", "");
			new PgAgent(_properties).createSettlementFile(from, to, _properties.getProperty("transaction.prefix"), 
					_properties.getProperty("serial.number.prefix"), file_name.replace("<from>", from_suffix));

			new FTPUtil(_properties.getProperty("ftp.server"), _properties.getProperty("ftp.username"), 
					_properties.getProperty("ftp.password"),  _properties.getProperty("ftp.upload.dir.remote") , 
					_properties.getProperty("ftp.upload.dir.local")).upload(file_name.replace("<from>", from_suffix));
		} catch (Exception fnfe) {
			if (logger.isDebugEnabled()) {
				logger.error("'FileNotFound' exception caught while trying to open application property file - " +
						fnfe.getMessage());
			}
		}
	}


	private void doDownload() {
		try {

			String file_name = _properties.getProperty("settlemet.file.name.remote");

			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

			today.add(Calendar.DAY_OF_YEAR, -1);
			String from = sdf1.format(today.getTime());
			if(_properties.getProperty("settlement.from") != null) {
				from = _properties.getProperty("settlement.from");
			}

			String from_suffix = from.replace("-", "");

			new FTPUtil(_properties.getProperty("ftp.server"), _properties.getProperty("ftp.username"), 
					_properties.getProperty("ftp.password"), _properties.getProperty("ftp.download.dir.remote") , 
					_properties.getProperty("ftp.download.dir.local") ).download(file_name.replace("<from>", from_suffix));

		} catch (Exception fnfe) {
			if (logger.isDebugEnabled()) {
				logger.error("'FileNotFound' exception caught while trying to open application property file - " +
						fnfe.getMessage());
			}
		}
	}
	
	public void doStampaTransactionFileMail() {
		try {

			String file_name = _properties.getProperty("stampa.transactions.file.name");
			Calendar today = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			String to = sdf1.format(today.getTime());
			today.add(Calendar.DAY_OF_YEAR, -1);
			String from = sdf1.format(today.getTime());
			if(_properties.getProperty("settlement.from") != null) {
				from = _properties.getProperty("settlement.from");
			}
			if(_properties.getProperty("settlement.to") != null) {
				to = _properties.getProperty("settlement.to");
			}
			String from_suffix = from.replace("-", "");
			file_name = file_name.replace("<from>", from_suffix);
			new PgAgent(_properties).createTransactionFileForStampa(from, to, file_name);

			new MailUtil().sendStampaTransaction("U prilogu", "no-reply@devellop.com", "Prepaid transakcije za " + from, new File(file_name));
		} catch (Exception fnfe) {
			if (logger.isDebugEnabled()) {
				logger.error("'FileNotFound' exception caught while trying to open application property file - " +
						fnfe.getMessage());
			}
		}
	}


	public static void main(String[] args) throws Exception{

		new App().doIt();

	}


	//    public static void main( String[] args )
	//    {
	//        System.out.println( "Hello World!" );
	//        
	//        FTPClient ftp = new FTPClient();
	//        FTPClientConfig config = new FTPClientConfig();
	//        boolean error = false;
	//        try {
	//          int reply;
	//          String server = "10.14.229.26";
	//          ftp.connect(server);
	//          System.out.println("Connected to " + server + ".");
	//          System.out.print(ftp.getReplyString());
	//
	//          // After connection attempt, you should check the reply code to verify
	//          // success.
	//          reply = ftp.getReplyCode();
	//
	//          if (!FTPReply.isPositiveCompletion(reply)) {
	//        	 System.out.print("Nije se ukonektovao");
	//            ftp.disconnect();
	//            System.err.println("FTP server refused connection.");
	//            System.exit(1);
	//          }
	//          
	//          ftp.login("Stampasistem", "STisp2wr!Ni!u");
	//          System.out.print("Ulogovao se na ftp");
	//          File file = new File("StampasistemP_Stampasistem_20240529.csv");
	//
	//          ftp.storeFile("/Stampasistem/StampasistemP_Stampasistem_20240529.csv", new FileInputStream(file));
	//          System.out.print("prebacio fajl na ftp ");
	//          ftp.logout();
	//          System.out.print("logout sa ftp-a ");
	//        } catch (IOException e) {
	//        	System.out.print("IOException, details: " + e.getMessage());
	//          error = true;
	//          e.printStackTrace();
	//        } finally {
	//          if (ftp.isConnected()) {
	//            try {
	//              ftp.disconnect();
	//            } catch (IOException ioe) {
	//              // do nothing
	//            }
	//          }
	//          System.exit(error ? 1 : 0);
	//        }
	//    }
}
