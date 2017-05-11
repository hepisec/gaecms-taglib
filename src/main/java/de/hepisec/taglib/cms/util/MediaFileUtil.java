package de.hepisec.taglib.cms.util;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.storage.BlobId;
import org.owasp.encoder.Encode;

/**
 *
 * @author Hendrik Pilz
 */
public class MediaFileUtil {
    public static String getImageCode(String bucket, String blobid, String altText) {
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        BlobId blobId = BlobId.of(bucket, blobid);
        String servingUrl = "https://storage.googleapis.com/" + blobId.getBucket() + "/" + blobId.getName();

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            String googleStorageFileName = "/gs/" + blobId.getBucket() + "/" + blobId.getName();
            servingUrl = imagesService.getServingUrl(ServingUrlOptions.Builder.withGoogleStorageFileName(googleStorageFileName).secureUrl(true));
        }
        
        return "<img src=\"" + Encode.forHtmlAttribute(servingUrl) + "\" alt=\"" + Encode.forHtmlAttribute(altText) + "\" class=\"img-responsive\" />";
    }
}
