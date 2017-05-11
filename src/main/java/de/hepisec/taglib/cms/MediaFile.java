package de.hepisec.taglib.cms;

import com.google.appengine.api.utils.SystemProperty;
import de.hepisec.taglib.cms.util.MediaFileUtil;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author Hendrik Pilz
 */
public class MediaFile extends BodyTagSupport {

    private String blobid;
    private String bucket;

    @Override
    public int doAfterBody() throws JspException {
        try {
            BodyContent bc = getBodyContent();

            if (null != bc) {
                String altText = bc.getString();
                bc.clearBody();
                bc.getEnclosingWriter().write(MediaFileUtil.getImageCode(getBucket(), blobid, altText));
            }
        } catch (IOException ex) {
            throw new JspException(ex);
        }

        return SKIP_BODY;
    }

    public void setBlobid(String blobid) {
        this.blobid = blobid;
    }

    private String getBucket() {
        if (null != this.bucket) {
            return this.bucket;
        }

        ServletContext ctx = pageContext.getServletContext();
        String bucket = ctx.getInitParameter("bucket");

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
            bucket = ctx.getInitParameter("staging-bucket");
        }

        if (null == bucket) {
            bucket = "";
        }

        this.bucket = bucket;
        return this.bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
