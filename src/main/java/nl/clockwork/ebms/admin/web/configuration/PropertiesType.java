package nl.clockwork.ebms.admin.web.configuration;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum PropertiesType
{
	EBMS_ADMIN("ebms-admin.properties"), EBMS_ADMIN_EMBEDDED("ebms-admin.embedded.properties"), EBMS_CORE("ebms-core.properties");
	
	String propertiesFile;

	public static PropertiesType getPropertiesType(String propertiesFile)
	{
		return Arrays.stream(PropertiesType.values()).filter(p -> p.propertiesFile.equals(propertiesFile)).findFirst().orElse(null);
	}
}