package nl.clockwork.ebms.admin.web;

import org.apache.wicket.model.Model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class StringModel extends Model<String>
{
	private static final long serialVersionUID = 1L;
	Supplier<String> getObject; 

	@Override
	public String getObject()
	{
		return getObject.get();
	}
}
