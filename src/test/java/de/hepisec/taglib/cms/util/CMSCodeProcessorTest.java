package de.hepisec.taglib.cms.util;

import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Hendrik Pilz
 */
public class CMSCodeProcessorTest {
    
    public CMSCodeProcessorTest() {
    }
    
    @Test   
    public void replaceImages() {
        String text = "Hello {{img name=\"hello world\"}} World!!!";
        
        CMSCodeProcessor processor = new CMSCodeProcessor();
        processor.addCodeHandler("img", new CMSCodeHandler() {
            @Override
            public void handle(StringBuilder text, int firstPos, int lastPos, CMSCode code) {
                text.replace(firstPos, lastPos, "<img src=\"" + code.getAttributes().get("name") + "\" />");
            }
        });
        
        text = processor.process(text);
        Logger.getLogger(CMSCodeProcessorTest.class.getName()).info(text);
        assertTrue(text.equals("Hello <img src=\"hello world\" /> World!!!"));
    }
}
