<?php
  require "config.inc.php";//includes our login credentials
  #check if 'posted data' is NOT empty
  if(!empty($_POST)) {
    #if form is submitted w/empty fields, form dies
    if(empty($_POST['username']) || empty($_POST['password'])) {
      #json
      $response["success"] = 0;
      $response["message"] = "Enter a username and password.";
      #die prevents code after from executing
      die(json_encode($response));
    }
    /*check entered username against our db, :user is a blank variable
    that will be changed on line 17, prior to query exectution.
    this adds security against SQL injections*/
    $query = "SELECT 1 FROM users WHERE username = :user";
    $query_params = array(':user' => $_POST['username'] );

    #run query
    try {
      $stmt = $db->prepare($query);
      $result = $stmt->execute($query_params);
    } catch (PDOException $ex) {
      $response["success"] = 0;
      $response["message"] = "Database Error (1). Try again.";
      die(json_encode($response));
    }
    #fetch returns an array of data if username already exists in db
    $row = $stmt->fetch();
    if($row) {
      $response["success"] = 0;
      $response["message"] = "I'm sorry, this username is already in use";
      die(json_encode($response));
    }
	    /*Setup query to create the new user.
	    Protection against SQL injections with tokens :user and :pass*/
	    $query =
        "INSERT INTO users ( username, password ) VALUES ( :user, :pass ) ";
	    #Now update our tokens with the real data:
	    $query_params = array(
	        ':user' => $_POST['username'],
	        ':pass' => $_POST['password']
	    );
	    #Run query, create the user
	    try {
	        $stmt   = $db->prepare($query);
	        $result = $stmt->execute($query_params);
	    }
	    catch (PDOException $ex) {
	        $response["success"] = 0;
	        $response["message"] = "Database Error (2). Please Try Again!";
	        die(json_encode($response));
	    }
	    /*We have successfully added a new user to database.
      Now we can redirect to the login page, but here we echo out some
      json data that will be read by the Android application, which will
      login the user or redirect to a different activity.*/
	    $response["success"] = 1;
	    $response["message"] = "Username Successfully Added!";
	    echo json_encode($response);
	    //for a php webservice you could do a simple redirect and die.
	    //header("Location: login.php");
	    //die("Redirecting to login.php");

  }else{ //close out php to use html
    ?>
    <h1>Register</h1>
    <form action="register.php" method="post">
      Username: <br />
      <input type="text" name="username" placeholder="user name"><br />
      Password: <br />
      <input type="password" name="password" placeholder="password"><br />
      <input type="submit" value="Register user"/>
    </form>
    <?php //reopen php to close file
  }
?>
