package de.hepisec.taglib.cms;

import de.hepisec.taglib.cms.util.CMSUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Hendrik Pilz
 */
public class ExternalScript extends SimpleTagSupport {

    private String script;
    private boolean print = false;

    /**
     * Called by the container to invoke this tag. The implementation of this
     * method is provided by the tag library developer, and handles all tag
     * processing, body iteration, etc.
     *
     * @throws javax.servlet.jsp.JspException
     */
    @Override
    public void doTag() throws JspException {
        PageContext ctx = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

        if (script != null) {
            CMSUtil.addExternalJavaScript(request, script);
        }
        
        if (print) {
            try {
                CMSUtil.writeExternalJavaScripts(request, getJspContext().getOut());
            } catch (IOException ex) {
                throw new JspException(ex);
            }
        }
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }
}
