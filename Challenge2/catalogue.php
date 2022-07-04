<?PHP
require_once("config.php");

  global $g_hostIP;;
  global $g_passWord;
  global $g_dbName;
  global $g_userName;



$link = mysqli_connect($g_hostIP, $g_userName, $g_passWord) or die("Could not connect: " . mysqli_error());
mysqli_select_db($link,$g_dbName) or die("Could not select database");

$query = "select * from catalogue order by oid_index desc";
$result=mysqli_query($link, $query);
?>
<html>
<body>
<?PHP

$total_rows="";
if ($result->num_rows > 0)
{
    while($row = $result->fetch_assoc())
    {
        $str="<tr><td><img style='width:200px;height:auto;' src='".$row['img_url']."'></td><td>".$row['name']."</td><td>".$row['units']."</td><td>".$row["price"]."</td><td>".$row['description']."</td><td>".$row['entry_time']."</td></tr>";
        $total_rows .= $str;
    }
}
mysqli_close($link);
?>
<table>
    <?PHP echo $total_rows;?>
</table>
</body>
</html>
