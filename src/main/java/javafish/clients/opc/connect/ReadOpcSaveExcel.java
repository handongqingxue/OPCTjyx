package javafish.clients.opc.connect;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import javafish.clients.opc.JOpc;
import javafish.clients.opc.SynchReadItemExample;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.variant.Variant;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 读取opc服务端
 * @author Administrator
 * @param <V>
 */
public class ReadOpcSaveExcel {
	
	//private static final String DZ="chanel1.device1._System._Error";
	//private static final String HLD="chanel1.device1._System._NoError";
	private static String dz;
	private static String hld;
	private static final int LOCALHOST=1;
	private static final int SERVER=2;
	private static int local=2;
	private static SimpleDateFormat dateSDF=new SimpleDateFormat("YYYYMMdd");
	private static SimpleDateFormat timeSDF=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	private static JOpc jopc;
	private static OpcGroup group;

	public static void main(String[] args) {

		SynchReadItemExample test = new SynchReadItemExample();
		JOpc.coInitialize();   //初始化JOpc        JOpc继承父类JCustomOpc
		
		/**
		 * 创建连接对象
		 * 127.0.0.1  				 opcService的地址,opcService在本地就填写本地地址
		 * KingView.View.1     opcService在系统注册表中的注册名,  组态王中的opcService默认名KingView.View.1,如果你不是使用组态王中的opcService那么请修改你要连接的注册名(opcService服务名称)
		 * JOPC1						OPC客户端的用户名---按你喜欢来填
		 */
		if(local==LOCALHOST) {
			dz="chanel1.device1._System._Error";
			hld="chanel1.device1._System._NoError";
			
			jopc = new JOpc("127.0.0.1", "Kepware.KEPServerEX.V6", "M3N881PM37O1M1D");
			group = new OpcGroup("chanel1.device1._System", true, 500, 0.0f);
			// new Opcitem("K1.Value",true,"");    "K1.Value"  表示要读取opc服务器中的变量名称的值。
			group.addItem(new OpcItem("chanel1.device1._System._Error", true, ""));      
			group.addItem(new OpcItem("chanel1.device1._System._NoError", true, ""));
		}
		else if(local==SERVER) {
			dz="ks.weight.1#dz";
			hld="ks.weight.1#hld";
			
			jopc = new JOpc("127.0.0.1", "Kepware.KEPServerEX.V6", "DESKTOP-POBS1HN");
			group = new OpcGroup("ks.weight", true, 500, 0.0f);
			group.addItem(new OpcItem("ks.weight.1#dz", true, ""));      
			group.addItem(new OpcItem("ks.weight.1#hld", true, ""));
		}
		
		jopc.addGroup(group);   //添加组
		OpcGroup responseGroup;

		//https://www.cnblogs.com/minixiong/p/11149281.html
		int rowNum=0;
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		try {
			jopc.connect();   //连接
			jopc.registerGroups();  //注册组
			
			wb=new HSSFWorkbook();
			sheet = wb.createSheet("KepServer变量查询");
			row = sheet.createRow(rowNum);
			HSSFCellStyle style = wb.createCellStyle();
			cell = row.createCell(0);
			cell.setCellValue("时间");
			cell.setCellStyle(style);
			
			cell=row.createCell(1);
			cell.setCellValue("道闸变量值");
			cell.setCellStyle(style);
			
			cell=row.createCell(2);
			cell.setCellValue("红绿灯变量值");
			cell.setCellStyle(style);
			
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
					test.wait(50);
				}
				responseGroup = jopc.synchReadGroup(group);
				int dzValue=0;
				int hldValue=0;
				ArrayList<OpcItem> opcItems = responseGroup.getItems();
				int index=0;
				for (OpcItem opcItem : opcItems) {
					String itemName = opcItem.getItemName();
					Variant value = opcItem.getValue();
					int inte = value.getInteger();
					if(dz.equals(itemName)) {
						dzValue=inte;
					}
					else if(hld.equals(itemName)) {
						hldValue=inte;
					}
					index++;
					//System.out.println("Item名:" + itemName  +  "  Item值: " + value);
				}
				
				if(dzValue==1||hldValue==1) {
					StringBuilder jsonSB=new StringBuilder();
					Date date = new Date();
					String time = timeSDF.format(date);
					jsonSB.append("{");
						jsonSB.append("\"time\":\""+time+"\",");
						jsonSB.append("\"dz\":\""+dzValue+"\",");
						jsonSB.append("\"hld\":\""+hldValue+"\"");
					jsonSB.append("}");
					//System.out.println("jsonSB==="+jsonSB.toString());
					JSONObject groupJSON = JSON.parseObject(jsonSB.toString());
					
					row=sheet.createRow(++rowNum);
				
					cell = row.createCell(0);
					Object timeObj = groupJSON.get("time");
					if(timeObj!=""&&timeObj!=null)
						cell.setCellValue(timeObj.toString());
					
					cell = row.createCell(1);
					Object dzObj = groupJSON.get("dz");
					if(dzObj!=""&&dzObj!=null)
						cell.setCellValue(dzObj.toString());
					
					cell = row.createCell(2);
					Object hldObj = groupJSON.get("hld");
					if(hldObj!=""&&hldObj!=null)
						cell.setCellValue(hldObj.toString());
	
					//输出Excel文件
					FileOutputStream output=new FileOutputStream("d:/opcapp/workbook/"+dateSDF.format(date)+".xls");
					wb.write(output);
					output.flush();
				}
			} catch (ComponentNotFoundException e) {
				//logger.error(e.getMessage()); //获取responseGroup错误
				JOpc.coUninitialize();     //错误关闭连接
			}catch (InterruptedException e) {
				//logger.error(e.getMessage());
				JOpc.coUninitialize(); //错误关闭连接
			} catch (SynchReadException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
