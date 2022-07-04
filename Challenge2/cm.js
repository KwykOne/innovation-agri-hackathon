// RahaInfoSystems : Project TripMantu
// For ONDC hackathon
var ja_g_url="";
var stack = Array();
function getDropBoxPanel(formElement)
{

        var txt = "<div id=olContainer><div id=uploadedImageDisplay></div>" +
                        "<form id=DropboxForm name=DropboxForm>" +
                        "<div id='dropBox'>" +
                        "<p>Select file to upload</p>" +
                        "</div><div id=DropBoxFormElements>" +
                        "</div><div id=DropBoxFiles><input type='file' name='fileInput' id='fileInput'/>" +
                        "</div>" +
                        "</form></div>";
        return txt;
}


function activateDropBox(url){
        //file input field trigger when the drop box is clicked
        ja_g_url = url;
        $("#dropBox").click(function(){
                $("#fileInput").click();
        });

        //prevent browsers from opening the file when its dragged and dropped
        $(document).on('drop dragover', function (e) {
                e.preventDefault();
        });

        //call a function to handle file upload on select file
        $('input[type=file]').on('change', fileUpload);
}

function formUpdate(data){
     var xhr = new XMLHttpRequest();

        //post file data for upload
        xhr.open('POST', ja_g_url, true);
        xhr.send(data);
        xhr.onload = function () {
                //get response and show the uploading status
                var response = JSON.parse(xhr.responseText);
                if(xhr.status === 200 && response.status == 1){
                        document.getElementById('overlaytext').innerHTML = "<div style='background-color:white;color:orange;font-size:30px;padding:10px;'>Image is uploaded successfully</div>";
                        on();
                        window.setTimeout(off, 1*1500);
                }
        };
}

var MAX_FILE_SIZE=5048576;
function fileUpload(event){

    //notify user about the file upload status
    $("#dropBox").html(event.target.value+" uploading...");

    //get selected file
    files = event.target.files;

    //form data check the above bullet for what it is
    var data = new FormData();

    //file data is presented as an array
    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        if(!file.type.match('image.*')) {
            //check file type
            $("#dropBox").html("Please choose an images file.");
        }else if(file.size >  MAX_FILE_SIZE){
            //check file size (in bytes)
            $("#dropBox").html("Sorry, your file is too large (>1 MB)");
        }else{

            //append the uploadable file to FormData object
            data.append('file', file, file.name);
                        appendFormData(data);
            //create a new XMLHttpRequest
            var xhr = new XMLHttpRequest();

            //post file data for upload
            xhr.open('POST', ja_g_url, true);
            xhr.send(data);
            xhr.onload = function () {
                //get response and show the uploading status
                var response = JSON.parse(xhr.responseText);
                if(xhr.status === 200 && response.status == 1){
                    $("#dropBox").html("File has been uploaded successfully. Click to upload another.");
                                                                        single_image_uploaded_event(response);
                                                                                btnClick();
                }else if(response.status == 'type_err'){
                    $("#dropBox").html("Please choose an images file. Click to upload another.");
                }else{
                    $("#dropBox").html("Some problem occured, please try again.");
                }
            };
        }
    }
}

function getValId(id){
        var a;
        if ((a = document.getElementById(id)) != null)
                return a.value;
        else
                return null;
}
var contactId="";


function progress()
{

        var a = stack[0];
        if (a.length <= 0 )
                return;
        if (document.getElementById(a['id']).innerHTML == "....")
                document.getElementById(a['id']).innerHTML = ".";
        else
                document.getElementById(a['id']).innerHTML = document.getElementById(a.id).innerHTML+ ".";
}


function exchangeContactDetails(v)
{

        var a = new Object();


        if (contactId == 'overlay_contact_id')
        {
                var txt = "<div style='width:300px;font-size:20px;background-color:white;color:black;text-align:center;'>"+ja_g_id_stack[v.stack_content_id]+"</div>";
                document.getElementById('overlaytext').classList.add('aligncenter');
                document.getElementById('overlaytext').innerHTML = txt;
                on(false,"color:black;");
        }

        a['function'] = progress;
        a['id'] = contactId;

        var t = window.setInterval(progress, 0.5*1000);
        a['timerId'] = t;


        stack.push(a);


        document.getElementById(contactId).innerHTML = ".";

        $.ajax({type:"GET",url: "assoc.php",data:{action:'ECD',i:JSON.stringify(v)}, success: function(data, status, xhttp) {
        if (data)
        {
                var v = JSON.parse(data);
                if (contactId == 'overlay_contact_id')
                {
                        document.getElementById(contactId).innerHTML = "<div style='font-size:15px;'>Above details and your contact info is shared with Vendor. Following are vendor details, you can contact them directly. </div><div><a span='width:100%;' href='tel:"+v.p+"'><img src='./images/phone_small.png'>&nbsp;"+v.p+"</a></div><div style='font-size:10px;text-align:cneter;'>Click to call</div>";
                }else
                {
                        document.getElementById(contactId).innerHTML = "<div><a span='width:100%;' href='tel:"+v.p+"'><img src='./images/phone_small.png'>&nbsp;"+v.p+"</a></div><div style='font-size:10px;text-align:cneter;'>Click to call</div>";

                }
                var a = stack.pop();
                document.getElementById(contactId).onclick='';
                window.clearTimeout(a['timerId']);
                //off();
        }
        }});

}

function appendFormData(data)
{
        if (!di('user_id'))
                return;

        data.append('contributor_id',di('user_id').value);
        data.append('contributor_source', "www.tripmantu.com");
}


function single_image_uploaded_event(res)
{
                        di('DropboxForm').setAttribute('method','POST');
                        di('uploadedImageDisplay').innerHTML = "<img style='width:100%;height:100%;object-fit:cover;' src='"+res.filename+"'>";
                        di('uploadedImageDisplay').setAttribute('url',res.filename)
                        di('DropBoxFiles').innerHTML = "";
                        di('dropBox').setAttribute('style', 'display:none;');

}

function insertAfter(newNode, referenceNode)
{
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}


                function uploadimage(url,poid,uid,latlng="")
                {

                var formElement = "<form><input type=hidden id=user_id name=user_id value="+uid+">" +
                                                " <input type=hidden id=place_pois_id name=place_pois_id  value="+oid+"> ";


                                var txt  =  "<div class='raCorosolContainer crop' style='height:auto !important;font-size:20px;'><div style='background-color:orange;color:white;'>Add Activity</div></div>";
                                activateDropBox(url);
                }



function di(i)
{
        return document.getElementById(i);
}


                function log(...args)
                {
                        //return;
                        console.log(...args);
                }
