<!--
  ~ JBoss, Home of Professional Open Source.
  ~ Copyright (c) 2011, Red Hat, Inc., and individual contributors
  ~ as indicated by the @author tags. See the copyright.txt file in the
  ~ distribution for a full listing of individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  -->
<%@page import="org.picketbox.core.UserContext"%>
<%@page import="org.picketbox.http.wrappers.RequestWrapper"%>
<%@page import="org.picketlink.idm.model.Role"%>
<%
    RequestWrapper requestWrapper = (RequestWrapper) request;
    UserContext userContext = requestWrapper.getUserContext();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>PicketBox Quickstarts: PicketBox Http Form Example</title>
<link rel="StyleSheet" href="resources/css/theme.css" type="text/css">
</head>
<body>
	<div class="defaultBoxContainer">
		<h2>
			<p>
				Welcome <span style="color: green"><%=request.getUserPrincipal().getName()%></span> !
			</p>
		</h2>
		<h2>
			<p>Check your information:</p>
		</h2>
		<div style="">
			<p>
				<b>First Name:</b>
				<%=userContext.getUser().getFirstName()%>
			</p>
			<p>
				<b>Last Name:</b>
				<%=userContext.getUser().getLastName()%>
			</p>
			<p>
				<b>E-mail:</b>
				<%=userContext.getUser().getEmail()%>
			</p>
			<p>
				<b>Roles:</b>
				<% 
					for (Role role: userContext.getRoles()) {
    			%>
				<%= role.getName() %>
				<% 
					} 
				%>
			</p>
		</div>
		<h4>
			<p>
			<center>
				Click here to access the <a href="droolsProtectedResource.jsp">Drools Protected Resource</a>.
			</center>
			</p>
		</h4>
		<h2>
			<p>
			<center>
				Click here to <a href="picketbox_logout">Logout</a>.
			</center>
			</p>
		</h2>
	</div>
	<div id="footerContainer">
		<div id="footerContent">
			<span class="footerContentLeft"><a
				href="http://jboss.org/picketbox">PicketBox at JBoss.Org</a><span
				class="footerContentSeparator">|</span><a
				href="http://github.com/picketbox">Follow us on Github</a><span
				class="footerContentSeparator">|</span><a
				href="https://docs.jboss.org/author/display/SECURITY">Documentation</a></span>
		</div>
	</div>
</body>
</html>