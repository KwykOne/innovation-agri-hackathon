<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);

require_once("config.php");
        global $g_hostIP;;
        global $g_passWord;
        global $g_dbName;
        global $g_userName;

//print_r($_POST);

$link = mysqli_connect($g_hostIP, $g_userName, $g_passWord) or die("Could not connect: " . mysqli_error());
mysqli_select_db($link,$g_dbName) or die("Could not select database");

$record=$_POST['record'];

if ($record != "")
{

$query = "insert into catalogue set name='".$record['name']."',units='".$record['unit']."',price='".$record["price"]."',description='".$record["description"]."',img_url='".$record['img_url']."'";
mysqli_query($link, $query);
//echo $query;
}

mysqli_close($link)
?>
