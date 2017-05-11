package de.hepisec.taglib.cms.util;

/**
 *
 * @author Hendrik Pilz
 */
public class ImgCodeHandler implements CMSCodeHandler {
    private String bucket;

    public ImgCodeHandler(String bucket) {
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }        
    
    @Override
    public void handle(StringBuilder text, int firstPos, int lastPos, CMSCode code) {
        String blobid = code.getAttributes().get("name");
        String altText = code.getAttributes().get("alt");
        
        if (null == blobid) {
            return;
        }
        
        if (null == altText) {
            altText = "";
        }
        
        text.replace(firstPos, lastPos, MediaFileUtil.getImageCode(bucket, blobid, altText));
    }
    
}
