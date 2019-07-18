<?php

function checkSession() {
    if (!empty($_GET['session'])) {
        $session = $_GET['session'];
        $users = Database::getInstance()->get('users', ['session', '=', $session]);

        if ($users->count() !== 1) {
            respond('session_missing');
        }
    } else {
        respond('session_missing');
    }
}

function respond($error = null) {
    global $response;

    if (is_null($error)) {
        $response['success'] = true;
    } else {
        $response['success'] = false;
        $response['error'] = $error;
    }
    die(json_encode($response));
}

function cleanComment($comment) {
    $comment->id = intval($comment->id);
    $comment->user_id = intval($comment->user_id);
    $comment->solution_id = intval($comment->solution_id);

    return $comment;
}

function cleanUser($user, $removeSession = false) {
    $user->id = intval($user->id);
    $user->reputation = intval($user->reputation);
    unset($user->password);
    if ($removeSession) {
        unset($user->session);
    }

    return $user;
}

function cleanVote($vote) {
    $vote->value = intval($vote->value);

    return $vote;
}

function cleanSolution($solution) {
    $solution->id = intval($solution->id);
    $solution->user_id = intval($solution->user_id);
    $solution->reputation = intval($solution->reputation);
    $solution->subject_id = intval($solution->subject_id);
    $solution->year = intval($solution->year);
    $solution->level = intval($solution->level);

    unset($solution->password);

    return $solution;
}
