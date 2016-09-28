<?php
require_once "controller/Controller.php";
session_start();
$c = new Controller();
try{
	$c->createEvent($_POST['event_name'], $_POST['event_date'], $_POST['starttime'], $_POST['endtime']);
} catch (Exception $e){
	$errors=explode("@", $e->getMessage());
	foreach($errors as $error){
		if (substr($error,0,1)==1){
			$_SESSION['errorEventName']=substr($error,1);
		} else if (substr($error,0,1)==2){
			$_SESSION['errorEventDate']=substr($error,1);
		} else if (substr($error,0,1)==3){
			$_SESSION['errorEventstarttime']=substr($error,1);
		} else if (substr($error,0,1)==4){
			$_SESSION['errorEventendtime']=substr($error,1);
		}
	}
}
?>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="refresh" content="0; url=/EventRegistration/" />
	</head>
</html>