package com.bccriskadvisory.link.rest.resource;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;

import com.atlassian.jira.user.ApplicationUser;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLinkServiceImpl;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLinkValidator;
import com.bccriskadvisory.jira.ao.validation.ValidationResult;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.connector.EdgescanV1Connector;
import com.bccriskadvisory.link.connector.EdgescanV1Connector.RequestBuilder;
import com.bccriskadvisory.link.processor.ProjectLinkImportProcessor;
import com.bccriskadvisory.link.processor.ImportResults;
import com.bccriskadvisory.link.processor.ConfluenceWikiMarkupSerializer;
import com.bccriskadvisory.link.rest.PluginResponse;
import com.bccriskadvisory.link.rest.edgescan.EdgescanResponse;
import com.bccriskadvisory.link.rest.edgescan.Vulnerability;
import com.bccriskadvisory.link.rest.edgescan.VulnerabilityDetails;
import com.bccriskadvisory.link.rest.form.ProjectLinkForm;
import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.rest.gson.GsonObject;
import com.bccriskadvisory.link.rest.projectlink.ProjectLinkDetails;
import com.bccriskadvisory.link.rest.projectlink.ProjectLinkTranslator;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;

@Path("links")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectLinkRestResource extends AbstractRestResource {
	
	private final JiraPluginContext pluginContext;
	private final ProjectLinkServiceImpl projectLinkService;

	public ProjectLinkRestResource(final JiraPluginContext context) {
		super(checkNotNull(context, "Plugin Context"));
		this.pluginContext = context;
		projectLinkService = context.getProjectLinkService();
	}
	
	@GET
	public Response index(@Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final List<ProjectLink> allLinks = projectLinkService.index();
		
		return respondOk(new PluginResponse().withLinks(allLinks));
	}
	
	@GET
	@Path("{id}")
	public Response show(@PathParam("id") final String key, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Optional<ProjectLink> link = getLinkByProjectKey(key);
		
		if (link.isPresent()) {
			return linkWithDetails(request, link.get());
		} else {
			return respondNotFound();
		}
	}
	
	@GET
	@Path("{id}/edit")
	public Response edit(@PathParam("id") final String key, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final ProjectLink link = getLinkByProjectKey(key).orElse(new ProjectLink(key));
		final FormStructure form = new ProjectLinkForm(pluginContext).withLink(link).build();
		
		return respondOk(new PluginResponse().withLink(link).withFormStructure(form));
	}
	
	@POST
	public Response create(@Context final HttpServletRequest request, final String body) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Optional<ProjectLink> newLink = getLinkFromBody(body);
		
		if (newLink.isPresent()) {
			ValidationResult validation = new ProjectLinkValidator(pluginContext).validate(newLink.get());
			
			if (validation.isValid()) {
				final ProjectLink created = projectLinkService.create(newLink.get());
			
				return linkWithDetails(request, created);
			} else {
				return respondOk(new PluginResponse().withErrorMessages(validation.getMessages()));
			}
		} else {
			return respondError();
		}
	}
	
	@PUT
	@Path("{id}")
	public Response update(@PathParam("id") final String id, @Context final HttpServletRequest request, final String body) {
		if (notAuthed(request)) return noAuthResponse(request);
		
		final Optional<ProjectLink> updatedLink = getLinkFromBody(body);
		
		if (updatedLink.isPresent()) {
			ValidationResult validation = new ProjectLinkValidator(pluginContext).validate(updatedLink.get());
			
			if (validation.isValid()) {
				final ProjectLink updated = projectLinkService.update(updatedLink.get());
				
				return linkWithDetails(request, updated);
			} else {
				return respondOk(new PluginResponse().withErrorMessages(validation.getMessages()));
			}
		} else {
			return respondError();
		}
	}
	
	@PUT
	@Path("{id}/toggle")
	public Response toggleEnabled(@PathParam("id") final String id, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);

		Optional<ProjectLink> linkByKey = getLinkByProjectKey(id);
		
		if (linkByKey.isPresent()) {
			ProjectLink toUpdate = linkByKey.get();
			toUpdate.toggleEnabled();
			
			ProjectLink updated = projectLinkService.update(toUpdate);
			
			return linkWithDetails(request, updated);
		} else {
			return respondNotFound();
		}
	}
	
	@PUT
	@Path("{id}/import")
	public Response manualImport(@PathParam("id") final String id, @Context final HttpServletRequest request) {
		if (notAuthed(request)) return noAuthResponse(request);
		String testMode = request.getParameter("testMode");
		String importMode = request.getParameter("mode");
		
		final Optional<ProjectLink> projectLink = getLinkByProjectKey(id);
		
		if (projectLink.isPresent()) {
			if (ProjectLinkImportProcessor.UPDATE_MODE.equals(importMode) || ProjectLinkImportProcessor.ALL_MODE.equals(importMode)) {
				final ProjectLinkImportProcessor processor = new ProjectLinkImportProcessor(pluginContext, importMode, Boolean.parseBoolean(testMode));
				processor.initWithLink(projectLink.get());
				final ImportResults importResults = processor.processImport();
				
				return respondOk(new PluginResponse().withImportResults(importResults));
			} else {
				return respondError();
			}
		} else {
			return respondNotFound();
		}
	}
	
	@PUT
	@Path("form")
	public Response buildForm(@Context final HttpServletRequest request, final String body) {
		if (notAuthed(request)) return noAuthResponse(request);

		final Optional<ProjectLink> linkFromBody = getLinkFromBody(body);
		
		if (linkFromBody.isPresent()) {
			final FormStructure form = new ProjectLinkForm(pluginContext).withLink(linkFromBody.get()).build();
			
			return respondOk(new PluginResponse().withFormStructure(form));
		} else {
			return respondError();
		}
	}
	
	@GET
	@Path("parse/{id}")
	public Response testParse(@PathParam("id") final String id) {
		return details(id);
	}

	private Response details(String id) {
		PegDownProcessor pegDownProcessor = new PegDownProcessor();
		
		Connection connection = pluginContext.getConnectionService().index().get(0);
		EdgescanV1Connector connector = new EdgescanV1Connector(pluginContext.getRequestFactory(), connection);

		EdgescanResponse vres = connector.vulnerabilities().withId(Integer.valueOf(id)).execute();
		Vulnerability detailedV = vres.getDetailedVulnerability().get();
		List<ParseResult> response = Lists.newArrayList(); 
			
		for (VulnerabilityDetails details : detailedV.getDetails().get()) {
			ParseResult parseResult = new ParseResult();
			parseResult.setMarkdown(details.getSrc());
			parseResult.setHtml(details.getHtml());
			RootNode markdown = pegDownProcessor.parseMarkdown(details.getSrc().toCharArray());
			String wikiMarkup = new ConfluenceWikiMarkupSerializer().toWikiMarkup(markdown);
			parseResult.setMarkup(wikiMarkup);
			response.add(parseResult);
		}
		
		return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
	}
	
	private Optional<ProjectLink> getLinkFromBody(final String body) {
		if (body == null) return Optional.empty();
		
		try {
			return Optional.ofNullable(GsonObject.fromJson(body, ProjectLink.class));
		} catch(final JsonSyntaxException e) {
			return Optional.empty();
		}
	}
	
	private Optional<ProjectLink> getLinkByProjectKey(final String key) {
		final List<ProjectLink> links = projectLinkService.findBy("PROJECT_KEY = ?", key);
		
		return links.isEmpty() ? Optional.empty() : Optional.of(links.get(0));
	}
	
	private Response linkWithDetails(final HttpServletRequest request, final ProjectLink link) {
		final Optional<ApplicationUser> user = pluginContext.getUserManager().getUser(request);
		final ProjectLinkDetails detail = new ProjectLinkTranslator(pluginContext).detail(link, user.get());
		
		return respondOk(new PluginResponse().withLink(link).withLinkDetails(detail));
	}
	
	protected FormStructure getFormStructure(final HttpServletRequest request) {
		return new FormStructure();
	}
}