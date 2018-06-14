Sample Flask app packed in docker container.
Built with Jenkins pipeline job and pushed to dockerhub.

Containerized Jenkins uses host network on port 8080.
To make Jenkins visible for other network participants, use: `sudo iptables -I INPUT 5 -p tcp -m tcp --dport 8080 -j ACCEPT` 
