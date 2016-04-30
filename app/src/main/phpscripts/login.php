<?php
  require "config.inc.php";//includes our login credentials

  if(!empty($_POST)) {
    #retrieve user data on the provided username
    $query = "SELECT id, username, password
              FROM users
              WHERE username = :username";
    $query_params = array( ':username' => $_POST['username'] );

    try {
      $stmt   = $db->prepare($query);
      $result = $stmt->execute($query_params);
    } catch (PDOException $ex) {
      #Show json message on failure
      $response["success"] = 0;
      $response["message"] = "Database Error (1). Try again.";
      die(json_encode($response));
    }

    #Flag for whether user's input is valid
    $validated_info = false;
    $login_ok = false;

    #Check if user's entered password is correct
    $row = $stmt->fetch();
    if($row) {
      #TODO: Add un-encryption right here, prior to comparison
      if($_POST['password'] === $row['password']) {
        $login_ok = true;
      }
    }

    if($login_ok) {
      $response["success"] = 1;
      $response["message"] = "Login successful.";
      die(json_encode($response));
    }
    else {
      $response["success"] = 0;
      $response["message"] = "Invalid credentials.";
      die(json_encode($response));
    }
  }
  else {
?> <!-- Close out php to start html -->
    <h1>Login</h1>
    <form action="login.php" method="post">
      Username:<br />
      <input type="text" name="username" placeholder="username" />
      <br /><br />
      Password:<br />
      <input type="password" name="password" placeholder="password" value="" />
      <br /><br />
      <input type="submit" value="Login" />
    </form>
    <a href="register.php">Register</a>
  <?php
  }
?>
