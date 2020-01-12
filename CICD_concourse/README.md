# CICD_concourse

software maintenance and evolution project 

### HOW TO SET UP?
1. sudo docker-compose up -d <br> deploy the docker (if you don't have docker-compose you should install -> "sudo apt install docker-compose") <br> 
2. install fly from localhost </br> (the main page of concourse on your localhost) <br> CLI to interact with concourse <br>
3. fly --target conc login --team-name main --concourse-url http://localhost:8080 \
   fly --target (name of instance) login --team-name main --concourse-url (server for deploying) <br>
4. press on the link and input the credentials (default test/test) </br>
5. fly -t conc set-pipeline -c pipeline.yml -p firstpipeline \
   fly -t (name of instance) set-pipeline -c (yml file) -p (name of pipeline)	
