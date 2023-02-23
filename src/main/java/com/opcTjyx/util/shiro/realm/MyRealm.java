package com.opcTjyx.util.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

//import com.cqZnxj.entity.*;
//import com.cqZnxj.dao.*;

public class MyRealm extends AuthorizingRealm {
	//@Autowired
	//private UserMapper userMapper;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		// TODO Auto-generated method stub
		System.out.println("111111111");
		return null;
	}

	/**
	 * ���˺ŵ�¼������֤
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		// TODO Auto-generated method stub
		/*
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user=new User(token.getUsername(),String.valueOf(token.getPassword()));
		User resultMsg=userMapper.get(user);
		if(token.getUsername().equals(resultMsg.getUserName())
				&&
				String.valueOf(token.getPassword()).equals(resultMsg.getPassword())){
			return new SimpleAuthenticationInfo(resultMsg,resultMsg.getPassword(),resultMsg.getUserName());  
		}else{
			throw new AuthenticationException();  
		}
		*/
		return null;
	}

}
