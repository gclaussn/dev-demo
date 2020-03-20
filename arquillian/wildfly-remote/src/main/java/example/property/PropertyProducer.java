package example.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class PropertyProducer {

  private Properties properties;

  @PostConstruct
  public void init() {
    properties = new Properties();

    InputStream is = PropertyProducer.class.getResourceAsStream("/application.properties");
    if (is == null) {
      return;
    }

    try {
      properties.load(is);
    } catch (IOException e) {
      throw new RuntimeException("Application properties could not be loaded", e);
    }
  }

  @Produces
  @Property
  public String getProperty(InjectionPoint ip) {
    String propertyName = getPropertyName(ip);
    return properties.getProperty(propertyName);
  }

  @Produces
  @Property
  public Boolean getPropertyAsBoolean(InjectionPoint ip) {
    String value = getProperty(ip);
    return value == null ? null : Boolean.valueOf(value);
  }

  @Produces
  @Property
  public Integer getPropertyAsInt(InjectionPoint ip) {
    String value = getProperty(ip);
    return value == null ? null : Integer.valueOf(value);
  }

  protected String getPropertyName(InjectionPoint ip) {
    Property property = ip.getAnnotated().getAnnotation(Property.class);
    return property.value();
  }
}
