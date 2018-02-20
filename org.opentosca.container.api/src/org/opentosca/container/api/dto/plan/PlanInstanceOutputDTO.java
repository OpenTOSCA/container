package org.opentosca.container.api.dto.plan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.PropertyDTO;
import org.opentosca.container.core.next.model.PlanInstanceOutput;

@XmlRootElement(name = "PlanInstanceOutput")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanInstanceOutputDTO extends PropertyDTO{
	public static class Converter{
		public static PlanInstanceOutputDTO convert(final PlanInstanceOutput object) {
			final PlanInstanceOutputDTO result = new PlanInstanceOutputDTO();
			PropertyDTO.Converter.fillValues(object, result);
			
			return result;
		}
	}
}
