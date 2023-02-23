package com.opcTjyx.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javafish.clients.opc.JOpc;
import javafish.clients.opc.SynchReadItemExample;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;

@Controller
@RequestMapping("/variant")
public class VariantController {
	
	static {
		System.out.println("111111111111111");
		JOpc.coInitialize();   //初始化JOpc        JOpc继承父类JCustomOpc
		
	}
	
	@RequestMapping(value="/goTest")
	public String goTest(HttpServletRequest request) {
		
		//http://localhost:8080/OPCTjyx/variant/goTest
		
		return "test";
	}

	@RequestMapping(value="/getTaiDaPlc1")
	@ResponseBody
	public void getTaiDaPlc1() {

		SynchReadItemExample test = new SynchReadItemExample();
		
		/**
		 * 创建连接对象
		 * 127.0.0.1  				 opcService的地址,opcService在本地就填写本地地址
		 * KingView.View.1     opcService在系统注册表中的注册名,  组态王中的opcService默认名KingView.View.1,如果你不是使用组态王中的opcService那么请修改你要连接的注册名(opcService服务名称)
		 * JOPC1						OPC客户端的用户名---按你喜欢来填
		 */
		JOpc jopc = new JOpc("127.0.0.1", "Kepware.KEPServerEX.V6", "M3N881PM37O1M1D");
		
		OpcGroup group = new OpcGroup("chanel1.device1._System", true, 500, 0.0f);
		
		// new Opcitem("K1.Value",true,"");    "K1.Value"  表示要读取opc服务器中的变量名称的值。
		group.addItem(new OpcItem("chanel1.device1._System._Error", true, ""));      
		group.addItem(new OpcItem("chanel1.device1._System._NoError", true, ""));
		group.addItem(new OpcItem("chanel1.device1._System._ScanMode", true, ""));
		group.addItem(new OpcItem("chanel1.device1._System._EncapsulationPort", true, ""));
		
		jopc.addGroup(group);   //添加组
		OpcGroup responseGroup;

		try {
			jopc.connect();   //连接
			jopc.registerGroups();  //注册组
		} catch (ConnectivityException e1) {
			System.out.println("ConnectivityException="+e1.getMessage());
			//logger.error(e1.getMessage());
		} catch (UnableAddGroupException e) {
			System.out.println("UnableAddGroupException="+e.getMessage());
			//logger.error(e.getMessage());
		} catch (UnableAddItemException e) {
			System.out.println("UnableAddItemException="+e.getMessage());
			//logger.error(e.getMessage());
		}
		synchronized(test) {
			try {
				test.wait(50);
			} catch (InterruptedException e) {
				//logger.error(e.getMessage());
			}
		}
		//同步读取
		while (true) {
			try {
				synchronized(test) {
					test.wait(5000);
				}
				responseGroup = jopc.synchReadGroup(group);
				ArrayList<OpcItem> opcItems = responseGroup.getItems();
				for (OpcItem opcItem : opcItems) {
					System.out.println("Item名:" + opcItem.getItemName()  +  "  Item值: " + opcItem.getValue().toString());
				}
			} catch (ComponentNotFoundException e) {
				//logger.error(e.getMessage()); //获取responseGroup错误
				JOpc.coUninitialize();     //错误关闭连接
			}catch (InterruptedException e) {
				//logger.error(e.getMessage());
				JOpc.coUninitialize(); //错误关闭连接
			} catch (SynchReadException e) {
				e.printStackTrace();
			}
		}
	}
}
