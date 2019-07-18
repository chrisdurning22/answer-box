<?php

if (!isset($_GET['year']) || !isset($_GET['subject']) || !isset($_GET['level'])) {
    respond('bad_request');
}

checkSession();

$user = Database::getInstance()->get('users', ['session', '=', $_GET['session']])->first();

$year = $_GET['year'];
$subject = $_GET['subject'];
$level = $_GET['level'];

$sql = 'SELECT * FROM solutions
        WHERE year = ? AND subject_id = (
            SELECT id FROM subjects
            WHERE name = ?
        ) AND level = ?
        ORDER BY reputation DESC';

$solutions = Database::getInstance()->query($sql, [$year, $subject, $level])->results();
$response['answers'] = [];

for ($i = 0; $i < count($solutions); $i++) {
    $solution = $solutions[$i];

    // Add reputation
    $sql = 'SELECT COALESCE(sum(value), 0) AS value
            FROM votes
            WHERE solution_id = ?';
    $solution->reputation = Database::getInstance()->query($sql, [$solution->id])->first()->value;

    $solution = cleanSolution($solution);
    $solution->files = [];

    // Add voters
    $sql = 'SELECT value
            FROM votes
            WHERE solution_id = ? and user_id = ?';
    $votes = Database::getInstance()->query($sql, [$solution->id, $user->id]);
    if ($votes->count() > 0) {
        $solution->vote = intval($votes->first()->value);
    } else {
        $solution->vote = 0;
    }

    // Add files
    $solutions_files = Database::getInstance()->get('solutions_files', ['solution_id', '=', $solution->id])->results();
    foreach ($solutions_files as $solution_file) {
        $file = Database::getInstance()->get('files', ['id', '=', $solution_file->file_id])->first();
        $solution->files[] = $file;
    }

    // Add comments
    $comments = Database::getInstance()->get('comments', ['solution_id', '=', $solution->id])->results();
    foreach ($comments as $comment) {
        $comment = cleanComment($comment);
        $commentUser = Database::getInstance()->get('users', ['id', '=', $comment->user_id])->first();
        $comment->user = cleanUser($commentUser, true);
    }

    $user = Database::getInstance()->get('users', ['id', '=', $solution->user_id])->first();

    $solution->comments = $comments;
    $solution->user = cleanUser($user, true);

    $response['answers'][] = $solution;
}

// Sort by reputation
for ($i = 0; $i < count($response['answers']); $i++) {
    for ($j = 0; $j < count($response['answers']) - 1; $j++) {
        if ($response['answers'][$j]->reputation < $response['answers'][$j + 1]->reputation) {
            $temp = $response['answers'][$j + 1];
            $response['answers'][$j + 1] = $response['answers'][$j];
            $response['answers'][$j] = $temp;
        }
    }
}

respond();
