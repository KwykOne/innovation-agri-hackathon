<?PHP
header('Access-Control-Allow-Origin: *');
?><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Speech Recording</title>

        <style>

#fileInput {
    display: none;
}

.olContainer {
    position: relative;
    text-align: center;
    color: black;
}

.raCorosolContainer {
    background-color: white;
    color: orange;
    font-size: 20px;
}

#dropBox {
    min-height: 50px;
    padding: 5px 5px;
    box-sizing: border-box;
}
#dropBox {
    border: 3px dashed #0087F7;
    border-radius: 5px;
    background: #F3F4F5;
    cursor: pointer;
}


#dropBox p {
    text-align: center;
    margin: 2em 0;
    font-size: 16px;
    font-weight: bold;
}
        </style>
</head>
<script src="./cm.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css">
<body style='width:100%;height:100%;'>
  <div style='width:80%;margin:auto;border-width: 10px;text-align: center;border-color: black;padding: 20px;background-color: #eaf7ea;'>
        <div>

                <div id="olContainer"><div class='col-sm-12 col-md-6' id="uploadedImageDisplay" style='margin:auto'></div>
                        <form id="DropboxForm" name="DropboxForm"><div id="dropBox"><p>Select file to upload</p></div>
                                <div id="DropBoxFiles">
                                        <input type="file" name="fileInput" id="fileInput">
                                </div>
                        </form>
                </div>
        </div>
        <div id=t1>
        </div>
  </div>
  <div style='width:80%;margin:auto;'>
  <div id=tokenstring></div>
  <div id=errorMsg></div>
  <div style1='display:none;' id=tokens data-string="" style='display1:none;'>

        </div>
        <div id=container>
                <table cellspacing=10px font-size:10px;>
                        <tr>
                                <td>Name</td>
                                <td><input autocomplete="off" id=name class=attribs onclick=recvSpeech('name');></td>
                        </tr>
                        <tr>
                                <td>Price</td>
                                <td><input autocomplete="off" id=price class=attribs onclick=recvSpeech('price');></td>
                        </tr>
                        <tr>
                                <td>Units</td>
                                <td><input id=unit class=attribs onclick=recvSpeech('unit');></td>
                        </tr>
                        <tr>
                                <td>Quantity</td>
                                <td><input id=quantity class=attribs onclick=recvSpeech('quantity');></td>
                        </tr>
                        <tr>
                                <td>Description</td>
                                <td><input id=description class=attribs onclick=recvSpeech('description');></td>
                        </tr>
                </table>
        </div>
  </div>

  <div onclick=saverecord() style='bottom:-100px;padding:20px;font-size:30px;text-align:center;width:88%;margin:auto;'>
  Save
  </div>


  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
  <script>
        var itemEntryIndex=0

    window.SpeechRecognition = window.webkitSpeechRecognition || window.SpeechRecognition;
    let finalTranscript = '';
    let recognition = new window.SpeechRecognition();

    recognition.interimResults = true;
    recognition.maxAlternatives = 10;
                recognition.lang = 'hi-IN';
    recognition.continuous = false; // For mobile keep this false as auto closure of speech happens within 4 seconds

    recognition.onresult = (event) => {
      let interimTranscript = '';
      for (let i = event.resultIndex, len = event.results.length; i < len; i++) {
        let transcript = event.results[i][0].transcript;
        if (event.results[i].isFinal) {
          finalTranscript += " " + transcript;
                                var l = finalTranscript.split(' ')
                                                console.log(transcript)
                                        console.log(finalTranscript);
                                        di(activeId).value=transcript
                                //tokenize(l)
        } else {
          interimTranscript += transcript;
        }
      }

      //document.querySelector('body').innerHTML = finalTranscript + '<i style="color:#ddd;">' + interimTranscript + '</>';
          document.getElementById('t1').innerHTML = finalTranscript + '<i style="color:#ddd;">' + interimTranscript + '</> ';
           //var l = finalTranscript.split(' ')
                 //console.log(interimTranscript);
           //tokenize(l)
    }

        recognition.onspeechstart= function() {
                st.push("Speech Started");
        }
        recognition.onend= function() {
                st.push("Disconnected");
        }



        recognition.onspeechend = function() {
                st.push("Speech Ended");
                //recognition.stop();
        }


        recognition.onerror = function(event) {
                st.push('Error occurred in recognition: ' + event.error);
        }

  var activeId=''
  function recvSpeech(id)
  {
                  activeId=id
                        recognition.start();
  }


  class sta{
                constructor(size,divid)
                {
                        this.maxSize=size
                        this.s=di(divid);
                }
                pop()
                {
                        if (this.s.children.length)
                        {
                                var el=this.s;
                                el.removeChild(el.children[0])
                        }
                }
                push(text)
                {
                        if (this.s.children.length > this.maxSize)
                                this.pop()
                        var el=document.createElement('div');
                        el.style='font-size:10px;';
                        el.innerHTML = text;
                        this.s.appendChild(el);
                }
        }

  var st=new sta(2,'errorMsg');


  recognition.nomatch = function(event) {
                stack.pop()
                stack.push("No match")
        }


        function tokenize(l)
        {
                if (!l.length)
                        return

                var tokenCount=di('tokens').getAttribute('data-string').split(' ').length-1
                console.log(tokenCount)
                addToken(l,tokenCount)
        }


        function addToken(l,tc)
        {
                let newToken=""

                for(let i=tc;i<=l.length-1;i++)
                {

                        if (l[i] == "डॉट")
                        {
                                updateToken(newToken)
                                newToken="";
                                if ( i>=l.length )
                                        return
                        }
                        newToken += l[i]
                        if (i!=l.length-1)
                                newToken += " "
                }
                updateToken(newToken)

        }

        function updateToken(newToken)
        {


                if (newToken=="")
                {
                        console.log("returning")
                        return
                }

                st.push(newToken)
                di('tokens').setAttribute('data-string', di('tokens').getAttribute('data-string') + " " + newToken)
                var n = document.createElement('div')
                n.innerHTML= newToken;
                di('tokens').append(n)



                attribs=document.getElementsByClassName('attribs')
                attribs[itemEntryIndex++].value = newToken

        }

        function saverecord()
        {
                var jobj = new Object();
                var a=document.getElementsByClassName('attribs');
                for(i=0;i<a.length;i++)
                {
                        jobj[a[i].getAttribute('id')]=a[i].value;
                }
                jobj['img_url'] = di('uploadedImageDisplay').getAttribute('url');
                str=JSON.stringify(jobj)
                $.ajax({type:"POST",url: "save_record",data:{action:'save_record',record:jobj}, success: function(data, status, xhttp){
                        console.log(data)
                        location.reload(true);
                }});

        }

function clearform()
{
}


        function di(id)
        {
                return document.getElementById(id);
        }


        activateDropBox("./upload_image");
</script>



</body>
</html>
