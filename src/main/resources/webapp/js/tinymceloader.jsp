<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%

    Pattern pattern = Pattern.compile("^[a-zA-Z0-9-]*$");
    Matcher matcher = pattern.matcher(request.getParameter("id"));

    if (matcher.matches()) {
%>
tinymce.init({
selector: '#textarea-<%= request.getParameter("id") %>',
menubar: false,
inline: true,
plugins: 'lists advlist code',
toolbar: 'bold italic underline strikethrough | '
        +'alignnone aligncenter alignright alignjustify | '
        +'formatselect | '
        +'bullist numlist outdent indent | '
        +'code'
});    
<%
    }

%>