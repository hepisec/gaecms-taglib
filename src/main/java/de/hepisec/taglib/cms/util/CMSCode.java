package de.hepisec.taglib.cms.util;

import java.util.Map;

/**
 *
 * @author Hendrik Pilz
 */
public class CMSCode {
    String tagName;
    Map<String, String> attributes;

    public CMSCode(String tagName, Map<String, String> attributes) {
        this.tagName = tagName;
        this.attributes = attributes;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }        
}
