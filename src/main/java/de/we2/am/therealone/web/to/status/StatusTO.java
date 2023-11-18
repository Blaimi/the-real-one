package de.we2.am.therealone.web.to.status;

import java.util.List;

public record StatusTO(List<String> authors, String api_version) {
}
