nop = () ->

dust.debugLevel = "DEBUG"

$ = AJS.$

buildParams = (names) ->
  content = {}
  for name in names
    value = inputValue name 
    content[name] = value if value && value.length
  return content

inputValue = (key) ->
  input = $("##{key}")
  switch input.attr("type")
    when "text", "hidden" 
      input.attr("value")
    when "checkbox" 
      if input.is(":checked") then "true" else "false"
    else
      if input.is("select")
        if input.is("[multiple]")
          value = []
          input.find("option:selected").each () -> value.push $(this).attr("value")
          value
        else
          input.find("option:selected").attr("value")

ajax_call = (method) -> 
  (url, data, onComplete, onError = nop) ->
    AJS.$.ajax {
      url: url
      type: method
      contentType: "application/json"
      data: JSON.stringify data
      processData: false
      success: onComplete
      error: onError
    }

get = ajax_call "GET"
destroy = (url, onComplete, onError) -> ajax_call("DELETE")(url, {}, onComplete, onError)
post = ajax_call "POST"
put = ajax_call "PUT"

addDustContext = (data = {}) ->
  data.optionSelected = optionSelected
  return data

optionSelected = (chunk, context) ->
  selected = context.get "selected"
  value = context.get "value"
  if selected and selected.constructor == Array
    chunk.write "selected" if ("#{selected}".indexOf value) > -1
  else
    chunk.write "selected" if "#{selected}" == value

responseHandler = (handler) ->
  (response) ->
    if response and response.errorMessages.length
      renderErrors response.errorMessages
    else
      handler response

renderErrors = (errors) ->
  AJS.messages.error("#alerts", {title: error.type, body: error.message}) for error in errors