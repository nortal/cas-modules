package com.nortal.cas.support.mobileid;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mobileId")
public class MobileIdProperties {

	private String serviceName;
	
	private String serviceUrl;
	
	private boolean isTestNumbersEnabled;
	
	private int mIdCheckDelay = 5;
	
	private int mIdCheckCount = 20;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public boolean isTestNumbersEnabled() {
		return isTestNumbersEnabled;
	}

	public void setTestNumbersEnabled(boolean isTestNumbersEnabled) {
		this.isTestNumbersEnabled = isTestNumbersEnabled;
	}

	public int getmIdCheckDelay() {
		return mIdCheckDelay;
	}

	public void setmIdCheckDelay(int mIdCheckDelay) {
		this.mIdCheckDelay = mIdCheckDelay;
	}

	public int getmIdCheckCount() {
		return mIdCheckCount;
	}

	public void setmIdCheckCount(int mIdCheckCount) {
		this.mIdCheckCount = mIdCheckCount;
	}
}
