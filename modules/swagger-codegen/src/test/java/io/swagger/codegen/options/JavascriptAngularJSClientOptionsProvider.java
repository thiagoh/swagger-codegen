package io.swagger.codegen.options;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.swagger.codegen.CodegenConstants;

public class JavascriptAngularJSClientOptionsProvider implements OptionsProvider {
	public static final String SORT_PARAMS_VALUE = "false";
	public static final String ENSURE_UNIQUE_PARAMS_VALUE = "true";

	@Override
	public String getLanguage() {
		return "javascript-angular-js";
	}

	@Override
	public Map<String, String> createOptions() {
		final ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
		return builder.put(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG, SORT_PARAMS_VALUE).put(CodegenConstants.ENSURE_UNIQUE_PARAMS, ENSURE_UNIQUE_PARAMS_VALUE)
				.put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, "true").build();
	}

	@Override
	public boolean isServer() {
		return false;
	}
}
