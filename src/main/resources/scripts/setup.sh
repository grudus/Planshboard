#!/usr/bin/env bash
db="mysql -u $1 -p$2 planshboard"
encoded_test123=\$2a\$12\$8GqAT8puP14z6XH6t8i.reJhrlSgoVLJRpwHrjR7XBahffJkzsyZW

echo "Inserting users..."
echo "INSERT INTO users(name, password) VALUES
('kuba', '$encoded_test123'), ('jgruda', '$encoded_test123')" | $db
echo "Users inserted"

echo "Adding games..."
echo "INSERT INTO boardgames(name, min_players, max_players, average_playing_time) VALUES
('Agricola', 1, 4, 45), ('Pedzace zolwie', 2, 5, 20)" | $db
echo "Games added"