#!/usr/bin/env bash
db="psql -U $1 -d planshboard"
encoded_test123=\$2a\$12\$8GqAT8puP14z6XH6t8i.reJhrlSgoVLJRpwHrjR7XBahffJkzsyZW
id1=2137
id2=66669

echo "Inserting users..."
echo "INSERT INTO users(id, name, password) VALUES
($id1, 'kuba', '$encoded_test123'), ($id2, 'jgruda', '$encoded_test123')" | $db
echo "Users inserted\n"

echo "Inserting board games..."
echo "INSERT INTO boardgames(name, user_id) VALUES
('Pojedynek', $id1), ('Agricola', $id1), ('Wsiąść do pociągu', $id1), ('Łotry', $id1), ('Zażółć gęślą jaźń', $id1),
('.', $id1), ('Każdym dniem zabijam to - deszczem obcych rąk zmywam ślad, zabijam to', $id1), ('Agricola', $id2)" | $db
echo "Board games inserted\n"

echo "Inserting opponents..."
echo "INSERT INTO opponents(user_id, name) VALUES
 ($id1, 'rywal1'), ($id1, 'Straszny'), ($id1, 'Łąóżźć'), ($id1, '.'), ($id1, 'Would you capture it or just let it slip?'),
 ($id2, 'marianek')" | $db
echo "Opponents inserted\n"
