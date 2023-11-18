package de.we2.am.therealone.web.resource;

import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import de.we2.am.therealone.web.to.error.ErrorTO;
import de.we2.am.therealone.web.to.error.ErrorsTO;
import de.we2.am.therealone.web.to.health.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;

import java.util.List;
import java.util.UUID;

@Path("health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    private final Logger logger = LogManager.getLogger(HealthResource.class);
    @Inject
    private HealthEndpoint healthEndpoint;
    @Inject
    private HealthContributorRegistry healthRegistry;

    @GET
    public Response getHealth() {
        HealthComponent health = healthEndpoint.health();
        DataSourceHealthIndicator dataSourceHealthIndicator = (DataSourceHealthIndicator) healthRegistry.getContributor("db");
        boolean ready = health.getStatus() == Status.UP;
        boolean databaseUp = dataSourceHealthIndicator.health().getStatus() == Status.UP;

        return Response.ok(new HealthTO(true /* see #getLive() */, ready, new DatabasesTO(new DatabaseTO(databaseUp)))).build();
    }

    @GET
    @Path("live")
    public Response getLive() {
        // We are always live otherwise we would not get here to begin with
        return Response.ok(new LiveTO(true)).build();
    }

    @GET
    @Path("ready")
    public Response getReady() {
        HealthComponent health = healthEndpoint.health();

        // Are you ready kids?
        if (health.getStatus() == Status.UP) {
            // aye aye captain
            return Response.ok(new ReadyTO(true)).build();
        }

        // They were not lout enough and the captain couldn't hear them
        UUID trace = UUID.randomUUID();
        StringMapMessage log = new StringMapMessage()
                .with("message", "Health ready checked failed")
                .with("error.trace", trace);

        for (NamedContributor<HealthContributor> contributor : healthRegistry) {
            if (!(contributor.getContributor() instanceof HealthIndicator indicator)) {
                continue;
            }

            Health contributorHealth = indicator.health();
            if (contributorHealth.getStatus() == Status.UP) {
                continue;
            }

            log.with(String.format("error.health.%s.status", contributor.getName()), contributorHealth.getStatus());
            log.with(String.format("error.health.%s.error", contributor.getName()), contributorHealth.getDetails().getOrDefault("error", "Unknown"));
        }

        logger.error(log);

        ErrorsTO errors = new ErrorsTO(List.of(new ErrorTO(ErrorCodeTO.NO_NEED_TO_KNOW, "Server error", "Service is not ready")), trace);
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errors).build();
    }
}
