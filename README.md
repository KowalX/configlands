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

_Example_
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

#### String

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.DEFAULT_TIME_ZONE)
private Instance<String> defaultTimeZone;
```

#### Long

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.RESTART_IN_SECONDS)
private Long restartInSeconds;
```

#### Integer

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.DEFAULT_TIME_ZONE)
private Integer defaultTimeZone;
```

#### Path

```java
@Inject
@Configured
@ConfigurationValue(value = ConfigurationKey.UPLOAD_LOCATION)
private Instance<Path> uploadLocation;
```

#### Path with java.tmp.dir

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

#### Paths

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