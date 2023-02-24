package com.opcTjyx.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.opcTjyx.entity.TaiDaPlc1;
import com.opcTjyx.util.DBHelper;

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
	
	@RequestMapping(value="/goTest")
	public String goTest(HttpServletRequest request) {
		
		//http://localhost:8080/OPCTjyx/variant/goTest
		
		return "test";
	}
	
	public static void main(String[] args) {

		SynchReadItemExample test = new SynchReadItemExample();
		JOpc.coInitialize();   //初始化JOpc        JOpc继承父类JCustomOpc
		
		/**
		 * 创建连接对象
		 * 127.0.0.1  				 opcService的地址,opcService在本地就填写本地地址
		 * KingView.View.1     opcService在系统注册表中的注册名,  组态王中的opcService默认名KingView.View.1,如果你不是使用组态王中的opcService那么请修改你要连接的注册名(opcService服务名称)
		 * JOPC1						OPC客户端的用户名---按你喜欢来填
		 */
		//JOpc jopc = new JOpc("127.0.0.1", "Kepware.KEPServerEX.V6", "M3N881PM37O1M1D");
		JOpc jopc = new JOpc("127.0.0.1", "Kepware.KEPServerEX.V5", "WIN-5BU0SQ3T97V");
		
		//OpcGroup group = new OpcGroup("chanel1.device1._System", true, 500, 0.0f);
		OpcGroup group = new OpcGroup("TAIDA.PLC1", true, 500, 0.0f);
		
		// new Opcitem("K1.Value",true,"");    "K1.Value"  表示要读取opc服务器中的变量名称的值。
		/*
		group.addItem(new OpcItem("chanel1.device1._System._Error", true, ""));      
		group.addItem(new OpcItem("chanel1.device1._System._NoError", true, ""));
		group.addItem(new OpcItem("chanel1.device1._System._ScanMode", true, ""));
		group.addItem(new OpcItem("chanel1.device1._System._EncapsulationPort", true, ""));
		*/
		
		group.addItem(new OpcItem(TaiDaPlc1.D60_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D61_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D62_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D63_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D64_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D65_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D66_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D67_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D68_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D69_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D70_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D71_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D72_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D73_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D74_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D75_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.D76_ITEM_NAME, true, ""));
		group.addItem(new OpcItem(TaiDaPlc1.M584_ITEM_NAME, true, ""));
		
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
				TaiDaPlc1 taiDaPlc1 = createTaiDaPlc1Entity(opcItems);
				if(taiDaPlc1.getM584())
					addTaiDaPlc1(taiDaPlc1);
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
	
	private static TaiDaPlc1 createTaiDaPlc1Entity(ArrayList<OpcItem> opcItems) {
		TaiDaPlc1 taiDaPlc1=new TaiDaPlc1();
		for (OpcItem opcItem : opcItems) {
			String itemName = opcItem.getItemName();
			String value = opcItem.getValue().toString();
			System.out.println("Item名:" + itemName  +  "  Item值: " + value);
			if(TaiDaPlc1.D60_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD60(value);
			else if(TaiDaPlc1.D61_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD61(value);
			else if(TaiDaPlc1.D62_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD62(value);
			else if(TaiDaPlc1.D63_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD63(value);
			else if(TaiDaPlc1.D64_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD64(value);
			else if(TaiDaPlc1.D65_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD65(value);
			else if(TaiDaPlc1.D66_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD66(value);
			else if(TaiDaPlc1.D67_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD67(value);
			else if(TaiDaPlc1.D68_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD68(value);
			else if(TaiDaPlc1.D69_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD69(value);
			else if(TaiDaPlc1.D70_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD70(value);
			else if(TaiDaPlc1.D71_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD71(value);
			else if(TaiDaPlc1.D72_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD72(value);
			else if(TaiDaPlc1.D73_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD73(value);
			else if(TaiDaPlc1.D74_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD74(value);
			else if(TaiDaPlc1.D75_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD75(value);
			else if(TaiDaPlc1.D76_ITEM_NAME.equals(itemName))
				taiDaPlc1.setD76(value);
			else if(TaiDaPlc1.M584_ITEM_NAME.equals(itemName))
				taiDaPlc1.setM584(Boolean.valueOf(value));
		}
		return taiDaPlc1;
	}
	
	private static void addTaiDaPlc1(TaiDaPlc1 taiDaPlc1) {
		Connection con = null;
		PreparedStatement pst = null;
		//第一步：加载JDBC驱动类
        try {
        	//https://blog.csdn.net/xzpaiwangchao/article/details/124505542
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//第二部：通过驱动管理器获得与数据库的连接对象（该对象为与数据库相通的管道）
			//con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=OPCTjyx","sa","123456");
			con = DriverManager.getConnection("jdbc:sqlserver://192.168.80.156:1433;DatabaseName=WMS_Barcode","zf","yongxing7.");
			//第三步：通过Connection对象获取封装了sql的PreparedStatement对象（封装了已经预编译的sql语句，效率高）
			StringBuilder sqlSB = new StringBuilder();
			sqlSB.append("insert into TaiDaPlc1 (");
			sqlSB.append(TaiDaPlc1.D60_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D61_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D62_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D63_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D64_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D65_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D66_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D67_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D68_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D69_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D70_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D71_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D72_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D73_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D74_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D75_COL_NAME);
			sqlSB.append(",");
			sqlSB.append(TaiDaPlc1.D76_COL_NAME);
			sqlSB.append(") values(?)");
			pst = con.prepareStatement(sqlSB.toString());//？：占位符
			pst.setString(TaiDaPlc1.D60_COL_LOC, taiDaPlc1.getD60());
			pst.setString(TaiDaPlc1.D61_COL_LOC, taiDaPlc1.getD61());
			pst.setString(TaiDaPlc1.D62_COL_LOC, taiDaPlc1.getD62());
			pst.setString(TaiDaPlc1.D63_COL_LOC, taiDaPlc1.getD63());
			pst.setString(TaiDaPlc1.D64_COL_LOC, taiDaPlc1.getD64());
			pst.setString(TaiDaPlc1.D65_COL_LOC, taiDaPlc1.getD65());
			pst.setString(TaiDaPlc1.D66_COL_LOC, taiDaPlc1.getD66());
			pst.setString(TaiDaPlc1.D67_COL_LOC, taiDaPlc1.getD67());
			pst.setString(TaiDaPlc1.D68_COL_LOC, taiDaPlc1.getD68());
			pst.setString(TaiDaPlc1.D69_COL_LOC, taiDaPlc1.getD69());
			pst.setString(TaiDaPlc1.D70_COL_LOC, taiDaPlc1.getD70());
			pst.setString(TaiDaPlc1.D71_COL_LOC, taiDaPlc1.getD71());
			pst.setString(TaiDaPlc1.D72_COL_LOC, taiDaPlc1.getD72());
			pst.setString(TaiDaPlc1.D73_COL_LOC, taiDaPlc1.getD73());
			pst.setString(TaiDaPlc1.D74_COL_LOC, taiDaPlc1.getD74());
			pst.setString(TaiDaPlc1.D75_COL_LOC, taiDaPlc1.getD75());
			pst.setString(TaiDaPlc1.D76_COL_LOC, taiDaPlc1.getD76());
			//第四步：执行sql语句
			int cnt = pst.executeUpdate();//插入||修改||删除返回操作记录数，即受影响的行数
			System.out.println(cnt);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally {
        	try {
				pst.close();//关闭连接管道，由内而外
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
