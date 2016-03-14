package com.gec.smarthome.bean;

/**
 * 侧滑菜单bean
 * 
 * @author Sig
 * @version 1.1
 */
public class MenuBean {
	private int iconId = 0;
	private String name = null;

	public MenuBean() {
	}

	public MenuBean(int iconId, String name) {
		this.iconId = iconId;
		this.name = name;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "MenuBean [iconId=" + iconId + ", name=" + name + "]";
	}
}
