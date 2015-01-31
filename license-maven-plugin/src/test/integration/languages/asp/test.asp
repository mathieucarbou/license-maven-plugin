<%@ LANGUAGE="VBScript" %>
<%
' ----------------------------------------------------------------------------------
'  Liberum Help Desk, Copyright (C) 2000-2004 Doug Luxem
'  Liberum Help Desk comes with ABSOLUTELY NO WARRANTY
'  Please view the license.html file for the full GNU General Public License.
'
'  Filename: caseSearchResults.asp
'  Date:     $Date: 2004/03/25 23:27:44 $
'  Version:  $Revision: 1.6 $
'  Purpose:  This page list the results of the search perform by the user
' ----------------------------------------------------------------------------------

%>

<%

Option Explicit

%>
<!--METADATA TYPE="TypeLib" NAME="Microsoft ActiveX Data Objects 2.5 Library" UUID="{00000205-0000-0010-8000-00AA006D2EA4}" VERSION="2.5"-->
<!--METADATA TYPE="TypeLib" NAME="Microsoft Scripting Runtime" UUID="{420B2830-E718-11CF-893D-00A0C9054228}" VERSION="1.0"-->
<!--METADATA TYPE="TypeLib" NAME="Microsoft CDO for Windows 2000 Library" UUID="{CD000000-8B95-11D1-82DB-00C04FB1625D}" VERSION="1.0"-->

<!-- #Include File = "Include/Settings.asp" -->
<!-- #Include File = "Include/Public.asp" -->

<!-- #Include File = "Classes/clsCase.asp" -->
<!-- #Include File = "Classes/clsCaseType.asp" -->
<!-- #Include File = "Classes/clsCategory.asp" -->
<!-- #Include File = "Classes/clsCollection.asp" -->
<!-- #Include File = "Classes/clsContact.asp" -->
<!-- #Include File = "Classes/clsDepartment.asp" -->
<!-- #Include File = "Classes/clsFile.asp" -->
<!-- #Include File = "Classes/clsGroup.asp" -->
<!-- #Include File = "Classes/clsLanguage.asp" -->
<!-- #Include File = "Classes/clsListItem.asp" -->
<!-- #Include File = "Classes/clsMail.asp" -->
<!-- #Include File = "Classes/clsNote.asp" -->
<!-- #Include File = "Classes/clsOrganisation.asp" -->
<!-- #Include File = "Classes/clsParameter.asp" -->
<!-- #Include File = "Classes/clsRole.asp" -->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>

