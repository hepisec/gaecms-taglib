package de.hepisec.taglib.cms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Hendrik Pilz
 */
public class CMSCodeProcessor {
    private Map<String, CMSCodeHandler> codeHandlerMap;
    private Pattern attrPattern = Pattern.compile("([a-zA-Z]+)=\"([^\"]+)\""); // atTR="value"

    public CMSCodeProcessor() {
        codeHandlerMap = new HashMap<>();
    }
    
    public void addCodeHandler(String tagName, CMSCodeHandler codeHandler) {
        codeHandlerMap.put(tagName, codeHandler);
    }
    
    public void removeCodeHandler(String tagName) {
        codeHandlerMap.remove(tagName);
    }
    
    public String process(String text) {
        StringBuilder sb = new StringBuilder(text);
        
        int pos = 0;
        
        while ((pos = sb.indexOf("{{", pos)) >= 0) {
            int lastPos = sb.indexOf("}}", pos) + 2;
            
            if (2 > lastPos) {
                break;
            }
            
            String tag;
            Map<String, String> attributes = new HashMap<>();
            int firstSpacePos = sb.indexOf(" ", pos + 2);
            
            if (0 <= firstSpacePos) { // tag with attributes
                tag = sb.substring(pos + 2, firstSpacePos);
                
                Matcher matcher = attrPattern.matcher(sb.subSequence(firstSpacePos, lastPos - 2));
                
                while (matcher.find()) {
                    attributes.put(matcher.group(1), matcher.group(2));
                }
            } else {
                tag = sb.substring(pos + 2, lastPos);
            }

            CMSCodeHandler handler = codeHandlerMap.get(tag);
            
            if (null != handler) {
                handler.handle(sb, pos, lastPos, new CMSCode(tag, attributes));
            }
            
            pos = lastPos;
        }
        
        return sb.toString();
    }
}
