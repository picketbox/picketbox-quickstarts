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
<link rel="StyleSheet" href="resources/css/idp.css" type="text/css">
</head>
<body>
	<img src="resources/images/picketbox-banner-1180px.png" class="picketboxBanner"/>
	<div class="loginBox">
		<form id="login_form" name="login_form" method="post"
			action="j_security_check" enctype="application/x-www-form-urlencoded">
			<center>
				<p>
					Welcome to the <b>PicketBox Quickstarts</b>
				</p>
				<p>Please login to proceed.</p>
			</center>

			<div style="margin-left: 15px;">
				<p>
					<label for="username"> Username</label><br /> <input id="username"
						type="text" name="j_username" size="20" /> (ex: admin)
				</p>
				<p>
					<label for="password"> Password</label><br /> <input id="password"
						type="password" name="j_password" value="" size="20" /> (ex: admin)
				</p>
				<input id="submit" type="submit" name="submit" value="Login" class="buttonmed" />
			</div>
		</form>
	</div>
</body>
</html>