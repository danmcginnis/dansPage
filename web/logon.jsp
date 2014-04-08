<%-- 
    Document   : index
    Created on : Feb 12, 2014, 9:43:32 AM
    Author     : dan mcginnis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link id="cssLinkID" href="defaultTheme.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script src="myscript.js"  type="text/javascript"></script>
        <script>
            // apply “tab selected” to the element with id “home”
            document.getElementById(this).className = "tab selected";
        </script>
        <title>Get Out Local</title>
    </head>

    
<jsp:include page="pre-content.jsp" />

    <%
            String msg = ""; // first display will show nothing on screen.
            String user_Name = request.getParameter("uname");
            String user_Role = "";
            String pwEncrypted = "";
            if (user_Name == null) {
                user_Name = ""; // surpress "NULL" (first display) from showing up in the username text field.
            } // postback, check username and password
            else {
                String passW = request.getParameter("pw");

                // For this simple/sample code, username must = password.  In a real
                // application you would encrypt the password before storing it in the DB and also
                // encrypt the pw entered by the user -- checking for a match with what's in the db.
                if (passW.equalsIgnoreCase(user_Name)) {
                    if (user_Name.equalsIgnoreCase("Bob")) {
                        user_Role = "view";  // can view private pages (no edit)
                        msg = "Hello " + user_Name + " (your role is " + user_Role + ")";
                    } else if (user_Name.equalsIgnoreCase("Dave")) {
                        user_Role = "edit"; // can edit (but not admin)
                        msg = "Hello " + user_Name + " (your role is " + user_Role + ")";
                    } else if (user_Name.equalsIgnoreCase("Eugene")) {
                        user_Role = "admin"; // can access admin page
                        msg = "Hello " + user_Name + ".  You are almost God (your role is " + user_Role + ")";
                    } else {
                        msg = "That username and password were not found in our database."; // log on not sucessful
                    }
                } // bad password (in this contrived example) is when user name <> password.
                else {
                    msg = "Invalid password !!";
                }

                // if username/password is OK, user_Role will have received a value.
                if (user_Role.length() > 0) {
                    session.setAttribute("userName", user_Name);
                    session.setAttribute("userRole", user_Role);
                } // successful log in

                pwEncrypted = Encrypt.encryptPw(passW);
            } // postback
        %>


        <form method="post" action="logon.jsp" >
            Please enter your username: <input type="input" name="uname" value="<%=user_Name%>">
            <br/>
            Please enter your password: <input type="password" name="pw">
            <br/>
            Your password encrypts to this: <%= pwEncrypted%>
            <br/>
            <br/>
            <input type = "submit" value = "Log On" > <br/>
           
            <h3>  <%=msg%></h3>
        </form>


<jsp:include page="post-content.jsp" />
