do (baseUrl = "/rest/eslink/1.0/links") ->
  AJS.toInit ->
    initPage() if root().length

  initPage = ->
    get projectUrl(), {}, renderLink, editLink

  renderLink = (response) ->
    dust.render 'project-link/page', response, (err, html) ->
      root().html html
      $("#edit-link").click editLink
      $("#enable-disable").click () ->
        put "#{projectUrl()}/toggle", {}, renderLink
      $("#import-updated").click () ->
        manualImport "updated"
      $("#import-full").click () ->
        manualImport "full"

  editLink = () ->
    get "#{projectUrl()}/edit", {}, renderForm 

  renderForm = (response) ->
      mergedForm = mergeFormData response.form, response.link
      dust.render 'form/form', mergedForm, (err, html) ->
        root().html html
        root().find("#connectionId").change updateForm
        root().find("#issueTypeId").change updateForm
        if "#{response.link.ID}" == "0"
          root().find("#project-link-form").submit createListener
        else 
          root().find("#project-link-form").submit updateListener
          root().find("#cancel").click (e) ->
            e.preventDefault()
            initPage()

  createListener = (e) ->
    e.preventDefault()
    post baseUrl, linkParams(), responseHandler(renderLink)

  updateListener = (e) ->
    e.preventDefault()
    put projectUrl(), linkParams(), responseHandler(renderLink)

  updateForm = () ->
    link = linkParams()
    put "#{baseUrl}/form", link, (response) ->
      response.link = link
      renderForm response

  manualImport = (modeString) ->
    testMode = $("#test-mode").is ":checked"
    $("#import-spinner").spin()
    $("#import-results").hide()
    put "#{projectUrl()}/import?mode=#{modeString}&testMode=#{testMode}", {}, (response) ->
      dust.render 'project-link/import-results', response.importResults, (err, html) ->
        $("#import-results").html html
        $("#import-spinner").spinStop()
        $("#import-results").show()
  
  mergeFormData = (form, data) ->
    return unless data
    for input in form.inputs
      input.value = data[input.name]

    for section in form.sections
      mergeFormData section, data

    addDustContext form

  projectKey = -> $("[name=projectKey]").attr("content")

  projectUrl = -> "#{baseUrl}/#{projectKey()}"

  linkParams = () -> buildParams(paramNames)

  root = -> $("#project-link-container")

  paramNames = 
    ["ID",
    "projectKey",
    "connectionId",
    "isEnabled",
    "assets",
    "minimalPriorityId",
    "lowPriorityId",
    "mediumPriorityId",
    "highPriorityId",
    "criticalPriorityId",
    "userKey",
    "issueTypeId",
    "openStatusId",
    "closeStatusId"]
