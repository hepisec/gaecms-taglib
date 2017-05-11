package de.hepisec.taglib.cms.util;

/**
 *
 * @author Hendrik Pilz
 */
public interface CMSCodeHandler {
    public void handle(StringBuilder text, int firstPos, int lastPos, CMSCode code);
}
