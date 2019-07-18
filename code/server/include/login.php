<?php

// Login by session
if (!empty($_GET['session'])) {
    $session = $_GET['session'];
    $users = Database::getInstance()->get('users', ['session', '=', $session]);

    if ($users->count() !== 1) {
        respond('session_missing');
    } else {
        $user = cleanUser($users->first());
        $response['user'] = $user;
        respond();
    }
}

// Login by identifier/password
$identifier = !empty($_GET['email']) ? 'email' : 'username';
$id_val = trim($_GET[$identifier]);
$password = trim($_GET['password']);
$users = Database::getInstance()->get('users', [$identifier, '=', $id_val]);

if ($users->count() !== 1) {
    respond('user_missing');
} else if (!password_verify($password, $users->first()->password)) {
    respond('password_incorrect');
}

$session = md5(uniqid(rand(), true));
Database::getInstance()->update('users', $users->first()->id, ['session' => $session]);
$user = Database::getInstance()->get('users', [$identifier, '=', $id_val])->first();
$response['user'] = cleanUser($user);
respond();