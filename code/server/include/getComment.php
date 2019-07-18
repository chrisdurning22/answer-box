<?php

if (!isset($_GET['id'])) {
    respond('bad_request');
}

checkSession();

$id = $_GET['id'];

$sql = "SELECT *
		FROM comments
		WHERE id = (
			SELECT MAX(id)
			FROM comments
		)";

$comment = Database::getInstance()->query($sql)->first();

$response['comment'] = cleanComment($comment);

respond();