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
 * 1��25���ֽ��ڣ�����11λ���֣�����136��150(�м���Լ��)��Ϊ��ͷ����27�࣬��27����ṩ�����ַ�����
<1>.136��150�ȿ�ͷǰ������Ƿ����֣�
<2>.136��150�ȿ�ͷǰ��Ϊ0��+86�����֣�
<3>.136��150�ȿ�ͷ�����11λ���ֽ���������Ƿ����֣�
2��������Ҫ������ת������ȫ�ǡ���ǡ���д���֡�Ȧ����
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
//		FileOutputStream writerstream = new FileOutputStream("D:/��дtxt/baoshijie/�ֻ���ʶ��/resultacc.txt");
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerstream,"utf-8"));
//		
//		init("D:/��дtxt/baoshijie/�ֻ���ʶ��/phone.txt","D:/��дtxt/baoshijie/�ֻ���ʶ��/transferDict.txt");
//		File file = new File("D:/��дtxt/baoshijie/�ֻ���ʶ��/0426data.txt");
//		InputStreamReader reader = new InputStreamReader(new FileInputStream(file),"utf-8");
//		BufferedReader read = new BufferedReader(reader);
//		String LineTxt = "";
//		int count = 0;
//		while((LineTxt = read.readLine())!=null){
//			count++;
//			LineTxt = "������ѯ17191097722<div layer1=";
//			//System.out.println(LineTxt);
//			String Textorg = HtmlToText.getFormatText_SAX(LineTxt);
//			String Text = normalizeText(Textorg);   //�ַ�ת��
//			List<PhoneNum> testlisttemp = findPhoneNum(Text);   //ȷ����ʼλ��
//			List<PhoneNum> testlistresult = ConfirmPhoneNum(Textorg , testlisttemp );   // ����ѡ�Ƿ���������,����textorgΪΪ�����ַ��滻��ԭʼ�ַ�������ֹ���ַ�ת�������Ĵ���
//			testlistresult = cleanRes(testlistresult);		//�����������
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
	//���ֱ���ת��
	public String normalizeText(String Text){
		Set<MatchPattern<String>> setResult = new TreeSet<MatchPattern<String>>();
		replacechar.match(Text, setResult);			
		int CunCuser = 0;		
		StringBuffer output = new StringBuffer();
		int nowpos = 0 ;
		for (MatchPattern<String> p : setResult) {
			// WumanTrie
			int iStart = p.offset;			//��ʼλ��
			int iEnd = p.offset + p.len;	//����λ��
			String iScore = p.pattern.value;  //���		 
			
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
			int iStart = p.offset;			//��ʼλ��
			int iEnd = p.offset + p.len;	//����λ��
			int iScore = p.pattern.value;  //���		 
			int stop = 0;
			if (CunCuser <= iStart) {												
				if (iStart+11 <= Text.length()) {
					boolean flag = false ;
					int count = 0 ;
					for(int i = iStart;i<Text.length();i++){						
						if (isNumber(Text.substring(i, i+1))) {
//						��ѡ�绰��ÿ������ǰ���ܽ���Ӣ����ĸ 
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
							//���ú�ѡ�绰�ţ������ʣ���������Χ��С
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
			//��βԼ������
			if (Text.length()>pnum.EndPos) {
				//�����ַ�����β���Ƿ�Ϊ������
				if (charisNumber(Text.substring(pnum.EndPos, pnum.EndPos+1))) {
					continue;
				}else{
					endflag = true;
				}
				 
			}else{
				endflag = true;
			}
			
			//��ͷԼ������
			
			if (pnum.StartPos==0) {
				startflag = true ;
			}else{
				//���鿪ͷǰ�Ƿ�Ϊ������
				if (!charisNumber(Text.substring(pnum.StartPos-1,pnum.StartPos))) {
					startflag = true ;
				}else {
					//���鿪ͷǰ�Ƿ�Ϊ��0��
					if (Text.substring(pnum.StartPos-1,pnum.StartPos).equals("0")) {
						pnum.StartPos = pnum.StartPos-1;
						startflag = true ;
					}
					//���鿪ͷǰ�Ƿ�Ϊ ��+86��
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
		
		// ����ο���Ϣ
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
		//���˵���ѡ�绰���ַ����к������ڵĽ������
		List<PhoneNum> resultend = new ArrayList<PhoneNum>();
		for(PhoneNum pNum : result){
			if (!regularlyFileter.backgroundSatisfiyRules(pNum)) {
				resultend.add(pNum);
			}
		}
		return resultend;
	}
	
	//�����ַ����Ƿ�Ϊ������
	private boolean isNumber(String Text){		 
		for (int i = 0; i < Text.length(); i++){
			 //  System.out.println(Text.charAt(i));
			   if (!Character.isDigit(Text.charAt(i))){
			   		return false;
			   }
		}
		return true;
	}
	
	//�����ַ��Ƿ�Ϊ����
	private boolean charisNumber(String Text){
		for (int i = 0; i < Text.length(); i++){
			  // System.out.println(Text.charAt(i));
			   if (!Character.isDigit(Text.charAt(i))){
			   		return false;
			   }
		}
		return true;
	}
	//�����ַ��Ƿ���ĸ
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
    //�������
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
