package de.hepisec.taglib.cms.util;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

/**
 *
 * @author Hendrik Pilz
 */
public class CMSUtil {
    private static final String EXTERNAL_JAVA_SCRIPTS = "de.hepisec.taglib.cms.util.ExternalJavaScripts";
    
    public static void addExternalJavaScript(HttpServletRequest request, String scriptUri) {
        @SuppressWarnings("unchecked")
        Set<String> externalJavaScripts = (Set<String>) request.getAttribute(EXTERNAL_JAVA_SCRIPTS);
        
        if (null == externalJavaScripts) {
            externalJavaScripts = new LinkedHashSet<>();
        }
        
        externalJavaScripts.add(scriptUri);
        request.setAttribute(EXTERNAL_JAVA_SCRIPTS, externalJavaScripts);
    }
    
    public static void writeExternalJavaScripts(HttpServletRequest request, JspWriter out) throws IOException {
        @SuppressWarnings("unchecked")
        Set<String> externalJavaScripts = (Set<String>) request.getAttribute(EXTERNAL_JAVA_SCRIPTS);
        
        if (null == externalJavaScripts) {
            return;
        }
        
        for (String script : externalJavaScripts) {
            out.println("<script type=\"text/javascript\" src=\"" + script + "\"></script>");
        }
    }
}
