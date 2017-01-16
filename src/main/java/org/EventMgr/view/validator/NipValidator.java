package org.EventMgr.view.validator;

import javax.faces.validator.Validator;
import javax.faces.application.FacesMessage;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

@FacesValidator("org.EventMgr.view.validator.NipValidator")
public class NipValidator implements Validator {

	@Override
	public void validate(final FacesContext context,
			final UIComponent component, final java.lang.Object value)
			throws ValidatorException {
		throw new ValidatorException(new FacesMessage(
				"Validator not yet implemented."));
	}
}