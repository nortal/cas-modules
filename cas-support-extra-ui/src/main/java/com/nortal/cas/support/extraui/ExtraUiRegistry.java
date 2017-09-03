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
package com.nortal.cas.support.extraui;

import java.util.ArrayList;
import java.util.List;

public class ExtraUiRegistry {
	private List<String> scripts = new ArrayList<>();

	private List<String> fragments = new ArrayList<>();

	public List<String> getFragments() {
		return fragments;
	}

	public void setFragments(List<String> fragments) {
		this.fragments = fragments;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

	public void add(String ui) {
		scripts.add(ui);
		fragments.add(ui);
	}
}
