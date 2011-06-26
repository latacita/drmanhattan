#!/bin/sh
#Si hay una instancia de la aplicacion ejecutandose no se inicia
proceso=$(ps -ef | grep drManhattanAlumno.jar | grep -v grep | awk '{print $2}')
java -jar /usr/bin/drManhattanAlumno.jar $proceso
