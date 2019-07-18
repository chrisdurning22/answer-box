<?php

if (!isset($_POST['title']) || !isset($_POST['body']) || !isset($_POST['subject']) || !isset($_POST['year']) || !isset($_POST['level'])) {
    respond('bad_request');
}

checkSession();

$userId = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first()->id;

$title = trim($_POST['title']);
$body = trim($_POST['body']);
$subject = trim($_POST['subject']);
$year = trim($_POST['year']);
$level = trim($_POST['level']);

$subjectId = Database::getInstance()->get('subjects', ['name', '=', $subject])->first()->id;

Database::getInstance()->insert('solutions', [
    'user_id'       => $userId,
    'title'         => $title,
    'content'       => $body,
    'subject_id'    => $subjectId,
    'year'          => $year,
    'level'         => $level
]);

$sql = 'SELECT * FROM solutions
        WHERE user_id = ? AND title = ?';

$response['answer'] = Database::getInstance()->query($sql, [$userId, $title])->first();

respond();
