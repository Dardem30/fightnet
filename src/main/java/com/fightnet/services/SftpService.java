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
    public void sendVideo(final FileInputStream inputStream, final String fighter1, final String fighter2) {
        String user = "roman";
        String password = "vVzq6F5g(h";
        String host = "192.168.100.6";
        int port = 22;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(99999999);
            session.connect();
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            sftpChannel.put(inputStream, fighter1 + " " + fighter2 + ".mp4", ChannelSftp.OVERWRITE);
            sftpChannel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error("Error during trying to send video using sftp connection", e);
        }
    }
}
