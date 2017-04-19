#!/bin/bash

scriptpath=$(cd $(dirname $0) ; pwd -P)
swagger_path=$(cd "$scriptpath/../" ; pwd -P) 
# echo $swagger_path

skip_tests=-DskipTests

while [[ $# -gt 1 ]]
do
key="$1"

case $key in
    -e|--env)
    environment="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

mvn clean package $skip_tests
rm -rf $swagger_path/wpm/output 
<<<<<<< HEAD
java -jar $swagger_path/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate -i $swagger_path/wpm/swagger-api.json -l javascript-angular-js -o $swagger_path/wpm/output
=======
java -jar $swagger_path/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate -i $swagger_path/wpm/kparamsothy-PHN-1.0.0-swagger.json -l javascript-angular-js -o $swagger_path/wpm/output
>>>>>>> 03cc4d3b3fed3ec9aeb0985d26833235eb9adc86