<%

	Dim cnnDB
	Dim objCollection
	Dim binUserPermMask
	Dim strHTML, strSQL, strWHERE, strORDERBY, strKeywords, strNoOfResults
	Dim I, intUserID, intPage, intPages
	Dim intStatusID, intPriorityID, intCaseTypeID, intCategoryID, intContactID, intRepID
	Dim dtDateFrom, dtDateTo
	Dim strColumn, strColumnOrder, strQuery



	' Create the connection to the database
	Set cnnDB = CreateConnection

	' Determine the logged in User's ID
	intUserID = GetUserID
	binUserPermMask = GetUserPermMask


	' Get the settings from the QueryString & Posted form
	intPage = CInt(Request.Querystring("page"))
	If intPage = 0 Then
		intPage = 1
	Else
		' Do nothing
	End If


  If Len(Request.QueryString("Keywords")) > 0 Then
	  strKeywords = Request.QueryString("Keywords")
	Else
	  strKeywords = Request.Form("tbxKeywords")
	End If

  If Len(Request.QueryString("Contact")) > 0 Then
	  intContactID = CInt(Request.QueryString("Contact"))
	Else
	  intContactID = CInt(Request.Form("cbxContact"))
	End If

  If Len(Request.QueryString("CaseType")) > 0 Then
  	intCaseTypeID = CInt(Request.QueryString("CaseType"))
  Else
  	intCaseTypeID = CInt(Request.Form("cbxCaseType"))
  End If

  If Len(Request.QueryString("Category")) > 0 Then
	  intCategoryID = CInt(Request.QueryString("Category"))
  Else
	  intCategoryID = CInt(Request.Form("cbxCategory"))
	End If

  If Len(Request.QueryString("Status")) > 0 Then
  	intStatusID = CInt(Request.QueryString("Status"))
  Else
  	intStatusID = CInt(Request.Form("cbxStatus"))
  End If

  If Len(Request.QueryString("Priority")) > 0 Then
  	intPriorityID = CInt(Request.QueryString("Priority"))
  Else
  	intPriorityID = CInt(Request.Form("cbxPriority"))
  End If

  If Len(Request.QueryString("Rep")) > 0 Then
		intRepID = CInt(Request.QueryString("Rep"))
  Else
		intRepID = CInt(Request.Form("cbxRep"))
	End If

  If Len(Request.QueryString("dtFrom")) > 0 Then
	  dtDateFrom = Request.QueryString("dtFrom")
  Else
	  dtDateFrom = Request.Form("tbxDateFrom")
	End If

  If Len(Request.QueryString("dtTo")) > 0 Then
	  dtDateTo = Request.QueryString("dtTo")
	Else
	  dtDateTo = Request.Form("tbxDateTo")
	End If

  strQuery = "Keywords=" & strKeywords & "&Contact=" & intContactID & "&CaseType=" & intCaseTypeID &_
             "&Category=" & intCategoryID & "&Status=" & intStatusID & "&=Priority=" & intPriorityID &_
             "&Rep=" & intRepID & "&dtFrom=" & dtDateFrom & "&dtTo=" & dtDateTo

	strColumn = Request.QueryString("Column")
	strColumnOrder = Request.QueryString("Order")


  ' Generate the where clause
	strWHERE = ""

	If Len(strKeywords) > 0 Then
		strWHERE = strWHERE & "WHERE (Title LIKE '%" & strKeywords & "%' " &_
							  "OR Description LIKE '%" & strKeywords & "%' " &_
							  "OR Resolution LIKE '%" & strKeywords & "%') "
	Else
		' Do nothing

	End If

	If intContactID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND ContactFK=" & intContactID & " "
		Else
			strWHERE = strWHERE & "WHERE ContactFK=" & intContactID & " "
		End If
	Else
		' Do nothing

	End If

	If intCaseTypeID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND CaseTypeFK=" & intCaseTypeID & " "
		Else
			strWHERE = strWHERE & "WHERE CaseTypeFK=" & intCaseTypeID & " "
		End If
	Else
		' Do nothing

	End If

	If intCategoryID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND CatFK=" & intCategoryID & " "
		Else
			strWHERE = strWHERE & "WHERE CatFK=" & intCategoryID & " "
		End If
	Else
		' Do nothing

	End If

	If intPriorityID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND PriorityFK=" & intPriorityID & " "
		Else
			strWHERE = strWHERE & "WHERE PriorityFK=" & intPriorityID & " "
		End If
	Else
		' Do nothing

	End If

	If intStatusID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND StatusFK=" & intStatusID & " "
		Else
			strWHERE = strWHERE & "WHERE StatusFK=" & intStatusID & " "
		End If
	Else
		' Do nothing

	End If

	If intRepID > 0 Then
		If Len(strWHERE) > 0 Then
			strWHERE = strWHERE & "AND RepFK=" & intRepID & " "
		Else
			strWHERE = strWHERE & "WHERE RepFK=" & intRepID & " "
		End If
	Else
		' Do nothing

	End If

	If IsDate(dtDateFrom) And IsDate(dtDateTo) Then

		If Len(strWHERE) > 0 Then
		  ' Check what database we are using 3 -> MS Access
		  If Application("DBType") = 3 Then
  			strWHERE = strWHERE & "AND RaisedDate>#" & FormatDate(dtDateFrom, "mm/dd/yyyy") & " # AND RaisedDate<#" & FormatDate(dtDateTo, "mm/dd/yyyy") & "# "
  		  Else
			strWHERE = strWHERE & "AND RaisedDate>'" & FormatDate(dtDateFrom, "mm/dd/yyyy") & " 0:00' AND RaisedDate<'" & FormatDate(dtDateTo, "mm/dd/yyyy") & " 23:59' "
		  End If
		Else
		  ' Check what database we are using 3 -> MS Access
		  If Application("DBType") = 3 Then
			strWHERE = strWHERE & "WHERE RaisedDate>#" & FormatDate(dtDateFrom, "mm/dd/yyyy") & " # AND RaisedDate<#" & FormatDate(dtDateTo, "mm/dd/yyyy") & "# "
  		  Else
  			strWHERE = strWHERE & "WHERE RaisedDate>'" & FormatDate(dtDateFrom, "mm/dd/yyyy") & " 0:00' AND RaisedDate<'" & FormatDate(dtDateTo, "mm/dd/yyyy") & " 23:59' "
		  End If
		End If

	Else

		' No valid date

	End If

	Select Case strColumn

	  Case "1"
	    strORDERBY = "ORDER BY CasePK"

	  Case "2"
	    strORDERBY = "ORDER BY Title"

	  Case "3"
	    strORDERBY = "ORDER BY ContactFK"

	  Case "4"
	    strORDERBY = "ORDER BY RepFK"

	  Case "5"
	    strORDERBY = "ORDER BY StatusFK"

	  Case "6"
	    strORDERBY = "ORDER BY RaisedDate"

	  Case Else
	    strORDERBY = "ORDER BY CasePK"

	End Select

	If strColumnOrder = "1" Then
    strORDERBY = strORDERBY & " DESC"
	Else
    strORDERBY = strORDERBY & " ASC"
	End If

	' Build SQL query string
	strSQL = "SELECT * FROM tblCases " & strWHERE & " " & strORDERBY


	Set objCollection = New clsCollection

	objCollection.CollectionType = objCollection.clCase
	objCollection.Query = strSQL

	If Not objCollection.Load Then

		Response.Write objCollection.LastError

	Else

		If objCollection.BOF And objCollection.EOF Then

			' No records returned

		Else

		  strNoOfResults = objCollection.RecordCount

			If objCollection.RecordCount Mod Application("ITEMS_PER_PAGE")= 0 Then
				intPages = Int(objCollection.RecordCount / Application("ITEMS_PER_PAGE"))
			Else
				intPages = Int(objCollection.RecordCount / Application("ITEMS_PER_PAGE")) + 1
			End If

			strHTML = ""

			' Move the the record at the start of the next intPage
			objCollection.Move(Application("ITEMS_PER_PAGE") * (intPage - 1))

			I = 0

			Do While Not objCollection.EOF And Application("ITEMS_PER_PAGE") > I

        ' Alternate row colours
		    If I Mod 2 > 0 Then
		      ' Odd row number
  		  	strHTML = strHTML & "<TR bgcolor=""white"" class=""lhd_TableRow_Odd"">" & Chr(13)
		    Else
		      ' Even row number
  		  	strHTML = strHTML & "<TR bgcolor=""WhiteSmoke"" class=""lhd_TableRow_Even"">" & Chr(13)
		    End If

				strHTML = strHTML & "	<TD align=""Center"">" & objCollection.Item.ID & "</TD>" & Chr(13)
				strHTML = strHTML & "	<TD>&nbsp;<A href=caseModify.asp?ID=" & objCollection.Item.ID & ">" & objCollection.Item.Title & "</A></TD>" & Chr(13)
				strHTML = strHTML & "	<TD align=""Center"">" & objCollection.Item.Contact.UserName & "</TD>" & Chr(13)

        If Len(objCollection.Item.RepID) > 0 Then
				  strHTML = strHTML & "	<TD align=""Center"">" & objCollection.Item.Rep.UserName & "</TD>" & Chr(13)
				Else
				  strHTML = strHTML & "	<TD align=""Center"">-</TD>" & Chr(13)
				End If
				strHTML = strHTML & "	<TD align=""Center"">" & objCollection.Item.Status.ItemName & "</TD>" & Chr(13)
				strHTML = strHTML & "	<TD >&nbsp;" & DisplayDateTime(objCollection.Item.RaisedDate) & "</TD>" & Chr(13)
				strHTML = strHTML & "</TR>" & Chr(13)

				I = I + 1

				objCollection.MoveNext
			Loop

		End If

	End If

	Set objCollection = Nothing

