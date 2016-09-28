<?php
require_once 'C:\Users\Andre\Desktop\Andrei\Projects\ECSE321\EventRegistrationApplication\workspace-web\EventRegistration\EventRegistration\controller\InputValidator.php';
require_once 'C:\Users\Andre\Desktop\Andrei\Projects\ECSE321\EventRegistrationApplication\workspace-web\EventRegistration\EventRegistration\persistence\PersistenceEventRegistration.php';
require_once 'C:\Users\Andre\Desktop\Andrei\Projects\ECSE321\EventRegistrationApplication\workspace-web\EventRegistration\EventRegistration\model\RegistrationManager.php';
require_once 'C:\Users\Andre\Desktop\Andrei\Projects\ECSE321\EventRegistrationApplication\workspace-web\EventRegistration\EventRegistration\model\Participant.php';
require_once 'C:\Users\Andre\Desktop\Andrei\Projects\ECSE321\EventRegistrationApplication\workspace-web\EventRegistration\EventRegistration\model\Event.php';

class Controller{
	public function __construct(){
	}
	public function createParticipant($participant_name){
		//1. Validate input
		$name = InputValidator::validate_input($participant_name);
		if ($name == null || strlen($name)==0){
			throw new Exception("Participant name cannot be empty!");
		} else {
			//2. Load all the data
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();
			
			//3. Add the new participant
			$participant = new Participant($name);
			$rm->addParticipant($participant);
			
			//4. Write the data
			$pm->writeDatatoStore($rm);
		}
	}
	public function createEvent($event_name, $event_date, $starttime, $endtime){	
		//1. Validate input text
		$eventname = InputValidator::validate_input($event_name);
		//2. Validate all input
		$error="";
		if ($eventname == null || strlen($eventname)==0){
			$error="@1Event name cannot be empty! ";
		} else if (strtotime($event_date)==false){
			$error="@2Event date must be specified correctly (YYYY-MM-DD)! ";
		} else if (strtotime($starttime)==false){
			$error="@3Event start time must be specified correctly (HH:MM)! ";
		}else if (strtotime($starttime==false)){
			$error="@4Event end time must be specified correctly (HH:MM)!";
		}
		//Throw exception if error occurred
		if (strlen($error)>0){ //Means our "error" variable contains an error message
			throw new Exception(trim($error));
		}  else { 
			//Load values
			$pm = new PersistenceEventRegistration();
			$rm = $pm->loadDataFromStore();
			
			//Add event
			$event = new Event($eventname, $event_date, $starttime, $endtime);
			$rm->addEvent($event);
		}
	}
	public function register($aParticipant, $aEvent){
		//1. Load data
		$pm = new PersistenceEventRegistration();
		$rm = $pm->loadDataFromStore();
		
		//2. Find participant
		$myparticipant = NULL;
		foreach($rm->getParticipants() as $participant){
			if (strcmp($participant->getName(), $aParticipant) == 0){
				$myparticipant = $participant;
				break;
			}
		}
		//3. Find event
		$myevent = NULL;
		foreach($rm->getEvents() as $event){
			if (strcmp($event->getName(), $aEvent)==0){
				$myevent=$event;
				break;
			}
		}
		//4. Register
		$error="";
		if ($myparticipant != NULL && $myevent != NULL){
			$myregistration=new Registration($myparticipant,$myevent);
			$rm->addRegistration($myregistration);
			$pm->writeDataToStore($rm);		
		} else {
			if ($myparticipant == NULL){
				$error .= "@1Participant ";
				if ($aParticipant!=NULL){
					$error .= $aParticipant;
				}
				$error .= " not found! ";
			}
			if ($myevent==NULL){
				$error .="@2Event ";
				if ($aEvent != NULL){
					$error .= $aEvent;
				}
				$error .= " not found!";
			}
			throw new Exception(trim($error));
		}
	}	
}
?>