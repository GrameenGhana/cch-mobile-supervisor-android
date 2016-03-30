package org.grameenfoundation.cch.supervisor.task;

import org.grameenfoundation.cch.supervisor.model.Payload;

public interface SubmitListener {
    void submitComplete(Payload response);
}
