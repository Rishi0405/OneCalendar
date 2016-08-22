<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>MyProfile</title>
</head>
<body>
<p>I'm in jsp</p>

<%  String name = request.getParameter("fname");
	String email = request.getParameter("email");
%>
Name = <%=name%>
Email = <%=email%>

</body>
</html>