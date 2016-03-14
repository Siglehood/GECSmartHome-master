/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.gec.smarthome.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * @author Sig
 * @version 1.1
 *
 */
public class SerialPort {
	private static final String TAG = SerialPort.class.getSimpleName();
	// Do not remove or rename the field mFd: it is used by native method
	// close().
	private FileDescriptor mFd = null;
	private FileInputStream mFileInputStream = null;
	private FileOutputStream mFileOutputStream = null;

	static {
		System.loadLibrary("serial_port");
	}

	public SerialPort(File device, int baudrate, int flags) {
		// Check access permission.
		if (!device.canRead() || !device.canWrite()) {
			// Missing read/write permission, trying to chmod the file
			Process process;
			try {
				process = Runtime.getRuntime().exec("su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
				process.getOutputStream().write(cmd.getBytes());
			} catch (Exception e) {
				throw new RuntimeException("输入输出异常", e);
			}
		}
		Log.d(TAG, "open start device path=" + device.getAbsolutePath());
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		Log.d(TAG, "open end");
		if (mFd == null)
			Log.e(TAG, "native open returns null");
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);

	public native void close();
}
