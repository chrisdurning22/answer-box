<?php

if (!isset($_GET['reputation']) || empty($_GET['solution'])) {
    respond('bad_request');
}

checkSession();

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();
$reputation = $_GET['reputation'];
$solutionId = $_GET['solution'];

if ($reputation > 0) {
    $reputation = 1;
} else if ($reputation < 0) {
    $reputation = -1;
} else {
    $reputation = 0;
}

$sql = 'SELECT *
        FROM votes
        WHERE user_id = ? AND solution_id = ?';

$votes = Database::getInstance()->query($sql, [$user->id, $solutionId]);

if ($votes->count() > 0) {
    Database::getInstance()->update('votes', $votes->first()->id, ['value' => $reputation]);
} else {
    Database::getInstance()->insert('votes', [
        'user_id'       => $user->id,
        'solution_id'   => $solutionId
    ]);
}

$sql = 'SELECT COALESCE(sum(value), 0) AS value
        FROM votes
        WHERE solution_id = ?';
		
respond();