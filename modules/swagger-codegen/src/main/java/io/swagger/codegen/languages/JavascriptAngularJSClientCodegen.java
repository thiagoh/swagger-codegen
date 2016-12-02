package io.swagger.codegen.languages;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import io.swagger.codegen.CliOption;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.DefaultCodegen;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;

public class JavascriptAngularJSClientCodegen extends DefaultCodegen implements CodegenConfig {
	public JavascriptAngularJSClientCodegen() {
		super();

		supportsInheritance = false;
		setReservedWordsLowerCase(Arrays.asList("abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package", "synchronized", "do", "goto", "private", "this", "break",
				"double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short",
				"try", "char", "final", "interface", "static", "void", "class", "finally", "const", "super", "while"));

		languageSpecificPrimitives = new HashSet<String>(Arrays.asList("string", "boolean", "number", "Object", "Blob", "Date"));
		instantiationTypes.put("array", "Array");

		typeMapping = new HashMap<String, String>();
		typeMapping.put("Array", "Array");
		typeMapping.put("array", "Array");
		typeMapping.put("List", "Array");
		typeMapping.put("boolean", "boolean");
		typeMapping.put("string", "string");
		typeMapping.put("int", "number");
		typeMapping.put("float", "number");
		typeMapping.put("number", "number");
		typeMapping.put("long", "number");
		typeMapping.put("short", "number");
		typeMapping.put("char", "string");
		typeMapping.put("double", "number");
		typeMapping.put("object", "Object");
		typeMapping.put("Object", "Object");
		typeMapping.put("File", "Blob");
		typeMapping.put("file", "Blob");
		typeMapping.put("integer", "number");
		typeMapping.put("Map", "Object");
		typeMapping.put("map", "Object");
		typeMapping.put("DateTime", "Date");

		importMapping = new HashMap<String, String>();
		defaultIncludes = new HashSet<String>(Arrays.asList("Object", "Array", "Blob"));

		typeMapping.put("binary", "string");

		outputFolder = "generated-code/javascript-angular-js";
		// modelTemplateFiles.put("model.mustache", ".js");
		apiTemplateFiles.put("api.mustache", ".js");
		embeddedTemplateDir = templateDir = "Javascript-AngularJS";
		apiPackage = "";
		modelPackage = "";

		cliOptions.add(new CliOption(CodegenConstants.HIDE_GENERATION_TIMESTAMP, "hides the timestamp when files were generated").defaultValue(Boolean.TRUE.toString()));
	}

