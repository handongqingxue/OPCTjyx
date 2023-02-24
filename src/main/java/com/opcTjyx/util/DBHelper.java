package com.opcTjyx.util;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
/** ���ݿ��װ������ */
public final class DBHelper {
	private DBHelper() {}
	/** �����ַ��� */
	private static String url = "jdbc:sqlserver://localhost:1433;DatabaseName=OPCTjyx";
	/** ���ݿ��û��� */
	private static String user = "sa";
	/** ���ݿ����� */
	private static String pwd = "123456";
	/** ��̬����,ע��ͬһʱ��ִ��һ������ */
	private static Statement st;
	/** ��̬��,��ʵ���Ǿ�̬���췽�� */
	static
	{
		try {
			DriverManager.registerDriver(new SQLServerDriver());
			Connection conn = DriverManager.getConnection(url,user,pwd);
			st	= conn.createStatement();
		} catch (SQLException e) {
			System.out.println("ע�����ݿ������쳣");
		}
	}
	/** ���Ҳ�ѯ��SQL����,���㷵�ش�����.����쳣����null */
	public static ResultSet doQuery(String sql)
	{
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
		} catch (Exception e) {
			System.out.println("��ѯ��������쳣--"+sql);
		}
		return rs;
	}
	/** �������ݸ��µ�SQL����,���㷵��Ӱ�������.����쳣����0 */
	public static int doUpdate(String sql)
	{
		int i = 0;
		try {
			i = st.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println("������������쳣--"+sql);
		}
		return i;
	}
}









