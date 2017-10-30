package com.hylanda.mysql;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;


/*
 * mysql 
 *
 * */
public class MysqlAnalysisLess {
	static String selectsql = null;
	static ResultSet retsult = null;

	public static final String url = "jdbc:mysql://rdsybvqfvybvqfv.mysql.rds.aliyuncs.com/bisconf";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "bis_conf";
	public static final String password = "bis_conf_hylanda";

	public static Connection conn = null;
	public static PreparedStatement pst = null;

	public static void main(String[] args) throws IOException {
		 FileWriter writer = new FileWriter("D:/¶ÁÐ´txt/baoshijie/blackname1.txt");
		 Set<String> set = new HashSet<String>();
		int count = 0;
		//int paraCount = 44;  
		int paraCount = 7; 
		//selectsql = "select * from compare_result where result_loc_content ='less'  ";// SQL 
		selectsql = "select itemtext,uid,id,listType,fieldlist,bw,modifyTime from tblackwhitelist where bw !=0 AND listType !=3 "; 
		try {
			Class.forName(name); 
			conn = DriverManager.getConnection(url, user, password); 
			pst = conn.prepareStatement(selectsql); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] paras = new String[paraCount];
		try {
			System.out.println("begin ...");
			retsult = pst.executeQuery(); 
			//System.out.println(retsult.getString(1));
			
			while (retsult.next()) {
				StringBuffer output = new StringBuffer();
				for (int i = 0; i < paraCount; i++) {
					paras[i] = retsult.getString(i+1);
					System.out.println(paras[i]);
					///set.add(paras[i]);
					if (i==paraCount-1) {
						output.append(retsult.getString(i+1));
					}else{
						output.append(retsult.getString(i+1)+"\t");
					}				     
				}	
				writer.write(output.toString()+"\r\n");
				writer.flush();
			//System.out.println(paras.toString());
			} 

			retsult.close();
			conn.close(); 
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		for(String str : set){
//			writer.write(str+"\r\n");
//			writer.flush();
//		}
		writer.close();
	}

}
