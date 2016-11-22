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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProducerTest {

	private static final ConfigurationKey CONFIGURATION_KEY = ConfigurationKey.NONE;

	private static final String JAVA_IO_TMPDIR_PROPERTY = "java.io.tmpdir";
	private static final String STRING_CONFIGURATION_VALUE = "STRING_CONFIGURATION_VALUE";
	private static final String INTEGER_CONFIGURATION_VALUE = "123";
	private static final String LONG_CONFIGURATION_VALUE = "123456789123456789";
	private static final String BOOLEAN_CONFIGURATION_VALUE = "True";
	private static final String PATH_CONFIGURATION_VALUE = "/path/to/something";

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private InjectionPoint injectionPoint;

	@Mock
	private ConfigurationProvidable configurationProvidable;

	@Mock
	private ConfigurationValue configurationValue;

	@InjectMocks
	private ConfigurationProducer configurationProducer;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() {
		doReturn(CONFIGURATION_KEY).when(configurationValue).value();

		doReturn(STRING_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		when(injectionPoint.getAnnotated().isAnnotationPresent(ConfigurationValue.class)).thenReturn(true);
		when(injectionPoint.getAnnotated().getAnnotation(ConfigurationValue.class)).thenReturn(configurationValue);
	}

	@Test
	public void getConfigurationValueAsStringWhenNotAnnotatedWithConfigurationValue() {
		when(injectionPoint.getAnnotated().isAnnotationPresent(ConfigurationValue.class)).thenReturn(false);

		expectedException.expect(ConfigurationException.class);
		expectedException.expectMessage("Annotation @ConfigurationValue is not present!");

		configurationProducer.getConfigurationValueAsString(injectionPoint);
	}

	@Test
	public void getConfigurationValueAsStringWhenExistingValue() {
		doReturn(STRING_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		String value = configurationProducer.getConfigurationValueAsString(injectionPoint);

		assertEquals(STRING_CONFIGURATION_VALUE, value);
	}

	@Test
	public void getConfigurationValueAsIntegerWhenExistingValue() {
		doReturn(INTEGER_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Integer result = configurationProducer.getConfigurationValueAsInteger(injectionPoint);

		assertEquals(Integer.parseInt(INTEGER_CONFIGURATION_VALUE), result.intValue());

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsIntegerWhenNullValue() {
		doReturn(null).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Integer result = configurationProducer.getConfigurationValueAsInteger(injectionPoint);

		assertNull(result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsIntegerWhenValueNotMatchesInteger() {
		doReturn(STRING_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		expectedException.expect(ConfigurationException.class);
		expectedException.expectMessage("Cannot parse given value as Integer: " + STRING_CONFIGURATION_VALUE);

		configurationProducer.getConfigurationValueAsInteger(injectionPoint);
	}

	@Test
	public void getConfigurationValueAsLongWhenExistingValue() {
		doReturn(LONG_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Long result = configurationProducer.getConfigurationValueAsLong(injectionPoint);

		assertEquals(Long.parseLong(LONG_CONFIGURATION_VALUE), result.longValue());

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsLongWhenNullValue() {
		doReturn(null).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Long result = configurationProducer.getConfigurationValueAsLong(injectionPoint);

		assertNull(result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsLongWhenValueNotMatchesLong() {
		doReturn(STRING_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		expectedException.expect(ConfigurationException.class);
		expectedException.expectMessage("Cannot parse given value as Long: " + STRING_CONFIGURATION_VALUE);

		configurationProducer.getConfigurationValueAsLong(injectionPoint);
	}

	@Test
	public void getConfigurationValueAsPathWhenExistingValue() {
		doReturn(PATH_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Path result = configurationProducer.getConfigurationValueAsPath(injectionPoint);

		assertEquals(Paths.get(PATH_CONFIGURATION_VALUE), result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathWhenNullValue() {
		doReturn(null).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Path result = configurationProducer.getConfigurationValueAsPath(injectionPoint);

		assertNull(result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathWhenValueContainsJavaTmpDir() {
		doReturn(JAVA_IO_TMPDIR_PROPERTY+"/new").when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Path result = configurationProducer.getConfigurationValueAsPath(injectionPoint);

		assertNotNull(result);
		assertEquals(Paths.get(System.getProperty(JAVA_IO_TMPDIR_PROPERTY), "new"), result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathsWhenEmptyValue() {
		doReturn("").when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final List<Path> result = configurationProducer.getConfigurationValueAsPaths(injectionPoint);

		assertNotNull(result);
		assertEquals(0, result.size());

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathsWhenNullValue() {
		doReturn(null).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final List<Path> result = configurationProducer.getConfigurationValueAsPaths(injectionPoint);

		assertNull(result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathsWhenSinglePathValue() {
		doReturn("PATH1").when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final List<Path> result = configurationProducer.getConfigurationValueAsPaths(injectionPoint);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(Paths.get("PATH1"), result.get(0));

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsPathsWhenMultiplePathsValue() {
		doReturn("PATH1;PATH2;PATH3").when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final List<Path> result = configurationProducer.getConfigurationValueAsPaths(injectionPoint);

		assertNotNull(result);

		assertEquals(3, result.size());
		assertEquals(Paths.get("PATH1"), result.get(0));
		assertEquals(Paths.get("PATH2"), result.get(1));
		assertEquals(Paths.get("PATH3"), result.get(2));

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsBooleanWhenExistingValue() {
		doReturn(BOOLEAN_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Boolean result = configurationProducer.getConfigurationValueAsBoolean(injectionPoint);

		assertEquals(Boolean.parseBoolean(BOOLEAN_CONFIGURATION_VALUE), result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsBooleanWhenNullValue() {
		doReturn(null).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Boolean result = configurationProducer.getConfigurationValueAsBoolean(injectionPoint);

		assertNull(result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}

	@Test
	public void getConfigurationValueAsBooleanWhenValueNotMatchesBoolean() {
		doReturn(STRING_CONFIGURATION_VALUE).when(configurationProvidable).getValue(CONFIGURATION_KEY);

		final Boolean result = configurationProducer.getConfigurationValueAsBoolean(injectionPoint);

		assertEquals(false, result);

		verify(configurationProvidable, only()).getValue(CONFIGURATION_KEY);
	}
}