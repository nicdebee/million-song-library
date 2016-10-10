#!/usr/bin/env bash

PURPLE='\033[0;35m'
RED='\033[0;31m'
GREEN='\033[1;36m'
NC='\033[0m' # no color

CASSANDRA=0
BUILD_SERVER=0
START_DOCKER_MACHINE=1

while [[ $# > 0 ]]; do
    key="$1"
    case $key in
        -c|--cassandra)
        BUILD_SERVER=1
        CASSANDRA=0
        ;;
        -m|--machine)
        START_DOCKER_MACHINE=0
        ;;
        -s|--server)
        CASSANDRA=1
        BUILD_SERVER=0
        ;;
        \0|-d|--default)
        CASSANDRA=0
        BUILD_SERVER=0
        START_DOCKER_MACHINE=1
        ;;
        *)
        echo -e "\n\n${GREEN}No valid params provided";
        echo -e "${GREEN}-c|--cassandra ................................... build only cassandra image and container "
        echo -e "${GREEN}-s|--server ...................................... build only server image and container "
        echo -e "${GREEN}-m|--machine ...................................... docker machine startup (skipped by default) "
        echo -e "${GREEN}-d|--default ...................................... build everything${NC}"
        exit 1;
        ;;
    esac
shift
done

function updateHostFile {
  matches=$(cat /etc/hosts | grep -c 'msl.kenzanlabs.com')
  if [[ ${matches} -gt 1 ]]; then
    echo -e "${GREEN}\nPlease update /etc/hosts file with $(docker-machine ip dev) msl.kenzanlabs.com\n${NC}"
  elif [[  ${matches} -eq 1 ]]; then
    echo -e "\n${PURPLE}Attempting to edit host file${NC}\n"
    sed -i.bak -e 's/([0-9]{1,3}.?){4}\s+msl.kenzanlabs.com/$(docker-machine ip dev)  msl.kenzanlabs.com/g' /etc/hosts
  elif [[  ${matches} -eq 0 ]]; then
    echo -e "\n${PURPLE}Attempting to edit host file${NC}\n"
    sudo echo "$(docker-machine ip dev) msl.kenzanlabs.com" >> /etc/hosts
  fi
}

function progressAnimation {
  timeout=$1
  message=$2
  spin='-\|/'
  i=0
  while [[ ${timeout} -gt 0 ]]; do
    i=$(( (i+1) %4 ))
    printf "\r${message} ................... [${spin:$i:1}]"
    timeout=$((timeout - 1))
    sleep 1s
  done
}

function startUpDockerMachine {
  docker-machine status dev
  if [[ $? -ne 0 ]]; then
    echo -e "\n${PURPLE}Creating dev machine${NC}"
    docker-machine create --driver virtualbox --virtualbox-memory 4000 dev
  fi

  docker-machine env dev
  if [[ $? -ne 0 ]]; then
    docker-machine start dev
    docker-machine env dev
  fi
  eval $(docker-machine env dev)
}

# SETUP CASSANDRA
function cassandraSetup {
  # Create the image
  docker images | grep 'msl/cassandra'
  if [[ $? -eq 0 ]]; then
    echo -e "\n${PURPLE}msl/cassandra image exists${NC}"
  else
    echo -e "\n${PURPLE}Building msl/cassandra image${NC}"
    docker build -t msl/cassandra -f cassandra.dockerfile .
  fi

  # Start the container
  docker ps -a | grep 'msl/cassandra'
  if [[ $? -eq 0 ]]; then
   docker stop msl-cassandra && docker rm msl-cassandra
  fi
  docker run \
        -d \
        --name msl-cassandra \
        -p 7000-7001:7000-7001 \
        -p 9042:9042 \
        -p 7199:7199 \
        -p 9160:9160 \
        msl/cassandra

  # Start data upload
  echo -e "\n${PURPLE}Uploading csv data into msl/cassandra container${NC}"
  RETRIES=0
  docker exec -it msl-cassandra bash setup.sh -y -v -c $(which cassandra)
  while [[ $? -ne 0 && ${RETRIES} -lt 5 ]]; do
    progressAnimation 10 "Attempting to load csv files"
    RETRIES=$((RETRIES + 1))
    docker exec -it msl-cassandra bash setup.sh -y -v -c $(which cassandra)
  done
  if [[ $? -ne 0 ]]; then echo -e "\n${RED}Error: unable to load csv data into DB${NC}" && exit 1; fi
}

# SETUP NODE & SERVER
function setupServer {
  # Create the image
  docker images | grep 'msl/node-server'
  if [[ $? -eq 0 ]]; then
    echo -e "\n${PURPLE}msl/node-server image already exists${NC}"
  else
   # This will take some time - about < 1 hour
   echo -e "\n${PURPLE}Building msl/node-server image${NC}"
   echo -e "\n${PURPLE}ETA: ~1hour${NC}"
   docker build -t msl/node-server -f Dockerfile .
  fi

  # Start the container
  docker ps -a | grep 'msl/node-server'
  if [[ $? -eq 0 ]]; then
   docker stop msl-node-server && docker rm msl-node-server
  fi
  docker run \
        -d -p 3000:3000 \
        -p 3002:3002 \
        -p 3003:3003 \
        -p 3004:3004 \
        -p 9004:9004 \
        -p 9003:9003 \
        -p 9002:9002 \
        -p 9001:9001 \
        --name msl-node-server \
        --net=host -ti \
        --entrypoint=/bin/bash \
        msl/node-server                       #image to run

  # Start FE
  echo -e "\n${PURPLE}Starting Server and View${NC}"
  docker exec \
     -d \
     msl-node-server \
     bash -c "bash npm rebuild node-sass && npm run deploy"

  progressAnimation 30 "Starting FE and BE, please wait"

  # Start servers
  echo -e "\n${PURPLE}Starting Servers${NC}"
  docker exec -it msl-node-server npm run serve-all

}

function init {

  cd ..

  updateHostFile

  if [[ ${START_DOCKER_MACHINE} -eq 0 ]]; then
    startUpDockerMachine
  fi

  if [[ ${CASSANDRA} -eq 0 ]]; then
    cassandraSetup
  fi

  if [[ ${BUILD_SERVER} -eq 0 ]]; then
    setupServer
  fi
}

init


exit 0;