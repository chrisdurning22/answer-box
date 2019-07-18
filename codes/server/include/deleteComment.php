<?php

if (!isset($_GET['id'])) {
    respond('bad_request');
}

checkSession();

$id = $_GET['id'];

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();

$comment = Database::getInstance()->get('comments', ['id', '=', $id])->first();

if ($comment->user_id === $user->id) {
    $del = Database::getInstance()->delete('comments', ['id', '=', $id]);
} else {
    respond('unauthorized');
}

respond();
