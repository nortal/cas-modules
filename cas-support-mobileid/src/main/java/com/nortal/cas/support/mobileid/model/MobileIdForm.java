/**
 * 
 */
package com.nortal.cas.support.mobileid.model;

import java.io.Serializable;

/**
 * @author Priit Liivak
 * 
 */
public class MobileIdForm implements Serializable {
	private static final long serialVersionUID = 1L;


	
	private long refreshTime;
	private long refreshCount;
	private MobileIdAuth mobileIdAuth;
	private String action;
	private boolean showAuth;
	private String phonenumber;
	
	
	public boolean isShowAuth() {
		return showAuth;
	}
	
	public void setShowAuth(boolean showAuth) {
		this.showAuth = showAuth;
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public long getRefreshCount() {
		return refreshCount;
	}

	public void setRefreshCount(long refreshCount) {
		this.refreshCount = refreshCount;
	}
	
public MobileIdAuth getMobileIdAuth() {
  return mobileIdAuth;
}

public void setMobileIdAuth(MobileIdAuth mobileIdAuth) {
  this.mobileIdAuth = mobileIdAuth;
}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

}
