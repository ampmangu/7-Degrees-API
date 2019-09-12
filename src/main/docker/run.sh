#!/usr/bin/env bash
TMP_KEY="`cat ~/.gradle/gradle.properties`"
MOVIE_DB_API_KEY="$(cut -d'=' -f2 <<<$TMP_KEY)"
export MOVIE_DB_API_KEY=$MOVIE_DB_API_KEY
docker-compose up -d