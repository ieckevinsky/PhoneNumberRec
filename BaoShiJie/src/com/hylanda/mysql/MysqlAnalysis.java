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
import java.util.TreeSet;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.hylanda.common.trie.MatchPattern;
import com.hylanda.common.trie.Pattern;
 
import com.hylanda.wumanbertrie.WumanberTrie;
 

 
 


/*
 * mysql 
 *
 * */
public class MysqlAnalysis {
	static String selectsql = null;
	static ResultSet retsult = null;
	public static WumanberTrie<Integer> orgDictTrie = new WumanberTrie<Integer>();
	public static final String url = "jdbc:mysql://rdsj7revmm26jvv.mysql.rds.aliyuncs.com/bisdata";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "bis_data";
	public static final String password = "bis_data_hylanda";

	public static Connection conn = null;
	public static PreparedStatement pst = null;

	private static Map<String,Integer> map = new HashMap<String,Integer>();
	private static Map<String,Integer> mapall = new HashMap<String,Integer>();
	private static void init() throws IOException, IOException{
		File file = new File("D:/��дtxt/baoshijie/blackname.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"utf-8");
		BufferedReader read = new BufferedReader(reader);
		String LineTxt = "";
		while((LineTxt =read.readLine())!=null){
			map.put(LineTxt, 0);
		}
		read.close();
	}
	private static void inittrie() throws IOException, IOException{

		Set<Pattern<Integer>> vKey = new HashSet<Pattern<Integer>>();
		File file = new File("D:/��дtxt/baoshijie/blackname.txt");
		InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");
		BufferedReader reader = new BufferedReader(read);
		String LineTxt = "";
		while ((LineTxt=reader.readLine())!=null) {			 			 
				 vKey.add(new Pattern<Integer>(LineTxt, 0 ));				 			 			 
		}
		orgDictTrie.build(vKey);
		System.out.println("init finish ...");
	
	}
	public static Map<String, Integer> SearchTrie(String Text ){
		Map<String, Integer> maptemp =new HashMap<String,Integer>();
		Set<MatchPattern<Integer>> setResult = new TreeSet<MatchPattern<Integer>>();
		orgDictTrie.match(Text, setResult);			
		int CunCuser = 0;			 
		for (MatchPattern<Integer> p : setResult) {
					// WumanTrie
					int iStart = p.offset;			//��ʼλ��
					int iEnd = p.offset + p.len;	//����λ��
					int iScore = p.pattern.value;  //���		 
			
			if (CunCuser <= iStart) {						
				CunCuser = iEnd;
				if (maptemp.containsKey(Text.substring(iStart, iEnd))) {
					maptemp.put(Text.substring(iStart, iEnd),maptemp.get(Text.substring(iStart, iEnd))+1);
				}else{
					maptemp.put(Text.substring(iStart, iEnd),1);
				}
				
			}
			
		}
		return maptemp;

	}
	public static void main(String[] args) throws IOException {
		inittrie();
		//FileWriter writer = new FileWriter("D:/��дtxt/baoshijie/D20170425.txt");
		int count = 0;
		//int paraCount = 44;  
		int paraCount = 2; 
		//selectsql = "select * from compare_result where result_loc_content ='less'  ";// SQL 
		for(int j = 27 ; j< 31;j++){
			if (j<10) {
				 selectsql = "select clueSpec,clueAbs from D2017040"+j;
				 
			}else{
				 selectsql = "select clueSpec,clueAbs from D201704"+j;
				 
			}
			
		 System.out.println("statical D201704"+j);
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
			//System.out.println(retsult.getString(1));
			while (retsult.next()) {	
				String[] paras = new String[paraCount];
				for (int i = 0; i < paraCount; i++) {
					paras[i] = retsult.getString(i+1).replace("\r\n", "");
		 
				}
				if (paras[0].equals("") && paras[1].equals("")) {
					
				}else{
					count++;
					if (count % 1000 ==0) {
						System.out.println("line number : "+count);
					}
					System.out.println(paras[0]+"\t"+paras[1]);
					String output = paras[0]+"\t"+paras[1];
					Map<String, Integer> maptemp = SearchTrie(output);
					for(Map.Entry<String, Integer> entry: maptemp.entrySet()){
						if (mapall.containsKey(entry)) {
							mapall.put(entry.getKey(),mapall.get(entry.getKey())+1);
						}else {
							mapall.put(entry.getKey(),entry.getValue());
						}
					}
				}
			//System.out.println(paras.toString());
			} 

			retsult.close();
			conn.close(); 
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(Map.Entry<String, Integer> entry: mapall.entrySet()){
			 
			// writer.write(entry.getKey()+"\t"+entry.getValue()+"\r\n");
			// writer.flush();
		}
	}
		//writer.close();
	}

}
