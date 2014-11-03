
package org.grameenfoundation.cch.supervisor.listener;

import org.grameenfoundation.cch.supervisor.model.Payload;

public interface SubmitListener {
	void submitComplete(Payload response);
}
