package com.itwuzhifei.reggie.utils;

import com.itwuzhifei.reggie.common.CustomerException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送邮件验证码
	 * @param javaMailSender 邮件发送核心对象
	 * @param from 发送者
	 * @param emailReceiver 接收者
	 * @param code 验证码
	 */
	public static void sendMessage(JavaMailSender javaMailSender, String from, String emailReceiver, String code){
		// 构建一个邮件对象
		SimpleMailMessage message = new SimpleMailMessage();
		// 设置邮件发送者
		message.setFrom(from);
		// 设置邮件接收者
		message.setTo(emailReceiver);
		// 设置邮件的主题
		message.setSubject("【瑞吉外卖】登录验证码");
		// 设置邮件的正文
		String text = "您的验证码为：" + code + ",请勿泄露给他人。";
		message.setText(text);
		// 发送邮件
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			throw new CustomerException("发送失败");
		}
	}

}
