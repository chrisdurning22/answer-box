<?php

if (!isset($_GET['session']) || !isset($_GET['old']) || !isset($_GET['new'])) {
    respond('bad_request');
}

checkSession();

$session = $_GET['session'];
$password = trim($_GET['old']);
$newPass = password_hash(trim($_GET['new']), PASSWORD_BCRYPT);

$users = Database::getInstance()->get('users', ['session', '=', $session]);
$user = $users->first();

if ($users->count() !== 1) {
    respond('user_missing');
} else if (!password_verify($password, $user->password)) {
    respond('password_incorrect');
} else {
    if (Database::getInstance()->update('users', $user->id, ['password' => $newPass])) {
        respond('error_unknown');
    } else {
        $user = Database::getInstance()->get('users', ['session', '=', $session])->first();
        respond();
    }
}
