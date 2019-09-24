#!/bin/sh

echo "The application will start in 5s..." && sleep 5
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Dspring.profiles.active=prod -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.ampmangu.degrees.Application"  "$@"
