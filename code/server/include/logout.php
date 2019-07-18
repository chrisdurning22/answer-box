<?php

// Login by session
if (!empty($_GET['session'])) {
    $session = $_GET['session'];
    $users = Database::getInstance()->get('users', ['session', '=', $session]);

    if ($users->count() !== 1) {
        respond('not_logged_in');
    } else {
        $sql = 'UPDATE users SET session = "" WHERE session = ?';
        Database::getInstance()->query($sql, [$session]);
        respond();
    }
} else {
    respond('bad_request');
}
