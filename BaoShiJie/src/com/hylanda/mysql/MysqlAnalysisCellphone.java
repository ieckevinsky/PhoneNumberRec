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
public class MysqlAnalysisCellphone {
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
		File file = new File("D:/读写txt/baoshijie/blackname.txt");
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
		File file = new File("D:/读写txt/baoshijie/blackname.txt");
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
					int iStart = p.offset;			//起始位置
					int iEnd = p.offset + p.len;	//结束位置
					int iScore = p.pattern.value;  //类别		 
			
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
		 
		FileWriter writer = new FileWriter("D:/读写txt/baoshijie/手机号识别/oldandnew/D20170602.txt");
		int count = 0;
		int paraCount = 2; 
		selectsql = "select text,ContentContact from D20170602 where ContentContact like '%Cellphone%'";
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
					paras[i] = retsult.getString(i+1);
		 
				}
				if (paras[0].equals("") && paras[1].equals("")) {
					
				}else{
					count++;
					if (count % 1000 ==0) {
						System.out.println("line number : "+count);
					}
					System.out.println(paras[0].replace("\n","")+"\t"+paras[1].replace("\n",""));
					//String output = paras[0]+"\t"+paras[1];
					writer.write(paras[0].replace("\n","").replace("\r\n", "").replace("\t", "")+"\t"+paras[1].replace("\n",";").replace("\r\n", ";").replace("\t", "")+"\r\n");
					writer.flush();
				}
			//System.out.println(paras.toString());
			} 

			retsult.close();
			conn.close(); 
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 
	 writer.close();
		 
	}

}
