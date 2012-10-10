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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>PicketBox Quickstarts: PicketBox Http Form Example</title>
<link rel="StyleSheet" href="resources/css/theme.css" type="text/css">
</head>
<body>
	<div class="defaultBoxContainer" style="height: 500px;width: 400px;">
		<h2>
			<p>
				Your Information: <span style="font-size: 13px;color: red;">
					<% if (request.getSession().getAttribute("message") != null)  out.println(request.getSession().getAttribute("message")); else out.println(""); %>
				</span>
			</p>
		</h2>
		<div style="">
			<form id="signup_form" name="signup_form" method="post"
				action="signup" enctype="application/x-www-form-urlencoded">
				<p>
					<label for="firstName">User ID: </label> <br /> <input type="text"
						id="userId" name="userId" size="32" required="required" /> *
				</p>
				<p>
					<label for="firstName">First Name:</label> <br /> <input
						type="text" id="firstName" name="firstName" size="32"
						required="required" /> *
				</p>
				<p>
					<label for="lastName">Last Name:</label> <br /> <input type="text"
						id="lastName" name="lastName" size="32" required="required" /> *
				</p>
				<p>
					<label for="email">E-mail:</label> <br /> <input type="text"
						id="email" name="email" size="32" required="required" /> *
				</p>
				<p>
					<label for="password">Password:</label> <br /> <input
						type="password" id="password" name="password" required="required" />
					*
				</p>
				<p>
					<label for="confirmPassword">Confirm Password:</label> <br /> <input
						type="password" id="confirmPassword" name="confirmPassword"
						required="required" /> *
				</p>
				<br />
				<p>
				<center>
					<input class="loginBtn2" type="submit" value="Finish" />
					<input class="loginBtn2" type="button" value="Back" onclick="window.location='<%= request.getContextPath() %>'"/>
				</center>
				</p>
			</form>
		</div>
	</div>
	<br/><br/><br/>
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
<%
	session.removeAttribute("message");
%>