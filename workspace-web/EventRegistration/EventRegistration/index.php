<!DOCTYPE html>
<html>
	<head> 
		<meta charset="UTF-8">
		<title> Event Registration </title>
		<style>
			.error {color:#FF0000;}
		</style>
	</head>
	<body>
		<?php 
			require_once "model/Participant.php";
			session_start();
		?>
		<h1> Add Participant </h1>
		<form action = "addparticipant.php" method="post"> <!--Insert the php file behind this-->
			<p>Name? <input type="text" name="participant_name" />
			<span class="error">
			<?php
			if (isset($_SESSION['errorParticipantName']) && !empty($_SESSION['errorParticipantName'])){
				echo " * " . $_SESSION["errorParticipantName"];
			}
			?>
			</span></p>	
			<p><input type="submit" value="Add Participant"/></p>
		</form>

		<h1>Add Event</h1>
		<form action = ""> <!--Insert the php file behind this-->
			<fieldset>
				<legend>Event:</legend>
				Event Name:<br>
				<input type="text" name="event_name" placeholder="Enter event name here"/><br>
				Event Date and time:<br>
				<input type="date" name="event_date" value="<?php echo date('Y-m-d'); ?>" /><br>
				Date :<br> <!--Add better way to select the date-->
				<input type="time" name="starttime" value="<?php echo date('H:i'); ?>" /> <br>
				<input type="time" name="endtime" value="<?php echo date('H:i'); ?>" /> <br>
				<!-- Also add start/end times-->
				<input type="submit" value="submit">	
			</fieldset>
		</form>

		<h1>Register</h1>
		<form action = ""> <!--Insert the php file behind this-->
			<fieldset>
				<legend>Register :</legend>
				Select Participant: <br>
				<!--Select participant/event has to be a drop-down not a text field -->
				<input type="text" name="notHandledLikeThis" placeholder="Event name"><br>
				Select Event: <br> <!--Add better way to select the date-->
				<input type="text" name="notHandledLikeThis" placeholder="Date of your event"><br>
				<input type="submit" value="submit">	
			</fieldset>
		</form>
	
	</body>
</html>

