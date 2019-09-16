#!/usr/bin/env bash
TMP_KEY="`cat ~/.gradle/gradle.properties`"
KEY="$(cut -d'=' -f2 <<<$TMP_KEY)"
export MOVIE_DB_API_KEY=$KEY
cat > .env <<EOF
MOVIE_DB_API_KEY=$MOVIE_DB_API_KEY
EOF
docker-compose up -d