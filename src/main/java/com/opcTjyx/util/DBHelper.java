package com.opcTjyx.util;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
/** 数据库封装助手类 */
public final class DBHelper {
	private DBHelper() {}
	/** 连接字符串 */
	private static String url = "jdbc:sqlserver://localhost:1433;DatabaseName=OPCTjyx";
	/** 数据库用户名 */
	private static String user = "sa";
	/** 数据库密码 */
	private static String pwd = "123456";
	/** 静态载体,注意同一时间执行一条命令 */
	private static Statement st;
	/** 静态块,其实就是静态构造方法 */
	static
	{
		try {
			DriverManager.registerDriver(new SQLServerDriver());
			Connection conn = DriverManager.getConnection(url,user,pwd);
			st	= conn.createStatement();
		} catch (SQLException e) {
			System.out.println("注册数据库驱动异常");
		}
	}
	/** 给我查询的SQL命令,给你返回处理结果.如果异常返回null */
	public static ResultSet doQuery(String sql)
	{
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
		} catch (Exception e) {
			System.out.println("查询命令出现异常--"+sql);
		}
		return rs;
	}
	/** 给我数据更新的SQL命令,给你返回影响的行数.如果异常返回0 */
	public static int doUpdate(String sql)
	{
		int i = 0;
		try {
			i = st.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("更新命令出现异常--"+sql);
		}
		return i;
	}
}