	@Override
	public void processOpts() {
		super.processOpts();

		// default HIDE_GENERATION_TIMESTAMP to true
		if (!additionalProperties.containsKey(CodegenConstants.HIDE_GENERATION_TIMESTAMP)) {
			additionalProperties.put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, Boolean.TRUE.toString());
		}
	}

	/**
	 * Output the API (class) name (capitalized) ending with "Api"
	 * Return DefaultApi if name is empty
	 *
	 * @param name
	 *            the name of the Api
	 * @return camelized Api name ending with ""
	 */
	@Override
	public String toApiName(String name) {
		if (name.length() == 0) {
			return "defaultApi";
		}
		return camelize(name + "Api", true);
	}

	@Override
	public String toApiFilename(String name) {
		if (name.length() == 0) {
			return "default-api";
		}
		return camelize(name + "-api", true);
	}

	@Override
	public String getName() {
		return "javascript-angular-js";
	}

	@Override
	public String getHelp() {
		return "Generates a Javascript AngularJS client library (beta)";
	}

	@Override
	public CodegenType getTag() {
		return CodegenType.CLIENT;
	}

	@Override
	public String escapeReservedWord(String name) {
		return "_" + name;
	}

	@Override
	public String apiFileFolder() {
		return outputFolder + "/" + apiPackage().replace('.', File.separatorChar);
	}

	@Override
	public String modelFileFolder() {
		return outputFolder + "/" + modelPackage().replace('.', File.separatorChar);
	}

	@Override
	public String toVarName(String name) {
		// sanitize name
		name = sanitizeName(name);

		// replace - with _ e.g. created-at => created_at
		name = name.replaceAll("-", "_");

		// if it's all uppper case, do nothing
		if (name.matches("^[A-Z_]*$")) {
			return name;
		}

		// camelize the variable name
		// pet_id => PetId
		name = camelize(name, true);

		// for reserved word or word starting with number, append _
		if (isReservedWord(name) || name.matches("^\\d.*")) {
			name = escapeReservedWord(name);
		}

		return name;
	}

	@Override
	public String toParamName(String name) {
		// should be the same as variable name
		return toVarName(name);
	}

	@Override
	public String toModelName(String name) {
		if (!StringUtils.isEmpty(modelNamePrefix)) {
			name = modelNamePrefix + "_" + name;
		}

		if (!StringUtils.isEmpty(modelNameSuffix)) {
			name = name + "_" + modelNameSuffix;
		}

		// model name cannot use reserved keyword, e.g. return
		if (isReservedWord(name)) {
			LOGGER.warn(name + " (reserved word) cannot be used as model name. Renamed to " + camelize("model_" + name));
			name = "model_" + name; // e.g. return => ModelReturn (after
									// camelize)
		}

		// camelize the model name
		// phone_number => PhoneNumber
		return camelize(name);
	}

	@Override
	public String toModelFilename(String name) {
		// should be the same as the model name
		return toModelName(name);
	}

	@Override
	public String getTypeDeclaration(Property p) {
		if (p instanceof ArrayProperty) {
			final ArrayProperty ap = (ArrayProperty) p;
			final Property inner = ap.getItems();
			return getSwaggerType(p) + "<!" + getTypeDeclaration(inner) + ">";
		} else if (p instanceof MapProperty) {
			final MapProperty mp = (MapProperty) p;
			final Property inner = mp.getAdditionalProperties();
			return "Object<!string, " + getTypeDeclaration(inner) + ">";
		} else if (p instanceof FileProperty) {
			return "Object";
		}
		final String type = super.getTypeDeclaration(p);
		if (type.equals("boolean") || type.equals("Date") || type.equals("number") || type.equals("string")) {
			return type;
		}
		return apiPackage + "." + type;
	}

	@Override
	public String getSwaggerType(Property p) {
		final String swaggerType = super.getSwaggerType(p);
		String type = null;
		if (typeMapping.containsKey(swaggerType)) {
			type = typeMapping.get(swaggerType);
			if (languageSpecificPrimitives.contains(type)) {
				return type;
			}
		} else {
			type = swaggerType;
		}
		return type;
	}

	@Override
	public Map<String, Object> postProcessModels(Map<String, Object> objs) {

		final List<Object> models = (List<Object>) objs.get("models");
		for (final Object _mo : models) {
			final Map<String, Object> mo = (Map<String, Object>) _mo;
			final CodegenModel cm = (CodegenModel) mo.get("model");
			cm.imports = new TreeSet(cm.imports);
			for (final CodegenProperty var : cm.vars) {
				// handle default value for enum, e.g. available =>
				// StatusEnum.available
				if (var.isEnum && var.defaultValue != null && !"null".equals(var.defaultValue)) {
					var.defaultValue = var.datatypeWithEnum + "." + var.defaultValue;
				}
			}
		}
		return objs;
	}

	@Override
	public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
		if (objs.get("imports") instanceof List) {
			final List<Map<String, String>> imports = (ArrayList<Map<String, String>>) objs.get("imports");
			Collections.sort(imports, new Comparator<Map<String, String>>() {
				@Override
				public int compare(Map<String, String> o1, Map<String, String> o2) {
					return o1.get("import").compareTo(o2.get("import"));
				}
			});
			objs.put("imports", imports);
		}
		return objs;
	}

	@Override
	public String toOperationId(String operationId) {
		// throw exception if method name is empty
		if (StringUtils.isEmpty(operationId)) {
			throw new RuntimeException("Empty method/operation name (operationId) not allowed");
		}

		operationId = camelize(sanitizeName(operationId), true);

		// method name cannot use reserved keyword, e.g. return
		if (isReservedWord(operationId)) {
			final String newOperationId = camelize("call_" + operationId, true);
			LOGGER.warn(operationId + " (reserved word) cannot be used as method name. Renamed to " + newOperationId);
			return newOperationId;
		}

		return operationId;
	}

	@Override
	public String escapeQuotationMark(String input) {
		// remove ', " to avoid code injection
		return input.replace("\"", "").replace("'", "");
	}

	@Override
	public String escapeUnsafeCharacters(String input) {
		return input.replace("*/", "*_/").replace("/*", "/_*");
	}

}
