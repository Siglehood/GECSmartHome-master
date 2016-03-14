package com.gec.smarthome.bean;

public class HumitureBean {
	float temp = 0.0f;
	float humidity = 0.0f;

	public HumitureBean() {
	}

	public HumitureBean(float temp, float humidity) {
		this.temp = temp;
		this.humidity = humidity;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public float getHumidity() {
		return humidity;
	}

	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	@Override
	public String toString() {
		return "HumitureBean [temp=" + temp + ", humidity=" + humidity + "]";
	}
}