%>

<HEAD>

  <META content="MstrHTML 6.00.2600.0" name=GENERATOR>
</HEAD>

<LINK rel="stylesheet" type="text/css" href="Default.css">


<BODY>
<P align=center>
<TABLE align=center cellSpacing=1 cellPadding=1 width="680" border=0>
  <TR>
    <TD>
      <%
      Response.Write DisplayHeader
      %>
    </TD>
  </TR>
  <TR>
    <TD>
		  <TABLE class="lhd_Table_Normal" cellSpacing="1">
		    <TR class="lhd_Heading1">
		    	<TD colspan="6" align="Center"><%=Lang("Search_Results")%>&nbsp;(<%=strNoOfResults%>)</TD>
		    </TR>
        <TR>
		      <TH width="7%" align="Center"><A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=CStr(intPage)%>&Column=1&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Case")%> #</A></TH>
		      <TH width="45%" align="Left">&nbsp;<A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=intPage%>&Column=2&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Title")%></A></TH>
		      <TH width="10%" align="Center"><A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=intPage%>&Column=3&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Contact")%></A></TH>
		      <TH width="10%" align="Center"><A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=intPage%>&Column=4&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Assigned")%></A></TH>
		      <TH width="10%" align="Center"><A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=intPage%>&Column=5&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Status")%></A></TH>
		      <TH width="18%" align="Left">&nbsp;<A href="caseSearchResults.asp?<%=strQuery%>&Page=<%=intPage%>&Column=6&Order=<%If strColumnOrder="1" Then Response.Write "0" Else Response.Write "1" End If%>"><%=Lang("Raised_Date")%></A></TH>
  		  </TR>

	      <%
		      Response.Write strHTML
		  	%>
		  </TABLE>
	</TD>
