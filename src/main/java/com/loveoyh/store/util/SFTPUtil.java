package com.loveoyh.store.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPUtil.class);
    
    private ChannelSftp sftp;
    
    private Session session;
    /** FTP 登录用户名*/
    private String username;
    /** FTP 登录密码*/
    private String password;
    /** 私钥 */
    private String privateKey;
    /** FTP 服务器地址IP地址*/
    private String host;
    /** FTP 端口*/
    private int port;
    
    
    /**
     * 构造基于密码认证的sftp对象
     * @param username
     * @param password
     * @param host
     * @param port
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }
    
    /**
     * 构造基于秘钥认证的sftp对象
     * @param username
     * @param host
     * @param port
     * @param privateKey
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }
    
    public SFTPUtil(){}
    
    /**
     * 连接sftp服务器
     *
     * @throws Exception
     */
    public void login(){
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
                LOGGER.info("sftp connect,path of private key file：{}" , privateKey);
            }
            LOGGER.info("sftp connect by host:{} username:{}",host,username);
            
            session = jsch.getSession(username, host, port);
            LOGGER.info("Session is build");
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            
            session.setConfig(config);
            session.connect();
            LOGGER.info("Session is connected");
            
            Channel channel = session.openChannel("sftp");
            channel.connect();
            LOGGER.info("channel is connected");
            
            sftp = (ChannelSftp) channel;
            LOGGER.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));
        } catch (JSchException e) {
            LOGGER.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}", new Object[]{host, port, e.getMessage()});
        }
    }
    
    /**
     * 关闭连接 server
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
                LOGGER.info("sftp is closed already");
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
                LOGGER.info("sshSession is closed already");
            }
        }
    }
    
    /**
     * 将输入流的数据上传到sftp作为文件
     *
     * @param directory 上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input 输入流
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            LOGGER.warn("directory is not exist");
            sftp.mkdir(directory);
            sftp.cd(directory);
        }
        sftp.put(input, sftpFileName);
        LOGGER.info("file:{} is upload successful" , sftpFileName);
    }
    
    /**
     * 上传单个文件
     *
     * @param directory
     *            上传到sftp目录
     * @param uploadFile
     *            要上传的文件,包括路径
     * @throws FileNotFoundException
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String uploadFile) throws FileNotFoundException, SftpException{
        File file = new File(uploadFile);
        upload(directory, file.getName(), new FileInputStream(file));
    }
    
    /**
     * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
     *
     * @param directory
     *            上传到sftp目录
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param byteArr
     *            要上传的字节数组
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, byte[] byteArr) throws SftpException{
        upload(directory, sftpFileName, new ByteArrayInputStream(byteArr));
    }
    
    /**
     * 将字符串按照指定的字符编码上传到sftp
     *
     * @param directory
     *            上传到sftp目录
     * @param sftpFileName
     *            文件在sftp端的命名
     * @param dataStr
     *            待上传的数据
     * @param charsetName
     *            sftp上的文件，按该字符编码保存
     * @throws UnsupportedEncodingException
     * @throws SftpException
     * @throws Exception
     */
    public void upload(String directory, String sftpFileName, String dataStr, String charsetName) throws UnsupportedEncodingException, SftpException{
        upload(directory, sftpFileName, new ByteArrayInputStream(dataStr.getBytes(charsetName)));
    }
    
    /**
     * 删除文件
     *
     * @param directory
     *            要删除文件所在目录
     * @param deleteFile
     *            要删除的文件
     * @throws SftpException
     * @throws Exception
     */
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }
    
    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @throws SftpException
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }
    
    public static void main(String[] args) throws SftpException, IOException {
        SFTPUtil sftp = new SFTPUtil("nginx", "nginx", "go2oyh.com", 22);
        sftp.login();
        //byte[] buff = sftp.download("/opt", "start.sh");
        //System.out.println(Arrays.toString(buff));
        File file = new File("G:\\Desktop\\1.png");
        InputStream is = new FileInputStream(file);
        
        sftp.upload("/usr/local/developtools/nginx/html", "test_sftp_upload.png", is);
        sftp.logout();
    }
}