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

  #Retrieve all existing score rows from db
  $rows = $stmt->fetchAll();

  if($rows) {
    $response["success"] = 1;
    $response["message"] = "Score Available!";
    $response["scores"]   = array();

    foreach ($rows as $row) {
      $post = array();
      $post["entry_id"] = $row["entry_id"];
      $post["username"] = $row["username"];
      $post["gamename"] = $row["gamename"];
      $post["score"] = $row["score"];
      #add to json data
      array_push($response["scores"], $post);
    }

    echo json_encode($response);
  }
  else {
    $response["success"] = 0;
    $response["message"] = "No scores available.";
    die(json_encode($response));
  }

?>
