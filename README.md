# configlands
Minimalistic configuration provider for Java EE applications.

## Configuration

### Create (overwrite) your enum with configuration values

```java
package pl.devcoffee.configlands;

public enum ConfigurationKey implements ConfigurableKey {
	YOUR_CONFIGURATION_PARAMETER("default value"),
	;

	private String defaultValue;

	private ConfigurationKey(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
}
```

**Remember to place the enum in pl.devcofee.configlands package.**

### Implement the ConfigurationProvidable interface

```java
public class Configurator implements ConfigurationProvidable {

	@Override
	public String getValue(ConfigurableKey configurableKey) {
		return configurableKey.getDefaultValue();
	}
}
```

## Usage

### Inject configuration value

#### 1. String

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.DEFAULT_TIME_ZONE)
private Instance<String> defaultTimeZone;
```

#### 2. Long

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.RESTART_IN_SECONDS)
private Long restartInSeconds;
```

#### 3. Integer

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.DEFAULT_TIME_ZONE)
private Integer defaultTimeZone;
```

#### 4. Path

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.UPLOAD_LOCATION)
private Instance<Path> uploadLocation;
```

#### 5. Path with java.tmp.dir

##### Precondition
ConfigurationKey.UPLOAD_LOCATION = "java.io.tmpdir/uploaded" 

##### Example code
```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.UPLOAD_LOCATION)
private Path uploadLocation;
```

##### Result

uploadedLocation injected as System.getProperty(java.io.tmpdir)/uploaded

#### 6. Paths

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.UPLOAD_LOCATIONS)
private List<Path> uploadLocations ;
```

### Use ConfigurationProvideable

```java
@Inject
private ConfigurationProvideable configurationProvideable;

public void getConfiguration() {
	configurationProvideable.getValue(ConfigurationKey.PARAMETER);
}
```