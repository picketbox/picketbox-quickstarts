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
				<h2>PicketBox Quickstart</h2>
			</center>
			<center>
				<input class="loginBtn2" type="button" value="Login" onclick="document.getElementById('loginDiv').style.display = '';document.getElementById('introDiv').style.display = 'none'" />&nbsp;&nbsp;&nbsp;&nbsp;<input
					class="loginBtn2" type="button" value="Sign Up"
					onclick="window.location='signup.jsp'" />
			</center>
		</div>
	</div>
	<div id="loginDiv" class="loginBox" style="display: none;">
		<div id="loginBoxContent">
			<form id="login_form" name="login_form" method="post"
				action="j_security_check"
				enctype="application/x-www-form-urlencoded">

				<p>
					<input id="username" type="text" name="j_username" size="30"
						placeholder="User ID" />
				</p>
				<p>
					<input id="password" type="password" name="j_password" value=""
						size="30" placeholder="Password" />
				<div id="loginBtnContainer">
					<input class="loginBtn" type="submit" name="submit" value="Login" />
				</div>
				</p>
			</form>
		</div>
	</div>
	<div id="footerContainer">
		<div id="footerContent">
			<span><a href="http://jboss.org/picketbox">PicketBox at
					JBoss.Org</a> | <a
				href="https://docs.jboss.org/author/display/SECURITY">Help</a></span>
		</div>
	</div>
</body>
</html>