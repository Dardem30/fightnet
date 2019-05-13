package com.fightnet.services;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Service
@Slf4j
public class SftpService {
    private static final String user = "roman";
    private static final String password = "2";
    private static final String host = "192.168.0.106";
    private static final int port = 22;

    public void sendFile(final FileInputStream inputStream, final String fileName) {
        try {
            final JSch jsch = new JSch();
            final Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(99999999);
            log.info("Trying to get session");
            session.connect();
            log.info("Session was created");
            final ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            log.info("Channel was created");
            sftpChannel.put(inputStream, fileName, ChannelSftp.APPEND);
            sftpChannel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error("Error during trying to send video using sftp connection", e);
        }
    }
}
