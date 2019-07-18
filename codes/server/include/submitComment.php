<?php

if (!isset($_POST['body']) || !isset($_POST['solution'])) {
    respond('bad_request');
}

checkSession();

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();
$body = trim($_POST['body']);
$solutionId = $_POST['solution'];

Database::getInstance()->insert('comments', [
    'user_id'       => $user->id,
    'content'       => $body,
    'solution_id'   => $solutionId
]);

$sql = 'SELECT *
        FROM comments
        WHERE user_id = ? AND content = ?
        ORDER BY id DESC';

$response['comment'] = cleanComment(Database::getInstance()->query($sql, [$user->id, $body])->first());
$response['comment']->user = cleanUser($user, true);

respond();