</TR>
<TR>
  <TD>
		<TABLE class=Normal cellSpacing=1 cellPadding=1 width="100%" border=0 bgColor=white>
			<%
			strHTML = ""

			If intPages > 1 Then

				strHTML = strHTML & "<TR style=""FONT-WEIGHT: Bold"">"
				strHTML = strHTML & "  <TD align=""Center"" style=""FONT-SIZE: 9.5pt"">"

				If intPage > 1 Then
          strHTML = strHTML & "    <A href=""caseSearchResults.asp?" & strQuery & "&Page=" & CStr(intPage-1) & "&Column=" & strColumn & "&Order=" & strColumnOrder & """>" & Lang("Previous") & "</A>"
				Else
					strHTML = strHTML & "    <FONT color=""gray"">" & Lang("Previous") & "</FONT>"
				End If

    		strHTML = strHTML & "&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;"

				If intPages > intPage Then
          strHTML = strHTML & "    <A href=""caseSearchResults.asp?" & strQuery & "&Page=" & CStr(intPage+1) & "&Column=" & strColumn & "&Order=" & strColumnOrder & """>" & Lang("Next") & "</A>"
				Else
					strHTML = strHTML & "    <FONT color=""gray"">" & Lang("Next") & "</FONT>"
				End If

				strHTML = strHTML & "  </TD>"
				strHTML = strHTML & "</TR>"

			Else

				' Do nothing

			End If

			Response.Write strHTML
			%>
		</TABLE>
	</TD>
</TR>
<TR>
    <TD>
    <%
    Response.Write DisplayFooter
    %>
    </TD>
</TR>

</P>
</BODY>
</HTML>

<%
cnnDB.Close
Set cnnDB = Nothing
%>