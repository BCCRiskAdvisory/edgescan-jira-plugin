do (baseUrl = "/rest/eslink/1.0/links") ->
  root = -> $("#es-project-link-summary-container")

  projectUrl= -> "#{baseUrl}/#{projectkey()}"
  projectkey = -> $("#project-config-details-project-key").html()

  AJS.toInit ->
    if root().length
      get projectUrl(), {}, linkFound, linkNotFound

  linkFound = (response) ->
    dust.render 'project-link/summary', response, (err, html) ->
      root().html html
      root().find("#enable-disable").click ()->
        put "#{projectUrl()}/toggle", {}, linkFound

  linkNotFound = (response) ->
    dust.render 'project-link/summary', {}, (err, html) -> root().html html