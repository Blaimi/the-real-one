package de.we2.am.therealone.web.to.error;

import java.util.List;
import java.util.UUID;

public record ErrorsTO(List<ErrorTO> errors, UUID trace) {
}
