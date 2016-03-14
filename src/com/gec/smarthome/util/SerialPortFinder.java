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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * @author Sig
 * @version 1.1
 *
 */
public class SerialPortFinder {
	private static final String TAG = SerialPortFinder.class.getSimpleName();

	private String mDriverName = null;
	private String mDeviceRoot = null;
	private Vector<File> mDevices = null;

	public class Driver {
		public Driver(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}

		public Vector<File> getDevices() {
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				File[] files = dev.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						Log.d(TAG, "Found new device: " + files[i]);
						mDevices.add(files[i]);
					}
				}
			}
			return mDevices;
		}

		public String getName() {
			return mDriverName;
		}
	}

	private Vector<Driver> mDrivers = null;

	private Vector<Driver> getDrivers() {
		if (mDrivers == null) {
			mDrivers = new Vector<Driver>();
			LineNumberReader r = null;
			try {
				r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
				String line;
				while ((line = r.readLine()) != null) {
					// Issue 3:
					// Since driver name may contain spaces, we do not extract
					// driver name with split()
					String drivername = line.substring(0, 0x15).trim();
					String[] w = line.split(" +");
					if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
						Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
						mDrivers.add(new Driver(drivername, w[w.length - 4]));
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("输入输出异常", e);
			} finally {
				try {
					r.close();
				} catch (IOException e) {
					throw new RuntimeException("输入输出异常", e);
				}
			}
		}
		return mDrivers;
	}

	public String[] getAllDevices() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		itdriv = getDrivers().iterator();
		while (itdriv.hasNext()) {
			Driver driver = itdriv.next();
			Iterator<File> itdev = driver.getDevices().iterator();
			while (itdev.hasNext()) {
				String device = itdev.next().getName();
				String value = String.format("%s (%s)", device, driver.getName());
				devices.add(value);
			}
		}
		return devices.toArray(new String[devices.size()]);
	}

	public String[] getAllDevicesPath() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		itdriv = getDrivers().iterator();
		while (itdriv.hasNext()) {
			Driver driver = itdriv.next();
			Iterator<File> itdev = driver.getDevices().iterator();
			while (itdev.hasNext()) {
				String device = itdev.next().getAbsolutePath();
				devices.add(device);
			}
		}
		return devices.toArray(new String[devices.size()]);
	}
}
