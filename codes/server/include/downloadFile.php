<?php

if (!isset($_GET['id'])) {
    respond('bad_request');
}

checkSession();

//TODO: Whitelist extensions

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();
$id = $_GET['id'];
$file = Database::getInstance()->get('files', ['id', '=', $id])->first();
$extension = $file->extension;
$fileName = $file->hash . '.' . $file->extension;

$file_contents = file_get_contents('../uploads/' . $fileName);
$file_contents = base64_encode($file_contents);

$file->data = $file_contents;
$response['file'] = $file;
respond();
