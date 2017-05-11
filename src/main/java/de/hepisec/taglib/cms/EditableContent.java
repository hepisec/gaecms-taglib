package de.hepisec.taglib.cms;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import de.hepisec.taglib.cms.util.CMSCodeProcessor;
import de.hepisec.taglib.cms.util.CMSUtil;
import de.hepisec.taglib.cms.util.ImgCodeHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.encoder.Encode;

/**
 *
 * @author Hendrik Pilz
 */
public class EditableContent extends SimpleTagSupport {

    private String bucket = null;
    private String id;
    private String mode = "plain";

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

        try {
            if (request.isUserInRole("admin") && !"true".equalsIgnoreCase(request.getParameter("viewcontent"))) {
                if (request.getMethod().equalsIgnoreCase("post") && request.getParameter("id").equals(id)) {
                    saveContent(request);
                }

                writeEditor(request);
            } else {
                writeContent(request);
            }
        } catch (IOException ex) {
            throw new JspException(ex);
        }
    }

    private void saveContent(HttpServletRequest request) throws IOException {
        String pContent = request.getParameter("content") != null
                ? request.getParameter("content")
                : request.getParameter("textarea-" + id);

        if (null == pContent) {
            return;
        }

        Storage storage = StorageOptions.getDefaultInstance().getService();

        BlobId blobId = BlobId.of(getBucket(), id);
        byte[] content = pContent.getBytes("UTF-8");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

        try (WriteChannel writer = storage.writer(blobInfo)) {
            writer.write(ByteBuffer.wrap(content, 0, content.length));
        }
    }

    private void writeEditor(HttpServletRequest request) throws JspException {
        String content = getContent();

        try {
            JspWriter out = getJspContext().getOut();
            String postUri = request.getRequestURI();
            String viewUri = request.getRequestURI();

            if (request.getQueryString() != null) {
                postUri = postUri + request.getQueryString();
                viewUri = viewUri + request.getQueryString() + "&viewcontent=true";
            } else {
                viewUri = viewUri + "?viewcontent=true";
            }

            out.println("<form method=\"POST\" action=\"" + Encode.forHtmlAttribute(postUri) + "\">");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\" />");

            if ("markdown".equals(mode)) {
                out.println("<label><small>Markdown Editor</small></label>");
            }

            if ("inline".equals(mode)) {
                out.println("<div id=\"textarea-" + id + "\">");
                out.println(content);
                out.println("</div>");
                CMSUtil.addExternalJavaScript(request, "/js/tinymce/tinymce.min.js");
                CMSUtil.addExternalJavaScript(request, "/js/tinymceloader.jsp?id=" + id);
            } else {
                out.println("<textarea id=\"textarea-" + id + "\" name=\"content\" class=\"form-control gaecms-textarea gaecms-" + mode + "-editor\">");
                out.println(Encode.forHtmlContent(content));
                out.println("</textarea>");

                if ("html".equals(mode)) {
                    CMSUtil.addExternalJavaScript(request, "/js/codemirror.js");
                    CMSUtil.addExternalJavaScript(request, "/js/mode/xml.js");
                    CMSUtil.addExternalJavaScript(request, "/js/mode/javascript.js");
                    CMSUtil.addExternalJavaScript(request, "/js/mode/css.js");
                    CMSUtil.addExternalJavaScript(request, "/js/mode/htmlmixed.js");
                    CMSUtil.addExternalJavaScript(request, "/js/codemirrorloader.jsp?id=" + id);
                }
            }

            out.println("<button type=\"submit\" name=\"submit\" class=\"btn btn-primary\"><i class=\"fa fa-save fa-lg\"></i> Save</button>");
            out.println("<a href=\"javascript:addMedia('" + id + "')\" class=\"btn btn-default\"><i class=\"fa fa-picture-o fa-lg\"></i> Add Media</a>");
            out.println("<a href=\"" + Encode.forHtmlAttribute(viewUri) + "\" class=\"btn btn-default\"><i class=\"fa fa-eye fa-lg\"></i> View Content</a>");
            CMSUtil.addExternalJavaScript(request, "/js/addmedia.js");

            out.println("</form>");
        } catch (java.io.IOException ex) {
            throw new JspException(ex);
        }
    }

    private void writeContent(HttpServletRequest request) throws JspException {
        String content = getContent();

        try {
            switch (mode) {
                case "plain":
                    writePlainContent(content);
                    break;
                case "inline":
                    writeHtmlContent(content);
                    break;
                case "html":
                    writeHtmlContent(content);
                    break;
                case "markdown":
                    writeMarkdownContent(content);
                    break;
                default:
                    JspWriter out = getJspContext().getOut();
                    out.println("Unsupported content mode: " + mode);
                    break;
            }

            if (request.isUserInRole("admin")) {
                writeEditorButton(request);
            }
        } catch (java.io.IOException ex) {
            throw new JspException("Error in EditableContent tag", ex);
        }
    }

    private String getBucket() {
        if (null != this.bucket) {
            return this.bucket;
        }

        ServletContext ctx = ((PageContext) getJspContext()).getServletContext();
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

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Use either "plain", "inline", "html" or "markdown"
     *
     * Default mode: "plain"
     *
     * @param mode
     * @throws IllegalArgumentException if mode is not supported
     */
    public void setMode(String mode) {
        if (!"plain".equals(mode) && !"inline".equals(mode) && !"html".equals(mode) && !"markdown".equals(mode)) {
            throw new IllegalArgumentException("Editor mode is not supported.");
        }
        
        this.mode = mode;
    }

    private String getContent() {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        try {
            BlobId blobId = BlobId.of(getBucket(), id);

            if (null == storage.get(blobId)) {
                return "";
            }

            return new String(storage.readAllBytes(blobId), "UTF-8");
        } catch (StorageException | UnsupportedEncodingException ex) {
            Logger.getLogger(EditableContent.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
            return "";
        }
    }

    private void writePlainContent(String content) throws IOException {
        getJspContext().getOut().println(processCmsCodes(Encode.forHtml(content)));
    }

    private void writeHtmlContent(String content) throws IOException {
        getJspContext().getOut().println(processCmsCodes(content));
    }

    private void writeMarkdownContent(String content) throws IOException {
        String htmlEncodedContent = Encode.forHtml(content);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(htmlEncodedContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        getJspContext().getOut().println(processCmsCodes(renderer.render(document)));
    }

    private void writeEditorButton(HttpServletRequest request) throws IOException {
        JspWriter out = getJspContext().getOut();
        String editUri = request.getRequestURI();

        if (request.getQueryString() != null) {
            editUri = editUri + request.getQueryString().replaceAll("(?i)(\\\\?|&)viewcontent=true", "");
        }

        out.println("<div class=\"gaecms-editor-button\">");
        out.println("<a href=\"" + Encode.forHtmlAttribute(editUri) + "\" class=\"btn btn-default\"><i class=\"fa fa-edit fa-lg\"></i></a>");
        out.println("</div>");
    }

    private String processCmsCodes(String content) {
        CMSCodeProcessor processor = new CMSCodeProcessor();
        processor.addCodeHandler("img", new ImgCodeHandler(getBucket()));
        return processor.process(content);
    }
}
