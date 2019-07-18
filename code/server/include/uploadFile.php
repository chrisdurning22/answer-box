<?php

if (!isset($_POST['solution']) || !isset($_POST['extension']) || !isset($_POST['file'])) {
    respond('bad_request');
}

checkSession();

//TODO: Whitelist extensions

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();
$solution = $_POST['solution'];
$extension = $_POST['extension'];
$file_contents = base64_decode($_POST['file']);
$hash = md5(uniqid(rand(), true));

file_put_contents("../uploads/" . $hash . '.' . $extension, $file_contents);

Database::getInstance()->insert('files', [
    'user_id'   => $user->id,
    'hash'      => $hash,
    'extension' => $extension
]);

$file = Database::getInstance()->get('files', ['hash', '=', $hash])->first();
$response['file'] = $file;

Database::getInstance()->insert('solutions_files', [
    'solution_id'   => $solution,
    'file_id'       => $file->id
]);

respond();
