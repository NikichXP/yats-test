<%@page import="eduportal.dao.entity.Corporation"%>
<%@page import="eduportal.dao.UserDAO"%>
<%@page import="eduportal.api.UserAPI"%>
<%@page import="eduportal.model.AuthContainer"%>
<%@page import="eduportal.dao.entity.UserEntity"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%!UserEntity user;%>
	<%
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("sesToken")) {
				user = AuthContainer.getUser(cookie.getValue());
			}
		}
		if (user == null) {
			response.sendRedirect("/");
			return;
		}
	%>
	<h1>Company:</h1>
	<br>
	<table>
	<% for (Corporation comp : UserDAO.getCorpList(user)) { %>
		<tr>
			<td><%= comp.getName() %></td>
			<td><%= comp.getId() %></td>
			<td>4</td>
		</tr>
		<% } %>
	</table>

	<h1></h1>
	<br>
	<h1></h1>
	<br>
	<h1></h1>
	<br>

</body>
</html>