package com.hylanda.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import com.hylanda.htmlparser.HtmlToText;
import com.hylanda.phoneNum.PhoneNumRec;
import com.hylanda.phoneNum.PhoneNumRec.PhoneNum;

public class TestPhoneNum {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		PhoneNumRec phoneNumRec = new PhoneNumRec().getInstance();
		phoneNumRec.init("D:/读写txt/baoshijie/手机号识别/phone.txt", "D:/读写txt/baoshijie/手机号识别/transferDict.txt");
		String LineTxt = "";

		LineTxt = "有意咨询17191097722<div layer1=";
		String Textorg = HtmlToText.getFormatText_SAX(LineTxt);
		String Text = phoneNumRec.normalizeText(Textorg); // 字符转换
		List<PhoneNum> testlisttemp = phoneNumRec.findPhoneNum(Text); // 确定开始位置
		List<PhoneNum> testlistresult = phoneNumRec.ConfirmPhoneNum(Textorg, testlisttemp); // 检查候选是否满足条件,传入textorg为为进行字符替换的原始字符串，防止因字符转换带来的错判
		testlistresult = phoneNumRec.cleanRes(testlistresult); // 结果噪音消除
		for (PhoneNum pNum : testlistresult) {
			System.out.println(pNum.pthonenumber + "\t" + pNum.StartPos + "\t" + pNum.EndPos + "\t" + pNum.originaltext);
		}

	}

}
