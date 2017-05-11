<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9-]*$");
    Matcher matcher = pattern.matcher(request.getParameter("id"));

    if (matcher.matches()) {
%>
var editor = CodeMirror.fromTextArea(document.getElementById("textarea-<%= request.getParameter("id") %>"), {
mode: "htmlmixed",
lineNumbers: true
});  
<%
    }

%>