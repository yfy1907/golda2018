package com.example.administrator.goldaappnew.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.util.Log;

import com.example.administrator.goldaappnew.staticClass.StaticMember;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFTPChannel{

    public static Session session = null;
    public static Channel channel = null;
    public static String getChannel(String src,String url,String dst, int timeout){

        try{
            Log.e("准备Sftp上传的参数", src+"||"+dst);
            String ftpHost = StaticMember.SFTP_REQ_HOST;
            String port = StaticMember.SFTP_REQ_PORT;
            String ftpUserName = StaticMember.SFTP_REQ_USERNAME;
            String ftpPassword = StaticMember.SFTP_REQ_PASSWORD;
            int ftpPort = StaticMember.SFTP_DEFAULT_PORT;
            if (port != null && !port.equals("")) {
                ftpPort = Integer.valueOf(port);
            }

            JSch jsch = new JSch(); // 创建JSch对象
            session = jsch.getSession(ftpUserName, ftpHost, ftpPort); // 根据用户名，主机ip，端口获取一个Session对象
            if (ftpPassword != null) {
                session.setPassword(ftpPassword); // 设置密码
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config); // 为Session对象设置properties
            session.setTimeout(timeout); // 设置timeout时间
            session.connect(); // 通过Session建立链接
            channel = session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建立SFTP通道的连接
//            Log.e("SFTP连接","Connected successfully to ftpHost = " + ftpHost + ", asftpUserName = " + ftpUserName + ", returning: " + channel);
            //用于创建动态的文件夹
            String[] folders = url.split( "/" );
            String temp="/";
//            Log.e("文件夹目录：",url);
            for ( String folder : folders ) {
                if ( folder.length() > 0 ) {
                    try {
//                        Log.e("即将验证的文件夹：",folder);
                        temp+=folder;
                        ((ChannelSftp) channel).cd(temp);  //如果不存在就在异常中添加文件夹
                        //((ChannelSftp) channel).ls(folder);
                        temp+="/";
                    }
                    catch ( SftpException e ) {
                        ((ChannelSftp) channel).mkdir(temp);
                        temp+="/";
                        //((ChannelSftp) channel).cd( "/"+folder);
                        Log.e("创建的文件夹：",folder);
                    }
                }
            }

            //代码段1
            OutputStream out = ((ChannelSftp) channel).put("/"+url+"/"+dst, new MyProgressMonitor(), ChannelSftp.OVERWRITE); // 使用OVERWRITE模式
            byte[] buff = new byte[1024 * 256]; // 设定每次传输的数据块大小为256KB
            int read;
            if (out != null) {
                System.out.println("Start to read input stream");
                InputStream is = new FileInputStream(src);

                do {
                    read = is.read(buff, 0, buff.length);
//                    Log.e("文件的大小", ""+read);
                    if (read > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                } while (read >= 0);
                System.out.println("input stream read done.");
            }


            //((ChannelSftp) channel).put(src, dst, new MyProgressMonitor(), ChannelSftp.OVERWRITE); // 代码段2

            //((ChannelSftp) channel).put(new FileInputStream(src), dst, new MyProgressMonitor(), ChannelSftp.OVERWRITE); // 代码段3

            ((ChannelSftp) channel).quit();
            return 1+"";//成功

        }catch(Exception e)
        {
            Log.e("SFTP异常","抛出"+e.getMessage());
            return 0+"";//失败
        }

    }

    public static void closeChannel() throws Exception {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}

class MyProgressMonitor implements SftpProgressMonitor {
    private long transfered;

    @Override
    public boolean count(long count) {
        transfered = transfered + count;
        System.out.println("Currently transferred total size: " + transfered + " bytes");
        return true;
    }

    @Override
    public void end() {
        System.out.println("Transferring done.");
        try {
            SFTPChannel.closeChannel();//关闭
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        System.out.println("Transferring begin.");
    }
}