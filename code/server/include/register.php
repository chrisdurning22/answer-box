<?php

$username = trim($_GET['username']);
$email = trim($_GET['email']);
$password = password_hash($_GET['password'], PASSWORD_BCRYPT);

$users = Database::getInstance()->get('users', ['username', '=', $username])->count();
$emails = Database::getInstance()->get('users', ['email', '=', $email])->count();

if ($users > 0) {
    respond('username_taken');
} else if ($emails > 0) {
    respond('email_exists');
} else {
    Database::getInstance()->insert('users', [
        'username'  => $username,
        'email'     => $email,
        'password'  => $password
    ]);
    $user = Database::getInstance()->get('users', ['email', '=', $email])->first();
    $response['user'] = cleanUser($user);
    respond();
}
