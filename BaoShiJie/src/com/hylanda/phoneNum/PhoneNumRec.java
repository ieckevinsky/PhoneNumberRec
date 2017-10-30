package com.hylanda.phoneNum;

import java.awt.image.BufferedImageFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;

import javax.sound.sampled.Line;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hylanda.common.trie.MatchPattern;
import com.hylanda.common.trie.Pattern;
import com.hylanda.htmlparser.HtmlToText;
import com.hylanda.wumanbertrie.WumanberTrie;

/*
 * 1、25个字节内，含有11位数字，且以136、150(中间可以间断)等为开头（共27类，该27类可提供）的字符串：
<1>.136、150等开头前面必须是非数字；
<2>.136、150等开头前面为0或+86的数字；
<3>.136、150等开头后面第11位数字结束后必须是非数字；
2、数字需要做变形转化，即全角、半角、大写汉字、圈文字
 * */
public class PhoneNumRec {
	private static  Logger logger = LoggerFactory.getLogger(PhoneNumRec.class);
	private static WumanberTrie<Integer> phoneNumTrie = new WumanberTrie<Integer>();
	private static WumanberTrie<String> replacechar = new WumanberTrie<String>();
	private static RegularlyFileter regularlyFileter = new RegularlyFileter();
	private static Set<String> sethead = new HashSet<String>();
	private static PhoneNumRec instance = new PhoneNumRec();
	public static PhoneNumRec getInstance() {
		return instance;
	}
	public static void main(String[] args) throws IOException, IOException {
//		// TODO Auto-generated method stub
//		FileOutputStream writerstream = new FileOutputStream("D:/读写txt/baoshijie/手机号识别/resultacc.txt");
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerstream,"utf-8"));
//		
//		init("D:/读写txt/baoshijie/手机号识别/phone.txt","D:/读写txt/baoshijie/手机号识别/transferDict.txt");
//		File file = new File("D:/读写txt/baoshijie/手机号识别/0426data.txt");
//		InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"utf-8");
//		BufferedReader read = new BufferedReader(reader);
//		String LineTxt = "";
//		int count = 0;
//		while((LineTxt = read.readLine())!=null){
//			count++;
//			LineTxt = "有意咨询17191097722<div layer1=";
//			//System.out.println(LineTxt);
//			String Textorg = HtmlToText.getFormatText_SAX(LineTxt);
//			String Text = normalizeText(Textorg);   //字符转换
//			List<PhoneNum> testlisttemp = findPhoneNum(Text);   //确定开始位置
//			List<PhoneNum> testlistresult = ConfirmPhoneNum(Textorg , testlisttemp );   // 检查候选是否满足条件,传入textorg为为进行字符替换的原始字符串，防止因字符转换带来的错判
//			testlistresult = cleanRes(testlistresult);		//结果噪音消除
//			for(PhoneNum pNum : testlistresult){
//				System.out.println(count+"\t"+pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext);
//				writer.write(count+"\t"+pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext+"\r\n");
//				writer.flush();
//			}
//		}
//		writer.close();
//		read.close();
//				
//		

	}
	public static class PhoneNum{
		public String pthonenumber = "";
		public int StartPos = 0 ;
		public int EndPos = 0 ; 
		public String originaltext = "";
	}
	
	public void init(String readfile_phone,String readfile_transfer){	
	boolean initok = false ;
	Set<Pattern<Integer>> vKey = new HashSet<Pattern<Integer>>();
	File file = new File(readfile_phone);
	InputStreamReader read;
	try {
		read = new InputStreamReader(new FileInputStream(file),"utf-8");
		BufferedReader reader = new BufferedReader(read);
		String LineTxt = "";
		while ((LineTxt=reader.readLine())!=null) {			 			 
				 vKey.add(new Pattern<Integer>(LineTxt, 0 ));	
				 if (LineTxt.length()==3) {
					 sethead.add(LineTxt);
					 initok = true; 
				}
				 
		}
		phoneNumTrie.build(vKey);
		read.close();		
		if (initok) {
			logger.info("phone head init finish ...");
			System.out.println("init finish ...");
		}else{
			logger.info("phone head init error ...");
			System.out.println("init error ...");
		}
		initTransfer(readfile_transfer);
		
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	private void initTransfer(String readpath){
		boolean initok = false;
		Set<Pattern<String>> vKey = new HashSet<Pattern<String>>();
		File file = new File(readpath);
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(new FileInputStream(file),"utf-8");
			BufferedReader read = new BufferedReader(reader);
			String LineTxt = "";
			while ((LineTxt = read.readLine())!=null) {
				 String[] temp = LineTxt.split("\t");
				 vKey.add(new Pattern<String>(temp[0], temp[1]));	
				 initok = true ;
			}
			replacechar.build(vKey);
			read.close();
			if (initok) {
				logger.info("replacechar init finish ...");
			}else{
				logger.info("replacechar init error ...");
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//数字变性转换
	public String normalizeText(String Text){
		Set<MatchPattern<String>> setResult = new TreeSet<MatchPattern<String>>();
		replacechar.match(Text, setResult);			
		int CunCuser = 0;		
		StringBuffer output = new StringBuffer();
		int nowpos = 0 ;
		for (MatchPattern<String> p : setResult) {
			// WumanTrie
			int iStart = p.offset;			//起始位置
			int iEnd = p.offset + p.len;	//结束位置
			String iScore = p.pattern.value;  //类别		 
			
			if (CunCuser <= iStart) {	
			 
				output.append(Text.substring(nowpos, iStart));
				output.append(iScore);
				CunCuser = iEnd;
				nowpos = iEnd;
				 
			}
			
		}
		if (nowpos< Text.length()) {
			output.append(Text.substring(nowpos, Text.length()));
		}
		return output.toString();
	}
	
	public List<PhoneNum> findPhoneNum(String Text){

		List<PhoneNum> list = new ArrayList<PhoneNum>(); 
		Set<MatchPattern<Integer>> setResult = new TreeSet<MatchPattern<Integer>>();
		phoneNumTrie.match(Text, setResult);			
		int CunCuser = 0;			 
		for (MatchPattern<Integer> p : setResult) {
			// WumanTrie
			int iStart = p.offset;			//起始位置
			int iEnd = p.offset + p.len;	//结束位置
			int iScore = p.pattern.value;  //类别		 
			int stop = 0;
			if (CunCuser <= iStart) {												
				if (iStart+11 <= Text.length()) {
					boolean flag = false ;
					int count = 0 ;
					for(int i = iStart;i<Text.length();i++){						
						if (isNumber(Text.substring(i, i+1))) {
//						候选电话号每个数字前不能紧邻英文字母 
//						    if (i<Text.length() && i>iStart) {
//								if (judgeContainsStr(Text.substring(i-1, i))) {
//									break;
//								}
//							} 
							
							count++;
							if (count==11) {
								CunCuser = iEnd;
								stop = i+1 ;
								flag = true ;
								break;
							}
						}
					}
					if (flag) {
						try {
							//设置候选电话号（含杂质），检索范围大小
							int len = Text.substring(iStart, stop).toString().getBytes("UTF-8").length;
							if (len<=25) {
								PhoneNum phoneNum = new PhoneNum();
								phoneNum.pthonenumber = Text.substring(iStart, stop);
								phoneNum.StartPos = iStart;
								phoneNum.EndPos = stop;
								list.add(phoneNum);
							}							
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}												
					}
						
				}
				
			}			
		}		 
		return list;	
	}
	
	public List<PhoneNum> ConfirmPhoneNum(String Text , List<PhoneNum> list ){
		List<PhoneNum> result = new ArrayList<PhoneNum>();
		for(PhoneNum pnum : list){
			boolean endflag = false;
			boolean startflag = false;			
			//结尾约束条件
			if (Text.length()>pnum.EndPos) {
				//检验字符串结尾后是否为非数字
				if (charisNumber(Text.substring(pnum.EndPos, pnum.EndPos+1))) {
					continue;
				}else{
					endflag = true;
				}
				 
			}else{
				endflag = true;
			}
			
			//开头约束条件
			
			if (pnum.StartPos==0) {
				startflag = true ;
			}else{
				//检验开头前是否为非数字
				if (!charisNumber(Text.substring(pnum.StartPos-1,pnum.StartPos))) {
					startflag = true ;
				}else {
					//检验开头前是否为“0”
					if (Text.substring(pnum.StartPos-1,pnum.StartPos).equals("0")) {
						pnum.StartPos = pnum.StartPos-1;
						startflag = true ;
					}
					//检验开头前是否为 “+86”
					if (pnum.StartPos-3>=0) {
						if (Text.substring(pnum.StartPos-3,pnum.StartPos).equals("+86")) {
							pnum.StartPos = pnum.StartPos-3;
							startflag = true ;
						}
					} 
				}
			}
			
			if (startflag == true  && endflag == true) {
				if (!regularlyFileter.satisfiyRules(pnum.pthonenumber)) {
					result.add(pnum);
				}
				
			}
		}
		
		// 补充参考信息
		int Offset = 10;
		for(PhoneNum pNum : result){
			if (pNum.StartPos-Offset>=0) {
				String prex = Text.substring(pNum.StartPos-Offset, pNum.StartPos);
				 
				if (pNum.EndPos+Offset<=Text.length()) {
					String endstr = Text.substring(pNum.EndPos, pNum.EndPos+Offset);
					pNum.originaltext = (prex+pNum.pthonenumber+endstr).replace("\n", "");
					//System.out.println(pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext);
				}else{
					String endstr = Text.substring(pNum.EndPos,Text.length());
					pNum.originaltext = (prex+pNum.pthonenumber+endstr).replace("\n", "");
					//System.out.println(pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext);
				}
				
			}else{
				String prex = Text.substring(0, pNum.StartPos);
				//System.out.println(pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos);
				
				if (pNum.EndPos+Offset<=Text.length()) {
					String endstr = Text.substring(pNum.EndPos, pNum.EndPos+Offset);
					pNum.originaltext = (prex+pNum.pthonenumber+endstr).replace("\n", "");
					//System.out.println(pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext);
				}else{
					String endstr = Text.substring(pNum.EndPos,Text.length());
					pNum.originaltext = (prex+pNum.pthonenumber+endstr).replace("\n", "");
					//System.out.println(pNum.pthonenumber+"\t"+pNum.StartPos+"\t"+pNum.EndPos+"\t"+pNum.originaltext);
				}
			}
			
		}
		//过滤掉候选电话号字符串中含有日期的结果数据
		List<PhoneNum> resultend = new ArrayList<PhoneNum>();
		for(PhoneNum pNum : result){
			if (!regularlyFileter.backgroundSatisfiyRules(pNum)) {
				resultend.add(pNum);
			}
		}
		return resultend;
	}
	
	//检验字符串是否为纯数字
	private boolean isNumber(String Text){		 
		for (int i = 0; i < Text.length(); i++){
			 //  System.out.println(Text.charAt(i));
			   if (!Character.isDigit(Text.charAt(i))){
			   		return false;
			   }
		}
		return true;
	}
	
	//检验字符是否为数字
	private boolean charisNumber(String Text){
		for (int i = 0; i < Text.length(); i++){
			  // System.out.println(Text.charAt(i));
			   if (!Character.isDigit(Text.charAt(i))){
			   		return false;
			   }
		}
		return true;
	}
	//检验字符是否含字母
    private boolean judgeContainsStr(String cardNum) {  
    	  char   c   =   cardNum.charAt(0);   
    	  int   i   =(int)c;   
    	  if((i>=65&&i<=90)||(i>=97&&i<=122)){   
    		  return   true;   
    	  }   
    	  else{   
    		  return   false;   
    	  }
    } 
    //结果整理
    public List<PhoneNum> cleanRes(List<PhoneNum> list){
    	List<PhoneNum>  result = new ArrayList<PhoneNum>();
    			
    	for(PhoneNum pNum : list){    		 
    		StringBuffer phone = new StringBuffer();
    		for (int i = 0; i < pNum.pthonenumber.length(); i++){
  			  // System.out.println(Text.charAt(i));
  			   if (Character.isDigit(pNum.pthonenumber.charAt(i))){
  				 phone.append(pNum.pthonenumber.charAt(i));
  			   }
    		}
    		pNum.pthonenumber = phone.toString();
    		//pNum.originaltext.lastIndexOf(pNum.pthonenumber)
    		if (sethead.contains(pNum.pthonenumber.substring(0,3)) ) {
    			result.add(pNum);
			}
    		else if ((sethead.contains(pNum.pthonenumber.substring(1,4)) &&  pNum.pthonenumber.substring(0,1).equals("0") )) {
    			result.add(pNum);
			}
    	}
		return result;
    	
    }
    
}
