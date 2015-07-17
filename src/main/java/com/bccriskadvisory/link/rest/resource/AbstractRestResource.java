/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.bccriskadvisory.link.rest.PluginError;
import com.bccriskadvisory.link.rest.PluginResponse;
import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.utility.AbstractLogSupported;

public abstract class AbstractRestResource extends AbstractLogSupported {

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
	
	protected Response respondError(String type, String message) {
		return respond(new PluginResponse().withError(new PluginError(type, message)));
	}
	
	protected Response respondException(Exception e) {
		return respond(new PluginResponse().withException(e));
	}

	protected Response respondOk() {
		return Response.ok().build();
	}

	protected Response respond(final PluginResponse response) {
		return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/form")
	public Response form(@Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		return respond(new PluginResponse().withFormStructure(getFormStructure(request)));
	}
	
	protected abstract FormStructure getFormStructure(final HttpServletRequest request);
}
