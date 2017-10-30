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
		phoneNumRec.init("D:/��дtxt/baoshijie/�ֻ���ʶ��/phone.txt", "D:/��дtxt/baoshijie/�ֻ���ʶ��/transferDict.txt");
		String LineTxt = "";

		LineTxt = "������ѯ17191097722<div layer1=";
		String Textorg = HtmlToText.getFormatText_SAX(LineTxt);
		String Text = phoneNumRec.normalizeText(Textorg); // �ַ�ת��
		List<PhoneNum> testlisttemp = phoneNumRec.findPhoneNum(Text); // ȷ����ʼλ��
		List<PhoneNum> testlistresult = phoneNumRec.ConfirmPhoneNum(Textorg, testlisttemp); // ����ѡ�Ƿ���������,����textorgΪΪ�����ַ��滻��ԭʼ�ַ�������ֹ���ַ�ת�������Ĵ���
		testlistresult = phoneNumRec.cleanRes(testlistresult); // �����������
		for (PhoneNum pNum : testlistresult) {
			System.out.println(pNum.pthonenumber + "\t" + pNum.StartPos + "\t" + pNum.EndPos + "\t" + pNum.originaltext);
		}

	}

}
