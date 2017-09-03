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
