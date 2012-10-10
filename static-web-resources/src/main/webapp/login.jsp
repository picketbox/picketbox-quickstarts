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
<body id="loginBody">
	<div id="introDiv" class="loginBox">
		<div id="loginBoxContent">
			<center>
				<h2 style="margin-top: -25px;">PicketBox Quickstart</h2>
			</center>
			<p>
				To get started, first create an user account by clicking on the <b>Register</b>
				button bellow. After that try to <b>Sign In</b> using your credentials.
			</p>
			<center>
				<input id="siginBtn" class="loginBtn2" type="button" value="Sign In"
					onclick="document.getElementById('loginDiv').style.display = '';document.getElementById('introDiv').style.display = 'none'" />&nbsp;&nbsp;&nbsp;&nbsp;<input
					class="loginBtn2" type="button" value="Register"
					onclick="window.location='signup.jsp'" />
			</center>
		</div>
	</div>
	<div id="loginDiv" class="loginBox" style="display: none;">
		<div id="loginBoxContent">
			<form id="login_form" name="login_form" method="post"
				action="j_security_check"
				enctype="application/x-www-form-urlencoded">
				<% if (request.getParameter("error") != null) { %>
				<p style="color: red;margin-top: -25px;padding-right: 15px;float:right;">Authentication failed.</p>
				<% } %>
				<p>
					<input id="username" title="Provide your User ID or Username."
						type="text" name="j_username" placeholder="User ID" required />
				</p>
				<p>
					<input id="password" title="Provide your password." type="password"
						name="j_password" value="" placeholder="Password" required />
				<div id="loginBtnContainer">
					<input class="loginBtn" type="submit" name="submit" value="Login" />
				</div>
				</p>
			</form>
		</div>
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
<% 
	if (request.getParameter("error") != null || request.getParameter("signin") != null) {
%>
	<script>document.getElementById('siginBtn').click();</script>
<%
    
	}
%>