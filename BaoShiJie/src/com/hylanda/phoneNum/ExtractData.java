package com.hylanda.phoneNum;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.hylanda.wumanbertrie.WumanberTrie;

public class ExtractData {
	static String selectsql = null;
	static ResultSet retsult = null;
	public static final String url = "jdbc:mysql://rdsj7revmm26jvv.mysql.rds.aliyuncs.com/bisdata";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "bis_data";
	public static final String password = "bis_data_hylanda";

	public static Connection conn = null;
	public static PreparedStatement pst = null;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub		 
		FileOutputStream writerStream = new FileOutputStream("D:/读写txt/baoshijie/手机号识别/0426data.txt");    
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8")); 
		int count = 0;
		//int paraCount = 44;  
		int paraCount = 1; 
		//selectsql = "select * from compare_result where result_loc_content ='less'  ";// SQL 
		//for(int j = 26 ; j< 27;j++){
//			if (j<10) {
//				selectsql = "select text,title from D2017040"+j;
//			}else{
//				selectsql = "select text,title from D201704"+j;
//			}
		//selectsql = "select text from D20170427 LIMIT 20000 OFFSET 1";
		selectsql = "select text from D20170428 LIMIT 200000 OFFSET 1";
		 System.out.println("statical D20170428	");
		//selectsql = "select clueSpec,clueAbs from D20170425"; 
		try {
			Class.forName(name); 
			conn = DriverManager.getConnection(url, user, password); 
			pst = conn.prepareStatement(selectsql); 
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			retsult = pst.executeQuery(); 
			System.out.println("begin ...");
			while (retsult.next()) {	
				String[] paras = new String[paraCount];
				for (int i = 0; i < paraCount; i++) {
					 
					 paras[i] = retsult.getString(i+1).replace("\r\n", "");

				}
				if (paras[0].equals("") ) {
					
				}else{
					count++;
					if (count % 1000 ==0) {
						System.out.println("line number : "+count);
					}
					System.out.println(paras[0]);
					String output = paras[0].replace("\n", "");
					if (!output.equals("")) {
						writer.write(output+"\r\n");
						 writer.flush();
					}
					 
				}
			//System.out.println(paras.toString());
			} 
			writer.close();
			retsult.close();
			conn.close(); 
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
 
//	}
		 
	
	}

}
