
function addMedia(id) {
    var mediaLibraryContainer = document.getElementById("media-library-container");

    if (mediaLibraryContainer === null) {
        mediaLibraryContainer = document.createElement("div");
        $(mediaLibraryContainer).attr("id", "media-library-container");
        $(mediaLibraryContainer).attr("role", "dialog");
        $(mediaLibraryContainer).attr("tabindex", "-1");
        $(mediaLibraryContainer).addClass("modal fade");
        $(mediaLibraryContainer).html('<div class="modal-dialog" role="document">'
                + '<div class="modal-content">'
                + '<div class="modal-header">'
                + '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
                + '<h4 class="modal-title">Media Library</h4>'
                + '</div>'
                + '<div class="modal-body">'
                + '</div>'
                + '</div><!-- /.modal-content -->'
                + '</div><!-- /.modal-dialog -->');
    }

    $.get("/admin/MediaLibrary?id=" + id + "&embedded=true", function (data) {
        $("body").append(mediaLibraryContainer);
        $("#media-library-container .modal-body").html(data);
        $("#media-library-container").modal('show');
    });

    return false;
}

/**
 * From: http://stackoverflow.com/questions/1064089/inserting-a-text-where-cursor-is-using-javascript-jquery
 */
function insertMedia(id, blobId) {
    var txt = $("#textarea-" + id);
    var txtToAdd = '{{img name="' + blobId + '" alt="Alternativtext"}}';

    if (txt.hasClass('mce-content-body')) {
        txt.append(txtToAdd);
    } else {
        var caretPos = txt[0].selectionStart;
        var textAreaTxt = txt.val();
        txt.val(textAreaTxt.substring(0, caretPos) + txtToAdd + textAreaTxt.substring(caretPos))
    }

    $("#media-library-container").modal('hide');
}