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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.sal.api.net.RequestFactory;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.connection.ConnectionService;
import com.bccriskadvisory.jira.ao.connection.ConnectionValidator;
import com.bccriskadvisory.jira.ao.validation.ValidationResult;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.rest.PluginResponse;
import com.bccriskadvisory.link.rest.edgescan.EdgescanResponse;
import com.bccriskadvisory.link.rest.form.ConnectionForm;
import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.rest.gson.GsonObject;

@Path("/connections")
@Produces(MediaType.APPLICATION_JSON)
public class ConnectionRestResource extends AbstractRestResource {
	
	private final ConnectionService connectionService;
	private final RequestFactory<?> requestFactory;
	private ConnectionValidator connectionValidator;
	
	public ConnectionRestResource(final JiraPluginContext context) {
		super(checkNotNull(context, "plugin context"));
		this.connectionService = context.getConnectionService();
		this.requestFactory = context.getRequestFactory();
		this.connectionValidator = new ConnectionValidator(connectionService);
	}

	@GET
	public Response index(@Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final List<Connection> all = connectionService.index();
		
		return respondOk(new PluginResponse().withConnections(all));
	}
	
	@GET
	@Path("{id}")
	public Response get(@PathParam("id") final int id, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Connection connection = connectionService.find(id);
		
		if (connection == null) {
			return respondNotFound();
		} else {
			final FormStructure formStructure = getFormStructure(request);
			return respondOk(new PluginResponse().withConnection(connection).withFormStructure(formStructure));
		}
	}
	
	@GET
	@Path("{id}/test")
	public Response test(@PathParam("id") final int id, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Connection found = connectionService.find(id);
		
		if (found == null) {
			return respondNotFound();
		} else {
			final EdgescanV1Connector connector = new EdgescanV1Connector(requestFactory, found);
			
			final EdgescanResponse response = connector.test();
			if (response.getAssets().isPresent()) {
				return respondOk();
			} else {
				return respondError();
			}
		}
	}
	
	@POST
	public Response create(final String body, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		Connection newConnection = GsonObject.fromJson(body, Connection.class);
		final ValidationResult validation = connectionValidator.validate(newConnection);

		try {
			if(validation.isValid()) {
				connectionValidator.normalize(newConnection);
				newConnection = connectionService.create(newConnection);

				return respondOk(new PluginResponse().withConnection(newConnection));
			} else {
				return respondOk(new PluginResponse().withErrorMessages(validation.getMessages()));
			}
		} catch (final Exception e) {
			return respondError();
		}
	}
	
	@PUT
	@Path("{id}")
	public Response update(@PathParam("id") final int id, @Context final HttpServletRequest request, final String body) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Connection connection = GsonObject.fromJson(body, Connection.class);
		
		final ValidationResult validation = connectionValidator.validate(connection);
		if (validation.isValid()) {
			final Connection updated = connectionService.update(connection);
			
			return respondOk(new PluginResponse().withConnection(updated));
		} else {
			return respondOk(new PluginResponse().withErrorMessages(validation.getMessages()));
		}
	}
	
	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") final int id, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Connection connection = connectionService.find(id);
		
		if (connection == null) {
			return respondNotFound();
		} else {
			if (connectionService.delete(connection)) {
				return respondOk();
			} else {
				return respondError();
			}
		}
	}
	
	protected FormStructure getFormStructure(final HttpServletRequest request) {
		String parameter = request.getParameter("action");
		
		FormStructure structure;
		switch (parameter) {
			case "create":
				structure = ConnectionForm.newForm().withSubmit("Create");
				break;
			case "update":
				structure = ConnectionForm.newForm().withSubmit("Update").withCancel("cancel");
				break;
			default:
				return null;
		}
		
		return structure;
	}
}