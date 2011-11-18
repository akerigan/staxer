<%
    response.setStatus(301);
    response.setHeader("Location", "sample?wsdl");
    response.setHeader("Connection", "close");
%> 
