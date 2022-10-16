local completion = require("cc.completion")

shell.setCompletionFunction("rom/programs/tape.lua", function(shell, nIndex, sText, prev)
  if nIndex == 1 then
    return completion.choice(sText, {"load", "save", "delete", "seek", "list", "wipe", "size"}, true)
  end
end)