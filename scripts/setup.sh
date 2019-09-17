#!/usr/bin/env bash

echo "=== SETUP START ==="
echo
echo

BASE_URL="http://localhost:8080"
AUTH_HEADER="Authorization"
USERNAME="grudus"
PASSWORD="test123"

OPPONENT_NAMES=("Madzia" "Tomek" "Atomek")
OPPONENT_IDS=()
BOARD_GAME_NAMES=("Agricola" "Osadnicy" "Pędzące Żółwie" "Cywilizacja poprzez wieki" "Pędzące ślimaki" "Jaipur" "Patchwork" "SkipBo" "Splendor" "Pojedynek" "Carcassone" "Wsiąść do pociągu" "Rummikub" "Dobble" "Domek" "Scrabble")
BOARD_GAME_IDS=()


create_user() {
   curl -s -H "Content-Type: application/json" -d "{\"username\":\"$1\",\"password\":\"$2\"}" "$BASE_URL/api/auth/register"
}

login() {
    curl --silent --output /dev/null -i -s -X POST -d "username=$1&password=$2" -c - "$BASE_URL/api/auth/login" | grep HttpOnly | awk '{print $7}'
}

authorized_post_request() {
    curl -s -H "Content-Type: application/json" --cookie "$AUTH_HEADER=$3" -d "$2" "$BASE_URL$1"
}

authorized_get_request() {
    curl -s -H "Content-Type: application/json" --cookie "$AUTH_HEADER=$3" "$BASE_URL$1"
}

json_val() {
    python3 -c "import sys, json; print(json.load(sys.stdin)['$1'])"
}

add_opponents() {
    for opponent in "${OPPONENT_NAMES[@]}"; do
        ID=$(authorized_post_request "/api/opponents" "{\"name\":\"$opponent\"}" "$1" | json_val 'id')
        OPPONENT_IDS+=(${ID})
    done
}


add_board_games() {
    for game in "${BOARD_GAME_NAMES[@]}"; do
        ID=$(authorized_post_request "/api/board-games" "{\"name\":\"${game}\"}" "$1" | json_val 'id')
        BOARD_GAME_IDS+=(${ID})
    done
}

add_play() {
    POINTS_1=$(( ( RANDOM % 200 )  + 1 ))
    POINTS_2=$(( ( RANDOM % 200 )  + 1 ))
    POINTS_3=$(( ( RANDOM % 200 )  + 1 ))
    POSITION_1=$(( ( RANDOM % 3 )  + 1 ))
    POSITION_2=$(( ( RANDOM % 3 )  + 1 ))
    POSITION_3=$(( ( RANDOM % 3 )  + 1 ))
    MONTH=$(printf "%02d" $(( (RANDOM % 12)  + 1 )))
    DAY=$(printf "%02d" $(( ( RANDOM % 28 )  + 1 )))
    HOURS=$(printf "%02d" $(( ( RANDOM % 23 )  + 1 )))
    MINUTES=$(printf "%02d" $(( ( RANDOM % 59 )  + 1 )))
    JSON="{\"results\":[{\"opponentName\":\"${OPPONENT_NAMES[0]}\",\"opponentId\":${OPPONENT_IDS[0]},\"position\":${POSITION_1},\"points\":$POINTS_1},{\"opponentName\":\"${OPPONENT_NAMES[1]}\",\"opponentId\":${OPPONENT_IDS[1]},\"position\":${POSITION_3},\"points\":$POINTS_3},{\"opponentName\":\"${OPPONENT_NAMES[2]}\",\"opponentId\":${OPPONENT_IDS[2]},\"position\":${POSITION_2},\"points\":$POINTS_2}],\"date\":\"2018-${MONTH}-${DAY}T${HOURS}:${MINUTES}:44\",\"note\":\"Note\"}"
    authorized_post_request "/api/board-games/$1/plays" "${JSON}" "$2" | json_val 'id'
}

echo "Creating user $USERNAME ..."
create_user ${USERNAME} ${PASSWORD}
echo -e "User created\n"

echo "Login ..."
AUTH_COOKIE=$(login ${USERNAME} ${PASSWORD})
echo -e "$AUTH_COOKIE"

echo "Adding opponents ..."
add_opponents "${AUTH_COOKIE}"
echo -e "Opponents added"

echo "Adding board games ..."
add_board_games "${AUTH_COOKIE}"
echo -e "Board games added"

for i in "${BOARD_GAME_IDS[@]}"; do
    PLAYS=$((RANDOM % 10))
    echo "Adding $PLAYS plays to boardgame $i ..."
    for p in $(seq 0 ${PLAYS}); do
        add_play "${i}" "${AUTH_COOKIE}"
    done
    echo -e "Plays added"
done




echo
echo
echo "=== THE END ==="
