showDialog = (header, body) ->
  id = "dialog"
  buildStructure = () ->
    {
      header: header
      body: body
      id: id
    }

  dust.stream 'dialog', buildStructure()
    .on "data", (html) ->
      $("#dialog-container").append(html)
    .on "end", () ->
      dialog = AJS.dialog2("##{id}")
      dialog.show()
      $("#dialog-close-button").click () -> 
        dialog.hide()