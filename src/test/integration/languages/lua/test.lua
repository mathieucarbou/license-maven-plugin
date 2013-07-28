do
  require("luascr/rluawfx")

  local function InsTextInCol ()
  local lung
  local lineaTxt
  local lineaCorrente
  local primaLinea
  local ultimaLinea
  local posizioneIniziale
  local pos
  local lungLinea = 0
  local lungAdd = 0
  local parte1 = ""
  local parte2 = ""
  local testoAdd = "[Testo da aggiungere]"
  local msg = rfx_GetStr("\r\n 1 -> Inizio linea \r\n 0 -> Fine linea")

  testoAdd = rwfx_InputBox("", rfx_GetStr("Testo da Inserire..."),
                            rfx_GetStr("Specifica il testo da inserire : "),
                            rfx_FN())
  if (testoAdd) then
    testoAdd = rfx_GF()
    lung = rwfx_InputBox("1", rfx_GetStr("Inserimento Testo in Colonna..."),
    rfx_GetStr("Specifica la colonna nella quale inserire il testo : ")..msg,
    rfx_FN())
    if lung then
      lung = rfx_GF()
      lung = tonumber(lung)
      if (lung > -1) then
        posizioneIniziale = editor.CurrentPos
        primaLinea = editor:LineFromPosition(editor.SelectionStart)
        ultimaLinea = editor:LineFromPosition(editor.SelectionEnd)
        --gestione singola linea
        lineaCorrente = primaLinea
        while (lineaCorrente <= ultimaLinea) do
          lineaTxt = editor:GetLine(lineaCorrente)
          lineaTxt = rfx_RemoveReturnLine(lineaTxt)
          lungLinea = string.len(lineaTxt)
          lungAdd = 0
          if (lung > 0) then
            if (lung > lungLinea) then
              lungAdd = lung - lungLinea
              lineaTxt = lineaTxt..string.rep(" ", lungAdd-1)
              lineaTxt = lineaTxt..testoAdd
            else
              parte1 = string.sub(lineaTxt,1, lung -1)
              parte2 = string.sub(lineaTxt,lung)
              lineaTxt = parte1..testoAdd..parte2
            end
          else --fine linea
            lineaTxt = lineaTxt..testoAdd
          end
          editor:GotoLine(lineaCorrente)
          editor:Home()
          editor:LineEndExtend()
          editor:ReplaceSel(lineaTxt)
          lineaCorrente = lineaCorrente + 1
        end --endwhile
        editor.CurrentPos = posizioneIniziale + lung
        editor.SelectionStart = editor.CurrentPos
        editor.SelectionEnd = editor.CurrentPos
      end --endif
    end --endif
  end --endif
  end --endfunction

  InsTextInCol()

end --endmodulo

