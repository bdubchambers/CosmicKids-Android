<?php
	
	require 'remote_config.inc.php';

	if(!empty($_POST)) {
		$query = "INSERT INTO scores ( username, gamename, score )
				  VALUES ( :user, :game, :score )";
		$query_params = array(':user' => $_POST['username'],
                          ':game' => $_POST['gamename'],
                          ':score' => $_POST['score']);

		#Execute mysql query
		try {
			$stmt   = $db->prepare($query);
			$result = $stmt->execute($query_params);
		} catch (PDOException $ex) {
			#Show json message on failure
			$response["success"] = 0;
			$response["message"] = "Database Error (1). Can't add score.";
			die(json_encode($response));
		}

		$response["success"] = 1;
		$response["message"] = "Score successfully saved! Go to scores.php.";
		echo json_encode($response);
	} else {
?> <!-- Close php to use html -->
		<h1>Add Score</h1>
		<form action="addscore.php" method="post">
			Username:<br />
			<input type="text" name="username" placeholder="username" />
			<br /><br />
			Gamename:<br />
			<input type="text" name="gamename" placeholder="name of game" />
			<br /><br />
			Score:<br />
			<input type="text" name="score" placeholder="post score" />
			<br /><br />
			<input type="submit" value="Add Score" />
		</form>
<?php
	}
?>
