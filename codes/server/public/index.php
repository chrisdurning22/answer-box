<?php

require '../include/init.php';

$response = [];
ini_set('display_errors', 'On');
error_reporting( E_ALL | E_STRICT );

$action = !empty($_GET['action']) ? $_GET['action'] : null;

// We explicitly define possible actions to prevent LFI and by extension, log poisoning etc.
switch ($action) {
    case 'login':
        require '../include/login.php';
        break;

    case 'logout':
        require '../include/logout.php';
        break;

    case 'register':
        require '../include/register.php';
        break;

    case 'password':
        require '../include/password.php';
        break;

    case 'browse':
        require '../include/browse.php';
        break;

    case 'uploadFile':
        require '../include/uploadFile.php';
        break;

    case 'downloadFile':
        require '../include/downloadFile.php';
        break;

    case 'submitAnswer':
        require '../include/submitAnswer.php';
        break;

    case 'submitComment':
        require '../include/submitComment.php';
        break;
		
	case 'getComment':
		require '../include/getComment.php';
		break;

    case 'deleteComment':
        require '../include/deleteComment.php';
        break;

    case 'reputation':
        require '../include/reputation.php';
        break;

    case 'report':
        require '../include/report.php';
        break;

    default:
        respond('bad_request');
}
