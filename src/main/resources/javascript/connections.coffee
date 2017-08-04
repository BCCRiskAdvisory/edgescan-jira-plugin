
do (baseUrl = "/rest/eslink/1.0/connections", paramNames = ['ID', 'name', 'description', 'url', 'apiKey', 'pollingInterval', 'isEnabled']) ->
  AJS.toInit () ->
    if $("#connection-config-container").length
      populateForm()
      initTable()

  populateForm = () ->
    get "#{baseUrl}/form?action=create", {}, (response) ->
      container = $("#form").empty()
      dust.render 'form/form', response.form, (err, html) ->
          container.append html
          container.find("#connection-form").submit createListener

  initTable = () ->
    get baseUrl, {}, (response) ->
      dust.render 'connection/connection-table', response, (err, html) ->
          $("#connection-table").html(html)
          $(".edit").click editListener
          $(".delete").click deleteListener
          $(".test", response.ID).click testListener

  createListener = (e) ->
    e.preventDefault()
    
    post baseUrl, buildParams(paramNames), responseHandler (response) ->
      connection = response.connection
      dust.render 'connection/connection-row', connection, (err, html) ->
          $("#connection-table tbody").append(html)

          elementById(".edit", connection.ID).click editListener
          elementById(".delete", connection.ID).click deleteListener
          elementById(".test", connection.ID).click testListener

          populateForm()

  updateListener = (e) ->
    e.preventDefault()
    id = $(this).find("#ID").attr("value")

    put idUrl(id), buildParams(paramNames), responseHandler (response) ->
      dust.render 'connection/connection-row', response.connection, (err, html) ->
          elementById("tr", id).replaceWith(html)
          elementById(".edit", id).click editListener
          elementById(".delete", id).click deleteListener
          elementById(".test", id).click testListener
          populateForm()

  editListener = () ->
    id = AJS.$(this).attr("data_id")

    get "#{idUrl(id)}?action=update", {}, (response) ->
      formData = mergeFormData(response.form, response.connection)
      dust.render 'form/form', formData, (err, html) ->
          replaceFormElement(html)
          AJS.$("#connection-form").submit updateListener
          AJS.$("#connection-form #cancel").click (e) ->
            e.preventDefault()
            populateForm()

  deleteListener = () ->
    id = AJS.$(this).attr("data_id")
    destroy idUrl(id), (response) ->
      elementById("tr", id).remove()

  testListener = () ->
    id = $(this).attr("data_id")
    get "#{idUrl(id)}/test", {}, (response) -> 
      if response && response.errorMessages.length
        error = response.errorMessages[0]
        showDialog error.type, error.message
      else
        showDialog "Test successful", "Successfully connected to Edgescan"

  replaceFormElement = (html) ->
    AJS.$("#form").empty()
    AJS.$("#form").html(html)

  idUrl = (id) ->
    baseUrl + "/#{id}"

  elementById = (element, id) ->
    AJS.$("#{element}[data_id=\"#{id}\"]")

  mergeFormData = (formStructure, connection = {}) ->
    input.value = connection[input.name] for input in formStructure.inputs
    formStructure