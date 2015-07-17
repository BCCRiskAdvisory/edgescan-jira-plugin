showDialog = (title, body) ->
  id = "esj-dialog"
  dialogStructure = 
    {
      title: title
      body: body
      id: id
    }

  dust.render 'common/dialog', dialogStructure, (err, html) ->
    $("#dialog-container").append(html)
    dialog = AJS.dialog2("##{id}")
    dialog.show()
    $("#dialog-close-button").click () -> 
      dialog.hide()