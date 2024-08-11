//package Java1.app.src.main.java.com.example.java1;
package com.example.myapplication;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

public final class FtpUtil {
    public static String uploadFile(String url,
                                    int port,
                                    String username,
                                    String password,
                                    String filename,
                                    String path,
                                    InputStream input ){
        String result = "false";
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login(username, password);

            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.out.println("login failed");
                return "login failed";

            }

            ftp.setRemoteVerificationEnabled(false);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.setBufferSize(1024);
            ftp.setControlEncoding("UTF-8");
            ftp.changeWorkingDirectory(path);
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();

            result = "true";
            System.out.println("save success");
        } catch (IOException e) {
            result = e.getMessage() + e.toString();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }
}
