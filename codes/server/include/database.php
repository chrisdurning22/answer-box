<?php

/*
 * This code was written by following a YouTube tutorial: https://www.youtube.com/playlist?list=PLfdtiltiRHWF5Rhuk7k4UAU1_yLAZzhWc
*/

class Database {
    private static $instance = null;
    private $pdo;
    private $query;
    private $error = false;
    private $results;
    private $count = 0;

    public function __construct() {
        $address = ($_SERVER['SERVER_ADDR'] == '188.166.171.227') ? 'localhost' : '188.166.171.227';
        $dsn = 'mysql:host=' . '188.166.171.227' . ';dbname=answer_box';
        $user = 'exam_helper';
        $password = '6hnzFqsjLLrhjLuf';

        try {
            $this->pdo = new PDO($dsn, $user, $password);
        } catch (PDOEexception $e) {
            die($e->getMessage());
        }
    }

    public static function getInstance() {
        if (!isset(self::$instance)) {
            self::$instance = new Database();
        }
        return self::$instance;
    }

    public function query($sql, $params = []) {
        $this->error = false;

        if ($this->query = $this->pdo->prepare($sql)) {
            if (count($params)) {
                $i = 1;
                foreach ($params as $param) {
                    $this->query->bindValue($i++, $param);
                }
            }

            if ($this->query->execute()) {
                $this->results = $this->query->fetchAll(PDO::FETCH_OBJ);
                $this->count = $this->query->rowCount();
            } else {
                $this->error = true;
            }
        }
        return $this;
    }

    public function action($action, $table, $where = []) {
        $operators = ['=', '>', '<', '>=', '<='];
        $field = $where[0];
        $operator = $where[1];
        $value = $where[2];

        if (in_array($operator, $operators)) {
            $sql = "{$action} FROM {$table}  WHERE {$field} {$operator} ?";

            if (!$this->query($sql, [$value])->error()) {
                return $this;
            }
        }
    }

    public function get($table, $where) {
        return $this->action('SELECT *', $table, $where);
    }

    public function first() {
        return $this->results[0];
    }

    public function delete($table, $where) {
        return $this->action('DELETE', $table, $where);
    }

    public function insert($table, $fields = []) {
        $keys = array_keys($fields);
        $values = '';
        $i = 1;

        foreach($fields as $field) {
            $values .= '?';
            if ($i++ < count($fields)) {
                $values .= ', ';
            }
        }

        $sql = "INSERT INTO {$table} (`" . implode('`, `', $keys) . "`) VALUES ({$values})";

        return $this->query($sql, $fields)->error();
    }

    public function update($table, $id, $fields) {
        $set = '';
        $i = 1;

        foreach ($fields as $key => $value) {
            $set .= "{$key} = ?";
            if ($i++ < count($fields)) {
                $set .= ', ';
            }
        }

        $sql = "UPDATE {$table} SET {$set} WHERE id = {$id}";

        return $this->query($sql, $fields)->error();
    }

    public function results() {
        return $this->results;
    }

    public function error() {
        return $this->error;
    }

    public function count() {
        return $this->count;
    }
}
