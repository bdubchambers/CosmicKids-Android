<?php
  require "remote_config.inc.php";

  mysql_connect($host, $username, $password) OR die("Cannot connect!");
  mysql_select_db($dbname);

  $query = 'SELECT * FROM ' . $usertable;
  $result = mysql_query($query);
  if($result) {
    while($row = mysql_fetch_array($result)){
        print $name = $row[$yourfield];
        echo 'Name: ' . $name;
    }
  }
  else {
    print "Database NOT Found ";
    mysql_close($db_handle);
  }
?>
