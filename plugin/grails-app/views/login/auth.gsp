<html>
<head>
    <title>Login</title>
</head>
<body>

<div id="login" class="grid_4 suffix_1" style="margin-bottom:15px;">
    <g:if test='${flash.message}'>
        <div class='login_message widget gb-warning'><p>${flash.message}</p></div>
    </g:if>
    <div class=' widget gb-blue loginform'>
        <form action='${postUrl}' method='POST' id='loginForm'>
            <p style="margin-top:15px">
                <label for='j_username'>User Name / Email</label>
                <input AUTOCOMPLETE=on type='text' class='text_' name='username' id='j_username' value="${username}"/>
            </p>
            <p>
                <label for='j_password'>Password</label>
                <input AUTOCOMPLETE=on type='password' class='text_' name='password' id='j_password'/>
            </p>
            <p class="remember">
                <input type='checkbox' class='chk' id='remember_me' name='_spring_security_remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if> />
                <label for='remember_me'>Remember me</label>
            </p>
            <p class='submit'>
                <input type='submit' value='Login' class='inputSubmit' name='loginButton' id='loginButton'/>
            </p>
        </form>
    </div>
</div> <!--end div id=login-->
</body>
</html>
