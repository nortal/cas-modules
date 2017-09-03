package com.nortal.cas.support.extraui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExtraUiConfiguration {
	@Bean
	ExtraUiRegistry extraUiRegistry() {
		return new ExtraUiRegistry();
	}
}
