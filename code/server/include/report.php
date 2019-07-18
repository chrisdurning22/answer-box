<?php

if (!isset($_GET['solution'])) {
    respond('bad_request');
}

Auth::checkSession();
$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();

$solutionID = $_GET['solution'];

Database::getInstance()->insert('reports', [
    'user_id'       => $user->id,
    'solution_id'   => $solutionID
]);

respond();