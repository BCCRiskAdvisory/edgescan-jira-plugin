package com.bccriskadvisory.link.rest.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.bccriskadvisory.jira.user.UnifiedUserManager;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.rest.PluginResponse;
import com.bccriskadvisory.link.rest.form.components.FormStructure;

public abstract class AbstractRestResource {

	private UnifiedUserManager userManager;

	public AbstractRestResource(JiraPluginContext pluginContext) {
		userManager = pluginContext.getUserManager();
	}
	
	protected boolean notAuthed(final HttpServletRequest request) {
		Optional<ApplicationUser> user = userManager.getUser(request);
		
		return !user.isPresent() || !userManager.getSalUserManager().isSystemAdmin(user.get().getName());
	}
	
	protected Response noAuthResponse(final HttpServletRequest request) {
		Optional<ApplicationUser> user = userManager.getUser(request);
		
		if (user.isPresent()) {
			return respondNotAuthorized();
		} else {
			return respondNotAuthenticated();
		}
	}
	
	private Response respondNotAuthenticated() {
		return Response.serverError().status(Status.UNAUTHORIZED).build();
	}

	protected Response respondNotAuthorized() {
		return Response.serverError().status(Status.FORBIDDEN).build();
	}

	protected Response respondNotFound() {
		return Response.serverError().status(Status.NOT_FOUND).build();
	}

	protected Response respondError() {
		return Response.serverError().build();
	}

	protected Response respondOk() {
		return Response.ok().build();
	}

	protected Response respondOk(final PluginResponse response) {
		return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/form")
	public Response form(@Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		return respondOk(new PluginResponse().withFormStructure(getFormStructure(request)));
	}
	
	protected abstract FormStructure getFormStructure(final HttpServletRequest request);
}
