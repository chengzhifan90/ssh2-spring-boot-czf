package com.example.demo;

import com.example.connectionUtils.ConnectionUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@SpringBootApplication
public class DemoApplication {

	/**
	* @Author:czf
	* @Description:
	* @Date:  2018/6/22
	*/
	//用户名
	private static final String USER="*****";
	//密码
	private static final String PASSWORD="******";
	//IP
	private static final String HOST="*********";
	//端口
	private static final int DEFAULT_SSH_PORT=22;
	//最大等待时间
	private static final int MAX_WAITE_TIME=30000;

	@RequestMapping("/connection")
	String home() {
		ConnectionUtil connectionUtil = new ConnectionUtil(HOST,USER,PASSWORD,DEFAULT_SSH_PORT,MAX_WAITE_TIME);
		connectionUtil.open();
		List resultList = connectionUtil.cmd("pwd","utf-8");
		connectionUtil.close();
		return String.join(",",resultList);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
