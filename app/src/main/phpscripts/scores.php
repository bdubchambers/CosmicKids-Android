<?php
  require 'config.inc.php';

  $query = "SELECT * FROM scores";
  
  #execute query
  try {
    $stmt   = $db->prepare($query);
    $result = $stmt->execute($query_params);
  } catch (PDOException $ex) {
    $response["success"] = 0;
    $response["message"] = "Database Error (1). Try again.";
    die(json_encode($response));
  }

  #Retrieve all existing comment rows from db
  $rows = $stmt->fetchAll();

  if($rows) {
    $response["success"] = 1;
    $response["message"] = "Post Available!";
    $response["posts"]   = array();

    foreach ($rows as $row) {
      $post = array();
      $post["post_id"] = $row["post_id"];
      $post["username"] = $row["username"];
      $post["title"] = $row["title"];
      $post["message"] = $row["message"];
      #add to json data
      array_push($response["posts"], $post);
    }

    echo json_encode($response);
  }
  else {
    $response["success"] = 0;
    $response["message"] = "No posts available.";
    die(json_encode($response));
  }

?>
