<#--
just a little comment header
-->
<html>[BR]
<head>[BR]
  <title>Welcome!</title>[BR]
</head>[BR]
<body>[BR]
  <#-- Greet the user with his/her name -->[BR]
  <h1>Welcome ${user}!</h1>[BR]
  <p>We have these animals:[BR]
  <ul>[BR]
  <#list animals as being>[BR]
    <li>${being.name} for ${being.price} Euros[BR]
  </#list>[BR]
  </ul>[BR]
</body>[BR]
</html>
