package com.gec.smarthome.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;

import com.gec.smarthome.bean.ComBean;

import android.util.Log;

/**
 * 串口辅助工具类
 * 
 * @author Sig
 * @version 1.1
 */
public abstract class SerialHelper {
	private SerialPort mSerialPort = null;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private OutputStreamWriter out = null;
	private InputStreamReader in = null;
	private ReadThread mReadThread = null;
	private String sPort = "/dev/ttySAC0";
	private int iBaudRate = 9600;
	private boolean isOpen = false;
	private boolean isInterrupt = false;
	private byte[] bLoopData = new byte[] { 0x30 };
	private int iDelay = 500;

	public SerialHelper(String sPort, int iBaudRate) {
		this.sPort = sPort;
		this.iBaudRate = iBaudRate;
	}

	public SerialHelper() {
		this("/dev/ttySAC0", 9600);
	}

	public SerialHelper(String sPort) {
		this(sPort, 9600);
	}

	public SerialHelper(String sPort, String sBaudRate) {
		this(sPort, Integer.parseInt(sBaudRate));
	}

	public void open() throws SecurityException, IOException, InvalidParameterException {
		Log.d("SerialHelper", "new SerialPort start sPort=" + sPort);
		Log.d("SerialHelper", "new SerialPort start iBaudRate=" + String.valueOf(iBaudRate));
		File file = new File(sPort);
		mSerialPort = new SerialPort(file, iBaudRate, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		out = new OutputStreamWriter(mOutputStream);
		in = new InputStreamReader(mInputStream);

		mReadThread = new ReadThread();
		mReadThread.start();

		isOpen = true;
	}

	public void close() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		isOpen = false;
		isInterrupt = true;
	}

	public void send(byte[] bOutArray) {
		try {
			mOutputStream.write(bOutArray);
		} catch (IOException e) {
			throw new RuntimeException("输入输出异常", e);
		}
	}

	public void sendHex(String sHex) {
		byte[] bOutArray = MyFunc.hex2ByteArr(sHex);
		send(bOutArray);
	}

	public void sendTxt(String sTxt) {
		byte[] bOutArray = sTxt.getBytes();
		send(bOutArray);
	}

	public int getBaudRate() {
		return iBaudRate;
	}

	public boolean setBaudRate(int iBaud) {
		if (isOpen) {
			return false;
		} else {
			iBaudRate = iBaud;
			return true;
		}
	}

	public boolean setBaudRate(String sBaud) {
		int iBaud = Integer.parseInt(sBaud);
		return setBaudRate(iBaud);
	}

	public String getPort() {
		return sPort;
	}

	public boolean setPort(String sPort) {
		if (isOpen) {
			return false;
		} else {
			this.sPort = sPort;
			return true;
		}
	}

	public boolean isOpen() {
		return isOpen;
	}

	public byte[] getbLoopData() {
		return bLoopData;
	}

	public void setbLoopData(byte[] bLoopData) {
		this.bLoopData = bLoopData;
	}

	public void setTxtLoopData(String sTxt) {
		this.bLoopData = sTxt.getBytes();
	}

	public void setHexLoopData(String sHex) {
		this.bLoopData = MyFunc.hex2ByteArr(sHex);
	}

	public int getiDelay() {
		return iDelay;
	}

	public void setiDelay(int iDelay) {
		this.iDelay = iDelay;
	}

	/**
	 * 向串口中写入字符串命令
	 * 
	 * @param s
	 *            字符串命令
	 * @throws Exception
	 *             异常
	 */
	public void writeln(String s) throws Exception {
		out.write(s);
		out.write('\r');
		out.flush();
	}

	/**
	 * 读取COM命令的返回字符串
	 * 
	 * @return 结果字符串
	 * @throws Exception
	 */
	public String read() throws Exception {
		int n, d;
		char c;
		String answer = "";
		for (d = 0; d < 10; d++) {
			while (in.ready()) {
				n = in.read();
				if (n != -1) {
					c = (char) n;
					answer = answer + c;
					Thread.sleep(1);
				} else
					break;
			}
			if (answer.indexOf("OK") != -1) {
				break;
			}
			Thread.sleep(100);
		}
		return answer;

	}

	public String readByRing() throws Exception {
		int n, d;
		char c;
		String answer = "";
		for (d = 0; d < 10; d++) {
			while (in.ready()) {
				n = in.read();
				if (n != -1) {
					c = (char) n;
					answer = answer + c;
					Thread.sleep(100);
				} else
					break;
			}
			if (answer.indexOf("OK") != -1) {
				break;
			}
			Thread.sleep(100);
		}
		return answer;

	}

	public String sendAT(String atcommand) {
		String s = "";
		try {
			Thread.sleep(100);
			writeln(atcommand);
			Thread.sleep(80);
			s = read();
			Thread.sleep(100);
		} catch (Exception e) {
			Log.e("SerialHelper",
					"ERROR: send AT command failed; " + "Command: " + atcommand + "; Answer: " + s + "  " + e);
		}
		return s;
	}

	private class ReadThread extends Thread {
		public boolean suspendFlag = false;// 控制线程的执行

		@Override
		public void run() {
			while (!isInterrupt) {
				synchronized (this) {
					while (suspendFlag) {
						try {
							wait();
						} catch (InterruptedException e) {
							throw new RuntimeException("线程中断异常", e);
						}
					}
				}
				if (mInputStream == null) {
					return;
				}
				String result = null;
				try {
					result = readByRing();
				} catch (Exception e) {
					throw new RuntimeException("输入输出异常", e);
				}
				if (result != null && !"".equals(result)) {
					ComBean ComRecData = new ComBean(sPort, result);
					onDataReceived(ComRecData);
				}
			}
		}

		// 线程暂停
		public void setSuspendFlag() {
			this.suspendFlag = true;
		}

		// 唤醒线程
		public synchronized void setResume() {
			this.suspendFlag = false;
			notify();
		}
	}

	public void startRead() {
		if (mReadThread != null) {
			mReadThread.setResume();
		}
	}

	public void stopRead() {
		if (mReadThread != null) {
			mReadThread.setSuspendFlag();
		}
	}

	protected abstract void onDataReceived(ComBean ComRecData);
}