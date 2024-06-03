package com.devellop.mts.settlement.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;


public class FTPUtil {

	private static Logger logger = Logger.getLogger(FTPUtil.class);

	private String _ftp_server = "";
	private String _username = "";
	private String _password = "";
	private String _dir_local = "";
	private String _dir_remote = "";


	public FTPUtil(String ftp_server, String username, String password, String dir_remote, String dir_local) {
		_ftp_server = ftp_server;
		_username = username;
		_password = password;
		_dir_local = dir_local;
		_dir_remote = dir_remote;

	}

	public void upload(String to_upload){

		logger.info( "--> uploadFile = " + to_upload);

		FTPClient ftp = new FTPClient();
		//FTPClientConfig config = new FTPClientConfig();
		boolean error = false;
		try {
			int reply;

			ftp.connect(_ftp_server);
			logger.info("Connected to " + _ftp_server + ".");
			logger.info(ftp.getReplyString());

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				logger.error("Nije se ukonektovao");
				ftp.disconnect();
				logger.error("FTP server refused connection.");
				System.exit(1);
			}

			ftp.login(_username, _password);
			System.out.print("Ulogovao se na ftp");
			File file = new File(_dir_local + to_upload);
			//File file = new File("StampasistemP_Stampasistem_20240529.csv");
			ftp.storeFile(_dir_remote + to_upload, new FileInputStream(file));
			//ftp.storeFile("/Stampasistem/StampasistemP_Stampasistem_20240529.csv", new FileInputStream(file));
			System.out.print("prebacio fajl na ftp ");
			ftp.logout();
			System.out.print("logout sa ftp-a ");
		} catch (IOException e) {
			System.out.print("IOException, details: " + e.getMessage());
			error = true;
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			System.exit(error ? 1 : 0);
		}

	}

	public void download(String remote_file) 
			throws IOException 
	{ 


		logger.info( "--> download = " + remote_file);

		String localFilePath = _dir_local + remote_file;
		String remoteFilePath = _dir_remote + remote_file;

		FTPClient ftp = new FTPClient();
		//FTPClientConfig config = new FTPClientConfig();
		boolean error = false;
		try {
			int reply;

			ftp.connect(_ftp_server);
			logger.info("Connected to " + _ftp_server + ".");
			logger.info(ftp.getReplyString());

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				logger.error("Nije se ukonektovao");
				ftp.disconnect();
				logger.error("FTP server refused connection.");
				System.exit(1);
			}

			ftp.login(_username, _password);
			System.out.print("Ulogovao se na ftp");

			//File file = new File("StampasistemP_Stampasistem_20240529.csv");



			System.out.println("Downloading file " + remoteFilePath + " to " +
					localFilePath);

			OutputStream outputStream =
					new BufferedOutputStream(new FileOutputStream(localFilePath));
			if (!ftp.retrieveFile(remoteFilePath, outputStream))
			{
				System.out.println("Failed to download file " + remoteFilePath);
			}
			outputStream.close();


			//ftp.storeFile("/Stampasistem/StampasistemP_Stampasistem_20240529.csv", new FileInputStream(file));
			System.out.print("downloadovao fajl sa ftp ");
			ftp.logout();
			System.out.print("logout sa ftp-a ");
		} catch (IOException e) {
			System.out.print("IOException, details: " + e.getMessage());
			error = true;
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			System.exit(error ? 1 : 0);
		}

	} 



}
