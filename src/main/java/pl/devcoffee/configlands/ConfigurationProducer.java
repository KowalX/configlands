/*
 * Copyright 2016 Piotr Raszkowski <piotr@raszkowski.pl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.devcoffee.configlands;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ConfigurationProducer {

	private static final String NO_CONFIGURATION_VALUE_ANNOTATION_MESSAGE = "Annotation @ConfigurationValue is not present!";

	private static final String JAVA_IO_TMPDIR_PROPERTY = "java.io.tmpdir";

	@Inject
	private ConfigurationProvidable configurationProvidable;

	@Produces
	@Configured
	public String getConfigurationValueAsString(InjectionPoint injectionPoint) {
		return getConfigurationValue(injectionPoint);
	}

	private String getConfigurationValue(InjectionPoint injectionPoint) {
		if (!injectionPoint.getAnnotated().isAnnotationPresent(ConfigurationValue.class)) {
			throw new ConfigurationException(NO_CONFIGURATION_VALUE_ANNOTATION_MESSAGE);
		}

		ConfigurableKey key = getConfigurationKey(injectionPoint);

		return configurationProvidable.getValue(key);
	}

	private ConfigurableKey getConfigurationKey(InjectionPoint injectionPoint) {
		ConfigurationValue configurationValueAnnotation = injectionPoint.getAnnotated().getAnnotation(ConfigurationValue.class);
		return configurationValueAnnotation.value();
	}

	@Produces
	@Configured
	public Integer getConfigurationValueAsInteger(InjectionPoint injectionPoint) {
		return getConfigurationValueAs(injectionPoint, this::asInteger);
	}

	private <T> T getConfigurationValueAs(InjectionPoint injectionPoint, Function<String, T> asFunction) {
		String value = getConfigurationValue(injectionPoint);

		if (value == null) {
			return null;
		}

		return asFunction.apply(value);
	}

	private Integer asInteger(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new ConfigurationException(String.format("Cannot parse given value as Integer: %s.", value), e);
		}
	}

	@Produces
	@Configured
	public Long getConfigurationValueAsLong(InjectionPoint injectionPoint) {
		return getConfigurationValueAs(injectionPoint, this::asLong);
	}

	private Long asLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new ConfigurationException(String.format("Cannot parse given value as Long: %s.", value), e);
		}
	}

	@Produces
	@Configured
	public Path getConfigurationValueAsPath(InjectionPoint injectionPoint) {
		return getConfigurationValueAs(injectionPoint, this::asPath);
	}

	private Path asPath(String value) {
		value = replaceJavaIOTmpDir(value);

		try {
			return Paths.get(value);
		} catch (InvalidPathException e) {
			throw new ConfigurationException(String.format("Cannot parse given value as Path: %s.", value), e);
		}
	}

	private String replaceJavaIOTmpDir(String value) {
		try {
			String javaTmpDirectory = System.getProperty(JAVA_IO_TMPDIR_PROPERTY);
			return value.replace(JAVA_IO_TMPDIR_PROPERTY, javaTmpDirectory);
		} catch (Exception e) {
			return value;
		}
	}

	@Produces
	@Configured
	public List<Path> getConfigurationValueAsPaths(InjectionPoint injectionPoint) {
		return getConfigurationValueAs(injectionPoint, this::asPaths);
	}

	private List<Path> asPaths(String value) {
		if (value.isEmpty()) {
			return Collections.emptyList();
		}

		String [] paths = value.split(";");

		if (paths.length == 0) {
			return null;
		} else if (paths.length == 1) {
			return Collections.singletonList(asPath(paths[0]));
		} else {
			return Arrays.stream(paths).map(this::asPath).collect(Collectors.toList());
		}
	}

	@Produces
	@Configured
	public Boolean getConfigurationValueAsBoolean(InjectionPoint injectionPoint) {
		return getConfigurationValueAs(injectionPoint, this::asBoolean);
	}

	private Boolean asBoolean(String value) {
		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			throw new ConfigurationException(String.format("Cannot parse given value as Boolean: %s.", value), e);
		}
	}
}
