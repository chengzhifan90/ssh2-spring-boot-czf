package com.example.connectionUtils;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author czf
 * @date 2018/6/22 14:14
 * 连接服务器工具类
 */
public class ConnectionUtil {

    private String host;
    private String user;
    private String password;
    private int port;
    private int maxWaitTime;
    private String keyfile;
    private String passphrase;
    private boolean sshKey;
    private ChannelSftp sftp;
    private ChannelExec channelExec;
    private Session session;
    private String name;

    public ConnectionUtil(String host, String user, String password, int port,
                       int maxWaitTime) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.maxWaitTime = maxWaitTime;
        this.keyfile = null;
        this.passphrase = null;
        this.sshKey = false;
    }

    public ConnectionUtil(String host, String user, int port, int maxWaitTime,
                       String keyfile, String passphrase) {
        this.host = host;
        this.user = user;
        this.password = null;
        this.port = port;
        this.maxWaitTime = maxWaitTime;
        this.keyfile = keyfile;
        this.passphrase = passphrase;
        this.sshKey = true;
    }

    public void open() {
        JSch jclient = new JSch();
        try {
            session = jclient.getSession(this.user, this.host, this.port);
            session.setUserInfo(new UserInfo() {

                public String getPassphrase() {
                    return null;
                }

                public String getPassword() {
                    return password;
                }

                public boolean promptPassphrase(String arg0) {
                    return true;
                }

                public boolean promptPassword(String arg0) {
                    return true;
                }

                public boolean promptYesNo(String arg0) {
                    return true;
                }

                public void showMessage(String arg0) {
                }
            });
            session.setTimeout(maxWaitTime);
            session.connect();

        } catch (JSchException j) {
            j.printStackTrace();
        }
    }

    //用于开启sftp服务，如果需要读取文件，就得开启sftp服务。
    public void opensftp(){
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        if (channelExec != null) {
            channelExec.disconnect();
            channelExec = null;
        }
        if (sftp != null) {
            sftp.disconnect();
            sftp = null;
        }
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public String stat(String path) throws SftpException {
//      System.out.println(sftp.lstat(path));// 查看path的文件信息
        return sftp.lstat(path).toString();
    }

    //把命令变成字符串，在目标服务器上运行，返回List结果集
    public List cmd(String cmd, String charset) {
        InputStream in = null;
        BufferedReader reader = null;
        List catalog = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.setInputStream(null);
            channelExec.connect();

            in = channelExec.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,
                    Charset.forName(charset)));
            String buf = null;
            catalog = new ArrayList<String>();
            while ((buf = reader.readLine()) != null) {
                catalog.add(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                in.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            channelExec.disconnect();
        }
        return catalog;
    }

//测试用
/*try{
			JSch jsch=new JSch();
			Session session = jsch.getSession(USER,HOST,DEFAULT_SSH_PORT);
			session.setPassword(PASSWORD);

			UserInfo userInfo = new UserInfo() {
				@Override
				public String getPassphrase() {
					System.out.println("getPassphrase");
					return null;
				}
				@Override
				public String getPassword() {
					System.out.println("getPassword");
					return null;
				}
				@Override
				public boolean promptPassword(String s) {
					System.out.println("promptPassword:"+s);
					return false;
				}
				@Override
				public boolean promptPassphrase(String s) {
					System.out.println("promptPassphrase:"+s);
					return false;
				}
				@Override
				public boolean promptYesNo(String s) {
					System.out.println("promptYesNo:"+s);
					return true;//notice here!
				}
				@Override
				public void showMessage(String s) {
					System.out.println("showMessage:"+s);
				}
			};

			session.setUserInfo(userInfo);

			// It must not be recommended, but if you want to skip host-key check,
			// invoke following,
			// session.setConfig("StrictHostKeyChecking", "no");

			//session.connect();
			session.connect(30000);   // making a connection with timeout.

			Channel channel=session.openChannel("shell");

			// Enable agent-forwarding.
			//((ChannelShell)channel).setAgentForwarding(true);

			channel.setInputStream(System.in);
      *//*
      // a hack for MS-DOS prompt on Windows.
      channel.setInputStream(new FilterInputStream(System.in){
          public int read(byte[] b, int off, int len)throws IOException{
            return in.read(b, off, (len>1024?1024:len));
          }
        });
       *//*

			channel.setOutputStream(System.out);
			System.out.println(System.out.toString()+"test");

      *//*
      // Choose the pty-type "vt102".
      ((ChannelShell)channel).setPtyType("vt102");
      *//*

      *//*
      // Set environment variable "LANG" as "ja_JP.eucJP".
      ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
      *//*

			//channel.connect();
			channel.connect(3*1000);
		}
		catch(Exception e){
			System.out.println(e);
		}*/


}
