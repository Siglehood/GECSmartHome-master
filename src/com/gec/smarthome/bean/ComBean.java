package com.gec.smarthome.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 串口数据
 * 
 * @author Sig
 * @version 1.1
 */
public class ComBean {
	public String sComPort = null;
	public String bRec = null;
	public String sRecTime = null;

	public ComBean(String sComPort, String bRec) {
		this.sComPort = sComPort;
		this.bRec = bRec;
		sRecTime = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date());
	}
}
