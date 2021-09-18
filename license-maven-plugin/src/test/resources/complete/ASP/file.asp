<%
  Dim dtmHour

  dtmHour = Hour(Now())

  If dtmHour < 12 Then
    strGreeting = "Good Morning!"
  Else
    strGreeting = "Hello!"
  End If
%>

<%= strGreeting %>
