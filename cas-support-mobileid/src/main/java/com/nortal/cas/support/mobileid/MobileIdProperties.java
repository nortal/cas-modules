/**
 *   Copyright 2017 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.cas.support.mobileid;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("mobileId")
public class MobileIdProperties {

	private String serviceName;
	
	private String serviceUrl;
	
	private boolean isTestNumbersEnabled;
	
	private int mIdCheckDelay = 5;
	
	private int mIdCheckCount = 20;
	
	private boolean allowPhoneOrId = true;

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

	public boolean isAllowPhoneOrId() {
		return allowPhoneOrId;
	}

	public void setAllowPhoneOrId(boolean allowPhoneOrId) {
		this.allowPhoneOrId = allowPhoneOrId;
	}
}
