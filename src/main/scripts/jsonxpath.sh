#!/bin/bash
java -cp 'target/dependency/*:target/jsonxml-1.0-SNAPSHOT.jar' com.semsaas.jsonxml.tools.JsonXpath "$@"

