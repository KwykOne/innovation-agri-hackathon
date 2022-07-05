<?PHP
require_once("config.php");

  global $g_hostIP;;
  global $g_passWord;
  global $g_dbName;
  global $g_userName;



                global $g_root;
                global $g_image_path;

                $id=1;
                $img_dir = $g_image_path.$id;
                $path =  $g_root."/images/";

                //print_r($_FILES);

                foreach($_FILES as $file_id => $v)
                {
                                $o = file_upload_assoc($id, $file_id, $img_dir, $path);
                }
                echo json_encode($o);



        function file_upload_assoc($vendor_id, $img_id, $img_dir, $img_path)
        {

                $output['status']=0;
//              $img_dir = "D:/wamp/www/travelneeds/packages/".$vendor_id;
//              $g_packagePath = "http://27.0.0.1/travelneeds/packages/";

                $dir_path = $img_dir;

                //$dir_path = $g_packagePath;
                if (!(is_dir($dir_path)))
                {
                        mkdir($dir_path);
                }
                /*
                print_r($_GET);
                print_r($_POST);
                print_r($_FILES);
*/

                if($_SERVER["REQUEST_METHOD"] == "POST")
                {
                        if(isset($_FILES[$img_id]) && $_FILES[$img_id]["error"] == 0){
                                $allowed = array("jpg" => "image/jpg", "jpeg" => "image/jpeg", "gif" => "image/gif", "png" => "image/png", "PNG" => "image/PNG", 
"JPG" => "image/JPG", "JPEG"=>"image/JPEG");
                                $filename = $_FILES[$img_id]["name"];
                                $filetype = $_FILES[$img_id]["type"];
                                $filesize = $_FILES[$img_id]["size"];

                                $ext = pathinfo($filename, PATHINFO_EXTENSION);
                                if(!array_key_exists($ext, $allowed)) die("Error: Please select a valid file format.");

                                $maxsize = 5 * 1024 * 1024;
                                if($filesize > $maxsize) die("Error3: File size is larger than the allowed limit.");

                                if(in_array($filetype, $allowed)){
                                                $file_name = $_FILES[$img_id]["name"];
                                                move_uploaded_file($_FILES[$img_id]["tmp_name"], $img_dir ."/". $_FILES[$img_id]["name"]);

                                                $output['filename']=$img_path.$vendor_id."/".$file_name;
                                                $output['status'] = 1;
                        //                      echo "Uploaded successfully";
                                } else{
                                        $output['error']= "Error:2 There was a problem uploading your file. Please try again.";
                                }

                        } else{
                                        $output['error'] = "Error:1 (" . $_FILES[$img_id]["error"].")(".$_FILES[$img_id].")";
                        }
                }

        //      print_r($output);
                return $output;
        }


?>
