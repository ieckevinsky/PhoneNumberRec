package com.hylanda.phoneNum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hylanda.phoneNum.PhoneNumRec.PhoneNum;

//正则表达式

public class RegularlyFileter {   
	
	public boolean satisfiyRules(String Text ){
		 
		 Pattern pattern = Pattern.compile("[0-9]{4}[/|-][0-9]{1,2}[/|-][0-9]{1,2}"); 
		 Matcher matcher = pattern.matcher(Text);  
	     //Matcher matcher = pattern.matcher("yuyun158 2017/04/27 00:0");  	           
	     // String dateStr = null;  
	        if(matcher.find()){  
	         // dateStr = matcher.group();  
	
	          return true;
	        }  
	          return false;
 
	        
	}
 
	
	public boolean backgroundSatisfiyRules(PhoneNum pnum){
		 
		 Pattern pattern = Pattern.compile("[0-9]{4}[/|-][0-9]{1,2}[/|-][0-9]{1,2}"); 
		 Matcher matcher = pattern.matcher(pnum.originaltext);  
	     //Matcher matcher = pattern.matcher("yuyun158 2017/04/27 00:0");  	           
	     // String dateStr = null; 
		 int start = pnum.originaltext.indexOf(pnum.pthonenumber);
		 int end = start + pnum.pthonenumber.length();
		 if (start>0) {
			 if (charisNumber(pnum.originaltext.substring(start-1, start))) {				 
				 
				 return true;
			}
			 			  
		}
		 if(matcher.find()){  
	         // dateStr = matcher.group();  	        	
	        	if (matcher.start()>=end || matcher.end()<=start) {
	        		return false;
				}else{
					return true;
				}
	        		          
	        } 
	          return false;

	        
	}

	
	private  boolean charisNumber(String Text){
		for (int i = 0; i < Text.length(); i++){
			  // System.out.println(Text.charAt(i));
			   if (!Character.isDigit(Text.charAt(i))){
			   		return false;
			   }
		}
		return true;
	}
}
